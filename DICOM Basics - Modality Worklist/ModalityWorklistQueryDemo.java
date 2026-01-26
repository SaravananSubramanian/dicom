package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.SequenceItem;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

/**
 * DICOM Modality Worklist (MWL) Query Demo
 *
 * PREREQUISITES: Orthanc with Worklist plugin on localhost:4242.
 * Setup: Create worklist files in Orthanc's WorklistsDatabase folder.
 * See: https://book.orthanc-server.com/plugins/worklists.html
 *
 * Modality Worklist is a DICOM service that allows imaging modalities (CT, MR, etc.)
 * to query the hospital information system (HIS/RIS) for scheduled procedures.
 *
 * This eliminates manual data entry at the modality and ensures patient demographic
 * data is consistent across the enterprise.
 *
 * Key concepts:
 * - MWL uses C-FIND at the STUDY level with ModalityWorklistInformationModelFind SOP Class
 * - Query includes Scheduled Procedure Step Sequence for scheduling info
 * - Returns patient demographics, scheduled procedure details, and requested procedures
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ModalityWorklistQueryDemo {

	// Define missing DICOM tags that are not in TagFromName
	private static final AttributeTag ScheduledProcedureStepSequence = new AttributeTag(0x0040, 0x0100);
	private static final AttributeTag ScheduledProcedureStepID = new AttributeTag(0x0040, 0x0009);
	private static final AttributeTag RequestedProcedurePriority = new AttributeTag(0x0040, 0x1003);

	// Modality Worklist Information Model - FIND SOP Class UID
	private static final String ModalityWorklistInformationModelFind = "1.2.840.10008.5.1.4.31";

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Modality Worklist Query Demo ===\n");

			// Use the default character set for VR encoding
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
			AttributeList identifier = new AttributeList();

			// === Patient-level attributes to retrieve ===
			identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientBirthDate);
			identifier.putNewAttribute(TagFromName.PatientSex);
			identifier.putNewAttribute(TagFromName.PatientWeight);

			// === Study-level attributes ===
			identifier.putNewAttribute(TagFromName.StudyInstanceUID);
			identifier.putNewAttribute(TagFromName.AccessionNumber, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.RequestingPhysician, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.ReferringPhysicianName, specificCharacterSet);

			// === Requested Procedure attributes ===
			identifier.putNewAttribute(TagFromName.RequestedProcedureID, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.RequestedProcedureDescription, specificCharacterSet);
			identifier.putNewAttribute(RequestedProcedurePriority, specificCharacterSet);

			// === Scheduled Procedure Step Sequence (critical for MWL) ===
			// This sequence contains the scheduling information
			SequenceAttribute scheduledProcedureStepSequence = new SequenceAttribute(ScheduledProcedureStepSequence);
			AttributeList scheduledProcedureStepItem = new AttributeList();

			// Add attributes to query within the sequence
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledStationAETitle, specificCharacterSet);
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledProcedureStepStartDate);
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledProcedureStepStartTime);
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledPerformingPhysicianName, specificCharacterSet);
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledProcedureStepDescription, specificCharacterSet);
			scheduledProcedureStepItem.putNewAttribute(ScheduledProcedureStepID, specificCharacterSet);
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledProcedureStepLocation, specificCharacterSet);

			// Filter by modality (e.g., CT, MR, US, XA, etc.)
			// Leave empty to get all modalities, or specify like "CT" or "MR"
			Attribute modalityAttr = new CodeStringAttribute(TagFromName.Modality);
			modalityAttr.addValue("CT"); // Query for CT procedures only
			scheduledProcedureStepItem.put(modalityAttr);

			// Filter by scheduled date - use today's date or a date range
			// Format: YYYYMMDD or YYYYMMDD-YYYYMMDD for range
			scheduledProcedureStepItem.putNewAttribute(TagFromName.ScheduledProcedureStepStartDate)
				.addValue("20240101-20241231"); // Date range for 2024

			scheduledProcedureStepSequence.addItem(scheduledProcedureStepItem);
			identifier.put(scheduledProcedureStepSequence);

			// === Imaging Service Request attributes ===
			identifier.putNewAttribute(TagFromName.ImagingServiceRequestComments, specificCharacterSet);

			System.out.println("Querying Modality Worklist...");
			System.out.println("Target AE: ORTHANC on localhost:4242\n");

			// Perform the MWL query
			// Note: You need a DICOM server that supports MWL (like Orthanc with worklist plugin,
			// dcm4chee, or a commercial RIS/PACS)
			new FindSOPClassSCU(
				"localhost",                                          // hostname
				4242,                                                 // port
				"ORTHANC",                                           // called AE title
				"WORKLISTSCU",                                       // calling AE title
				ModalityWorklistInformationModelFind,                // MWL SOP Class
				identifier,                                          // query attributes
				new WorklistResponseHandler()                        // response handler
			);

			System.out.println("\nMWL Query completed.");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}

/**
 * Custom handler to process MWL query responses
 */
class WorklistResponseHandler extends IdentifierHandler {

	private int responseCount = 0;

	@Override
	public void doSomethingWithIdentifier(AttributeList responseIdentifier) throws DicomException {

		responseCount++;
		System.out.println("=== Worklist Item #" + responseCount + " ===");

		// Extract patient information
		String patientName = getAttributeValue(responseIdentifier, TagFromName.PatientName);
		String patientId = getAttributeValue(responseIdentifier, TagFromName.PatientID);
		String patientBirthDate = getAttributeValue(responseIdentifier, TagFromName.PatientBirthDate);
		String patientSex = getAttributeValue(responseIdentifier, TagFromName.PatientSex);

		System.out.println("Patient Name: " + patientName);
		System.out.println("Patient ID: " + patientId);
		System.out.println("Birth Date: " + patientBirthDate);
		System.out.println("Sex: " + patientSex);

		// Extract procedure information
		String accessionNumber = getAttributeValue(responseIdentifier, TagFromName.AccessionNumber);
		String requestedProcedure = getAttributeValue(responseIdentifier, TagFromName.RequestedProcedureDescription);

		System.out.println("Accession Number: " + accessionNumber);
		System.out.println("Requested Procedure: " + requestedProcedure);

		// Extract scheduled procedure step information from sequence
		AttributeTag spsSeqTag = new AttributeTag(0x0040, 0x0100); // ScheduledProcedureStepSequence
		Attribute spsSequenceAttr = responseIdentifier.get(spsSeqTag);
		if (spsSequenceAttr != null && spsSequenceAttr instanceof SequenceAttribute) {
			SequenceAttribute spsSequence = (SequenceAttribute) spsSequenceAttr;
			if (spsSequence.getNumberOfItems() > 0) {
				AttributeList spsItem = spsSequence.getItem(0).getAttributeList();

				String scheduledDate = getAttributeValue(spsItem, TagFromName.ScheduledProcedureStepStartDate);
				String scheduledTime = getAttributeValue(spsItem, TagFromName.ScheduledProcedureStepStartTime);
				String modality = getAttributeValue(spsItem, TagFromName.Modality);
				String stationAE = getAttributeValue(spsItem, TagFromName.ScheduledStationAETitle);
				String stepDescription = getAttributeValue(spsItem, TagFromName.ScheduledProcedureStepDescription);

				System.out.println("Scheduled Date: " + scheduledDate);
				System.out.println("Scheduled Time: " + scheduledTime);
				System.out.println("Modality: " + modality);
				System.out.println("Station AE: " + stationAE);
				System.out.println("Step Description: " + stepDescription);
			}
		}

		System.out.println();
	}

	private String getAttributeValue(AttributeList list, AttributeTag tag) {
		Attribute attr = list.get(tag);
		if (attr != null) {
			String value = attr.getSingleStringValueOrNull();
			return value != null ? value : "(empty)";
		}
		return "(not present)";
	}
}
