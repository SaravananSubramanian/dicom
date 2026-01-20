package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

/**
 * DICOM C-FIND Service Demo
 *
 * PREREQUISITES: Uses public DICOM server (www.dicomserver.co.uk:104).
 * Alternatively, configure to use local Orthanc (localhost:4242, AE: ORTHANC).
 * Public server may have limited availability; local Orthanc recommended.
 *
 * This example demonstrates how to query a remote DICOM server using
 * the C-FIND service to search for studies, series, or instances.
 *
 * Key concepts:
 * - C-FIND is used to query for DICOM objects based on search criteria
 * - Query/Retrieve Information Models:
 *   - Patient Root: Query hierarchy starts at patient level
 *   - Study Root: Query hierarchy starts at study level
 * - Query Retrieve Levels: PATIENT, STUDY, SERIES, IMAGE
 * - Identifier: Specifies both search criteria and attributes to return
 * - Wildcard matching: Use '*' for partial matches (e.g., "Smith*")
 *
 * C-FIND Workflow:
 * 1. Build identifier with query level, search criteria, and return keys
 * 2. Establish association with Query/Retrieve SCP
 * 3. Send C-FIND request with identifier
 * 4. Receive zero or more C-FIND responses (one per match)
 * 5. Handler processes each matching result
 *
 * Note: Empty attributes in the identifier act as "return keys" -
 * they request the server to return those values for matches.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomCFindFunctionalityDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM C-FIND Service Demo ===\n");

		try {

			// === Configuration ===
			String remoteHost = "www.dicomserver.co.uk";
			int remotePort = 104;
			String calledAETitle = "MEDCONNEC";
			String callingAETitle = "OURCLIENT";

			// Search criteria
			String patientNamePattern = "Bowen*";  // Wildcard search

			System.out.println("Query Server: " + remoteHost + ":" + remotePort);
			System.out.println("Called AE Title: " + calledAETitle);
			System.out.println("Search Pattern: Patient Name = " + patientNamePattern);
			System.out.println();

			// === Build the Query Identifier ===
			// The identifier specifies:
			// 1. Query level (PATIENT, STUDY, SERIES, or IMAGE)
			// 2. Search criteria (attributes with values)
			// 3. Return keys (empty attributes to be filled by server)

			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
			AttributeList identifier = new AttributeList();

			// Query level - we're querying at the STUDY level
			identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY");

			// Search criteria - find patients matching this name pattern
			identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet).addValue(patientNamePattern);

			// Return keys - server will fill these in for each match
			identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientBirthDate);
			identifier.putNewAttribute(TagFromName.PatientSex);
			identifier.putNewAttribute(TagFromName.StudyInstanceUID);
			identifier.putNewAttribute(TagFromName.SOPInstanceUID);
			identifier.putNewAttribute(TagFromName.StudyDescription);
			identifier.putNewAttribute(TagFromName.StudyDate);

			System.out.println("Identifier built with " + identifier.size() + " attributes");
			System.out.println("Query Level: STUDY");
			System.out.println("\nExecuting C-FIND query...\n");

			// === Perform C-FIND Query ===
			// Using Study Root Query/Retrieve Information Model
			// The IdentifierHandler will be called for each matching result
			new FindSOPClassSCU(
				remoteHost,
				remotePort,
				calledAETitle,
				callingAETitle,
				SOPClass.StudyRootQueryRetrieveInformationModelFind,
				identifier,
				new IdentifierHandler()  // Default handler prints to console
			);

			System.out.println("\n=== C-FIND query completed ===");

		} catch (Exception e) {
			System.err.println("\nC-FIND query failed!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nCommon causes:");
			System.err.println("  - Query server not running or unreachable");
			System.err.println("  - Wrong host/port/AE Title configuration");
			System.err.println("  - Invalid query level or identifier");
			System.err.println("  - No matching results found");
			e.printStackTrace();
		}
	}
}
