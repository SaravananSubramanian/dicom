package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.ReceivedObjectHandler;

/**
 * DICOM C-GET Service Demo
 *
 * PREREQUISITES: Uses public DICOM server (www.dicomserver.co.uk:104).
 * Output directory 'c:\RetrievedDicomData' must exist. Not all servers support C-GET.
 * Alternatively, use local Orthanc with C-GET enabled in configuration.
 *
 * This example demonstrates how to retrieve DICOM objects using C-GET,
 * which pulls data directly over the existing association.
 *
 * Key concepts:
 * - C-GET retrieves DICOM objects over the same association (no separate SCP needed)
 * - The SCU must support Storage SCP role to receive objects
 * - Workflow: C-FIND to locate studies, then C-GET to retrieve them
 * - SOPClassesInStudy helps negotiate presentation contexts efficiently
 *
 * C-GET vs C-MOVE:
 * - C-GET: Objects sent back on same association (simpler setup)
 * - C-MOVE: Objects sent to a separate Storage SCP (more flexible)
 * - C-GET requires role reversal (SCU becomes Storage SCP temporarily)
 * - C-MOVE works through firewalls better (no incoming connection needed)
 *
 * Workflow in this demo:
 * 1. Perform C-FIND to locate studies for a patient
 * 2. Extract SOPClassesInStudy to know what presentation contexts to negotiate
 * 3. For each matching study, issue C-GET request
 * 4. Receive DICOM objects via the ReceivedObjectHandler callback
 * 5. Save received files to local storage
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomCGetFunctionalityDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM C-GET Service Demo ===\n");

		try {

			// === Configuration ===
			String remoteHost = "www.dicomserver.co.uk";
			int remotePort = 104;
			String calledAETitle = "MEDCONN";
			String callingAETitle = "OurFindScu";
			String patientNamePattern = "Bowen*";

			System.out.println("Server: " + remoteHost + ":" + remotePort);
			System.out.println("Search Pattern: Patient Name = " + patientNamePattern);
			System.out.println();
			System.out.println("Step 1: Performing C-FIND to locate studies...\n");

			// === Build C-FIND Identifier ===
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

			// Request SOPClassesInStudy - helps optimize C-GET negotiation
			identifier.putNewAttribute(TagFromName.SOPClassesInStudy);

			// === Perform C-FIND ===
			// The handler will trigger C-GET for each matching study
			new FindSOPClassSCU(
				remoteHost,
				remotePort,
				calledAETitle,
				callingAETitle,
				SOPClass.StudyRootQueryRetrieveInformationModelFind,
				identifier,
				new CGetFindResultHandler()
			);

			System.out.println("\n=== C-GET operation completed ===");

		} catch (Exception e) {
			System.err.println("\nC-GET operation failed!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nCommon causes:");
			System.err.println("  - Server doesn't support C-GET");
			System.err.println("  - SOP Class not supported");
			System.err.println("  - Network or firewall issues");
			e.printStackTrace();
		}
	}
}

/**
 * Handler for C-FIND results that triggers C-GET retrieval
 *
 * For each matching study found, this handler:
 * 1. Extracts the Study Instance UID
 * 2. Determines which SOP Classes are in the study
 * 3. Issues a C-GET request to retrieve all objects
 */
class CGetFindResultHandler extends IdentifierHandler {

	// === C-GET Configuration ===
	private static final String GET_SCP_ADDRESS = "www.dicomserver.co.uk";
	private static final String GET_SCP_AE_TITLE = "MEDCONN";
	private static final int GET_SCP_PORT = 104;
	private static final String GET_SCU_AE_TITLE = "JavaClient";
	private static final File STORAGE_PATH = new File("c:\\RetrievedDicomData");

	private int studyCount = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {

		studyCount++;
		System.out.println("--- Found Study #" + studyCount + " ---");

		// === Extract Study Information ===
		String studyInstanceUID = attributeListForFindResult
			.get(TagFromName.StudyInstanceUID)
			.getSingleStringValueOrEmptyString();

		System.out.println("  Study Instance UID: " + studyInstanceUID);

		// === Determine SOP Classes in Study ===
		// This helps us negotiate only the presentation contexts we need
		Set<String> sopClassesExpected = new HashSet<>();

		Attribute sopClassesInStudy = attributeListForFindResult.get(TagFromName.SOPClassesInStudy);
		if (sopClassesInStudy != null) {
			String[] sopClassList = sopClassesInStudy.getStringValues();
			if (sopClassList != null) {
				for (String sopClass : sopClassList) {
					sopClassesExpected.add(sopClass);
				}
				System.out.println("  SOP Classes in Study: " + sopClassesExpected.size());
			}
		}

		// If SOPClassesInStudy not available, use all storage SOP classes
		if (sopClassesExpected.isEmpty()) {
			sopClassesExpected = (Set<String>) SOPClass.getSetOfStorageSOPClasses();
			System.out.println("  Using all storage SOP classes (SOPClassesInStudy not available)");
		}

		// === Perform C-GET ===
		System.out.println("\nStep 2: Issuing C-GET request for study...\n");

		try {
			// Build C-GET identifier
			AttributeList getIdentifier = new AttributeList();

			AttributeTag levelTag = TagFromName.QueryRetrieveLevel;
			Attribute levelAttr = new CodeStringAttribute(levelTag);
			levelAttr.addValue("STUDY");
			getIdentifier.put(levelTag, levelAttr);

			AttributeTag uidTag = TagFromName.StudyInstanceUID;
			Attribute uidAttr = new UniqueIdentifierAttribute(uidTag);
			uidAttr.addValue(studyInstanceUID);
			getIdentifier.put(uidTag, uidAttr);

			// Execute C-GET
			new GetSOPClassSCU(
				GET_SCP_ADDRESS,
				GET_SCP_PORT,
				GET_SCP_AE_TITLE,
				GET_SCU_AE_TITLE,
				SOPClass.StudyRootQueryRetrieveInformationModelGet,
				getIdentifier,
				new IdentifierHandler(),  // For C-GET sub-operation status
				STORAGE_PATH,
				StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER,
				new CGetReceivedObjectHandler(),
				sopClassesExpected,
				0,     // Compression level
				true,  // Implicit VR Little Endian
				false, // Explicit VR Little Endian
				false  // Explicit VR Big Endian
			);

		} catch (Exception e) {
			System.err.println("Error during C-GET operation: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

/**
 * Handler for received DICOM objects during C-GET
 *
 * Called each time a DICOM object is received and stored.
 */
class CGetReceivedObjectHandler extends ReceivedObjectHandler {

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
