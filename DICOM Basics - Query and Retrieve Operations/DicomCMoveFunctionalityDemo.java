package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import java.io.IOException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.MoveSOPClassSCU;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;

/**
 * DICOM C-MOVE Service Demo
 *
 * PREREQUISITES: (1) Orthanc server on localhost:4242 with studies to retrieve.
 * (2) Our AE Title 'JavaStoreScp' must be registered in Orthanc's DicomModalities.
 * (3) Output directory 'c:\RetrievedDicomData' must exist.
 * Add to orthanc.json: "DicomModalities": { "JavaStoreScp": ["JavaStoreScp", "localhost", 11112] }
 *
 * This example demonstrates how to retrieve DICOM objects using C-MOVE,
 * which instructs the server to send data to a separate Storage SCP.
 *
 * Key concepts:
 * - C-MOVE requests the server to send objects to a specified destination
 * - Requires running a Storage SCP to receive the transferred objects
 * - The move destination AE Title must be configured on the server
 * - Three parties involved: Move SCU, Move SCP (source), Store SCP (destination)
 *
 * C-MOVE vs C-GET:
 * - C-MOVE: Server pushes to a Storage SCP (may require firewall config)
 * - C-GET: Objects pulled over same association (simpler but less common)
 * - C-MOVE is more widely supported by PACS systems
 * - C-MOVE allows sending to any configured destination
 *
 * Workflow in this demo:
 * 1. Start a local Storage SCP to receive incoming objects
 * 2. Perform C-FIND to locate studies matching criteria
 * 3. For each study found, issue C-MOVE request
 * 4. Move SCP sends objects to our Storage SCP
 * 5. ReceivedObjectHandler processes each received object
 *
 * Note: The destination AE Title must be registered with the Move SCP
 * (e.g., in Orthanc's DicomModalities configuration).
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomCMoveFunctionalityDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM C-MOVE Service Demo ===\n");

		try {

			// === Storage SCP Configuration ===
			// We need to run a Storage SCP to receive the moved objects
			String storageScpAeTitle = "JavaStoreScp";
			int storageScpPort = 11112;
			File storagePath = new File("c:\\RetrievedDicomData");

			// === Move SCP Configuration ===
			String moveScpHost = "127.0.0.1";
			int moveScpPort = 104;
			String moveScpAeTitle = "ORTHANC";
			String callingAeTitle = "OurFindScu";

			// === Search Criteria ===
			String patientNamePattern = "ALLISON*";

			System.out.println("Storage SCP: " + storageScpAeTitle + " on port " + storageScpPort);
			System.out.println("Move SCP: " + moveScpHost + ":" + moveScpPort + " (" + moveScpAeTitle + ")");
			System.out.println("Search Pattern: Patient Name = " + patientNamePattern);
			System.out.println();

			// === Step 1: Start Storage SCP ===
			System.out.println("Step 1: Starting local Storage SCP to receive objects...");

			StorageSOPClassSCPDispatcher storageScpDispatcher = new StorageSOPClassSCPDispatcher(
				storageScpPort,
				storageScpAeTitle,
				storagePath,
				StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER,
				new CMoveReceivedObjectHandler()
			);

			Thread scpThread = new Thread(storageScpDispatcher);
			scpThread.start();

			System.out.println("Storage SCP started on port " + storageScpPort + "\n");

			// === Step 2: Perform C-FIND ===
			System.out.println("Step 2: Performing C-FIND to locate studies...\n");

			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
			AttributeList identifier = new AttributeList();

			identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY");
			identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet).addValue(patientNamePattern);
			identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientBirthDate);
			identifier.putNewAttribute(TagFromName.PatientSex);
			identifier.putNewAttribute(TagFromName.StudyInstanceUID);
			identifier.putNewAttribute(TagFromName.SOPInstanceUID);
			identifier.putNewAttribute(TagFromName.StudyDescription);
			identifier.putNewAttribute(TagFromName.StudyDate);

			// === Execute C-FIND ===
			// The handler will trigger C-MOVE for each matching study
			new FindSOPClassSCU(
				moveScpHost,
				moveScpPort,
				moveScpAeTitle,
				callingAeTitle,
				SOPClass.StudyRootQueryRetrieveInformationModelFind,
				identifier,
				new CMoveFindResultHandler()
			);

			System.out.println("\n=== C-MOVE operation completed ===");
			System.out.println("Files saved to: " + storagePath.getAbsolutePath());

		} catch (Exception e) {
			System.err.println("\nC-MOVE operation failed!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nCommon causes:");
			System.err.println("  - Move SCP doesn't know our Storage SCP AE Title");
			System.err.println("  - Firewall blocking incoming Storage SCP connection");
			System.err.println("  - Storage SCP port already in use");
			e.printStackTrace();
		}
	}
}

/**
 * Handler for C-FIND results that triggers C-MOVE retrieval
 */
class CMoveFindResultHandler extends IdentifierHandler {

	// === C-MOVE Configuration ===
	private static final String MOVE_SCP_ADDRESS = "127.0.0.1";
	private static final String MOVE_SCP_AE_TITLE = "ORTHANC";
	private static final int MOVE_SCP_PORT = 104;
	private static final String MOVE_SCU_AE_TITLE = "JavaMoveScu";

	// Destination for moved objects (our Storage SCP)
	private static final String STORAGE_SCP_AE_TITLE = "JavaStoreScp";

	private int studyCount = 0;

	@Override
	public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {

		studyCount++;
		System.out.println("--- Found Study #" + studyCount + " ---");

		// === Extract Study Instance UID ===
		String studyInstanceUID = attributeListForFindResult
			.get(TagFromName.StudyInstanceUID)
			.getSingleStringValueOrEmptyString();

		String patientName = attributeListForFindResult
			.get(TagFromName.PatientName)
			.getSingleStringValueOrEmptyString();

		System.out.println("  Patient: " + patientName);
		System.out.println("  Study Instance UID: " + studyInstanceUID);

		// === Step 3: Perform C-MOVE ===
		System.out.println("\nStep 3: Issuing C-MOVE request...");
		System.out.println("  Destination: " + STORAGE_SCP_AE_TITLE + "\n");

		try {
			// Build C-MOVE identifier
			AttributeList moveIdentifier = new AttributeList();

			AttributeTag levelTag = TagFromName.QueryRetrieveLevel;
			Attribute levelAttr = new CodeStringAttribute(levelTag);
			levelAttr.addValue("STUDY");
			moveIdentifier.put(levelTag, levelAttr);

			AttributeTag uidTag = TagFromName.StudyInstanceUID;
			Attribute uidAttr = new UniqueIdentifierAttribute(uidTag);
			uidAttr.addValue(studyInstanceUID);
			moveIdentifier.put(uidTag, uidAttr);

			// Execute C-MOVE
			// The STORAGE_SCP_AE_TITLE tells the server where to send the objects
			new MoveSOPClassSCU(
				MOVE_SCP_ADDRESS,
				MOVE_SCP_PORT,
				MOVE_SCP_AE_TITLE,
				MOVE_SCU_AE_TITLE,
				STORAGE_SCP_AE_TITLE,  // Move destination
				SOPClass.StudyRootQueryRetrieveInformationModelMove,
				moveIdentifier
			);

		} catch (Exception e) {
			System.err.println("Error during C-MOVE operation: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

/**
 * Handler for received DICOM objects during C-MOVE
 *
 * Called by the Storage SCP each time an object is received.
 */
class CMoveReceivedObjectHandler extends ReceivedObjectHandler {

	private int objectCount = 0;

	@Override
	public void sendReceivedObjectIndication(
			String filename,
			String transferSyntax,
			String calledAeTitle) throws DicomNetworkException, DicomException, IOException {

		objectCount++;

		System.out.println("--- Received Object #" + objectCount + " ---");
		System.out.println("  From: " + calledAeTitle);
		System.out.println("  File: " + filename);
		System.out.println("  Transfer Syntax: " + transferSyntax);
		System.out.println();
	}
}
