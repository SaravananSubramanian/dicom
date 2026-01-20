package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

/**
 * DICOM C-FIND Service with Custom Identifier Handler Demo
 *
 * PREREQUISITES: Uses public DICOM server (www.dicomserver.co.uk:104).
 * Alternatively, configure to use local Orthanc (localhost:4242, AE: ORTHANC).
 *
 * This example demonstrates how to use a custom IdentifierHandler to
 * process C-FIND query results programmatically.
 *
 * Key concepts:
 * - IdentifierHandler: Callback interface for processing query results
 * - doSomethingWithIdentifier(): Called once for each matching result
 * - AttributeList: Contains the matched attributes for one result
 * - Custom handlers allow you to:
 *   - Extract specific values from results
 *   - Filter or transform results
 *   - Trigger subsequent operations (e.g., C-GET, C-MOVE)
 *   - Store results in a database or collection
 *
 * Why use a custom handler?
 * - The default IdentifierHandler just prints results to console
 * - Custom handlers let you integrate results into your application
 * - You can chain operations (find -> get/move)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomCFindFunctionalityWithCustomIdentifierHandlerDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM C-FIND with Custom Handler Demo ===\n");

		try {

			// === Configuration ===
			String remoteHost = "www.dicomserver.co.uk";
			int remotePort = 104;
			String calledAETitle = "MEDCONNEC";
			String callingAETitle = "OURCLIENT";
			String patientNamePattern = "Bowen*";

			System.out.println("Query Server: " + remoteHost + ":" + remotePort);
			System.out.println("Search Pattern: Patient Name = " + patientNamePattern);
			System.out.println();

			// === Build the Query Identifier ===
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
			AttributeList identifier = new AttributeList();

			// Query level
			identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY");

			// Search criteria
			identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet).addValue(patientNamePattern);

			// Return keys
			identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientBirthDate);
			identifier.putNewAttribute(TagFromName.PatientSex);
			identifier.putNewAttribute(TagFromName.StudyInstanceUID);
			identifier.putNewAttribute(TagFromName.SOPInstanceUID);
			identifier.putNewAttribute(TagFromName.StudyDescription);
			identifier.putNewAttribute(TagFromName.StudyDate);

			System.out.println("Executing C-FIND query with custom handler...\n");

			// === Perform C-FIND with Custom Handler ===
			// Our custom handler will process each result
			new FindSOPClassSCU(
				remoteHost,
				remotePort,
				calledAETitle,
				callingAETitle,
				SOPClass.StudyRootQueryRetrieveInformationModelFind,
				identifier,
				new CustomFindResultHandler()  // Our custom handler
			);

			System.out.println("\n=== C-FIND query completed ===");

		} catch (Exception e) {
			System.err.println("\nC-FIND query failed!");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

/**
 * Custom handler to process C-FIND query results
 *
 * This handler is called once for each matching result returned by the server.
 * You can extend this to store results, trigger retrieval operations, or
 * perform any other processing needed by your application.
 */
class CustomFindResultHandler extends IdentifierHandler {

	private int resultCount = 0;

	/**
	 * Process a single C-FIND result
	 *
	 * @param attributeListForFindResult The attributes for one matched result
	 * @throws DicomException if processing fails
	 */
	@Override
	public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {

		resultCount++;

		System.out.println("--- Result #" + resultCount + " ---");

		// Extract specific values from the result
		String studyInstanceUID = attributeListForFindResult
			.get(TagFromName.StudyInstanceUID)
			.getSingleStringValueOrEmptyString();

		String patientName = attributeListForFindResult
			.get(TagFromName.PatientName)
			.getSingleStringValueOrEmptyString();

		String studyDate = attributeListForFindResult
			.get(TagFromName.StudyDate)
			.getSingleStringValueOrEmptyString();

		String studyDescription = attributeListForFindResult
			.get(TagFromName.StudyDescription)
			.getSingleStringValueOrEmptyString();

		// Display extracted values
		System.out.println("  Patient Name: " + patientName);
		System.out.println("  Study Date: " + studyDate);
		System.out.println("  Study Description: " + studyDescription);
		System.out.println("  Study Instance UID: " + studyInstanceUID);
		System.out.println();

		// In a real application, you might:
		// - Store these values in a list or database
		// - Use the Study Instance UID to retrieve images (C-GET/C-MOVE)
		// - Update a user interface with the results
	}
}
