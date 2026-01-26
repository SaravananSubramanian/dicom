package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

/**
 * DICOM Modality Worklist Query by Patient Demo
 *
 * PREREQUISITES: Orthanc with Worklist plugin on localhost:4242.
 * Worklist must contain entries matching the patient name pattern (DOE*).
 *
 * This example demonstrates querying the worklist for a specific patient
 * using patient name or patient ID as search criteria.
 *
 * Common use case: When a patient arrives at the imaging department,
 * the technologist searches for them by name or ID to pull up their
 * scheduled procedures.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ModalityWorklistQueryByPatientDemo {

	// Define missing DICOM tags
	private static final AttributeTag ScheduledProcedureStepSequence = new AttributeTag(0x0040, 0x0100);
	private static final AttributeTag ScheduledProcedureStepID = new AttributeTag(0x0040, 0x0009);

	// Modality Worklist Information Model - FIND SOP Class UID
	private static final String ModalityWorklistInformationModelFind = "1.2.840.10008.5.1.4.31";

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Modality Worklist - Query by Patient ===\n");

			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
			AttributeList identifier = new AttributeList();

			// === Search criteria - Query by patient name using wildcard ===
			// Use * for wildcard matching (e.g., "Smith*" matches "Smith", "Smithson", etc.)
			identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet)
				.addValue("DOE*"); // Search for patients with last name starting with "DOE"

			// Alternatively, you can search by Patient ID:
			// identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet)
			//     .addValue("12345678");

			// === Attributes to retrieve ===
			identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientBirthDate);
			identifier.putNewAttribute(TagFromName.PatientSex);
			identifier.putNewAttribute(TagFromName.StudyInstanceUID);
			identifier.putNewAttribute(TagFromName.AccessionNumber, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.RequestedProcedureDescription, specificCharacterSet);
			identifier.putNewAttribute(TagFromName.RequestedProcedureID, specificCharacterSet);

			// === Scheduled Procedure Step Sequence ===
			SequenceAttribute spsSequence = new SequenceAttribute(ScheduledProcedureStepSequence);
			AttributeList spsItem = new AttributeList();

			spsItem.putNewAttribute(TagFromName.Modality);
			spsItem.putNewAttribute(TagFromName.ScheduledStationAETitle, specificCharacterSet);
			spsItem.putNewAttribute(TagFromName.ScheduledProcedureStepStartDate);
			spsItem.putNewAttribute(TagFromName.ScheduledProcedureStepStartTime);
			spsItem.putNewAttribute(TagFromName.ScheduledProcedureStepDescription, specificCharacterSet);
			spsItem.putNewAttribute(ScheduledProcedureStepID, specificCharacterSet);

			spsSequence.addItem(spsItem);
			identifier.put(spsSequence);

			System.out.println("Searching for patients with name matching 'DOE*'...\n");

			new FindSOPClassSCU(
				"localhost",
				4242,
				"ORTHANC",
				"WORKLISTSCU",
				ModalityWorklistInformationModelFind,
				identifier,
				new PatientWorklistHandler()
			);

			System.out.println("Query completed.");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}

class PatientWorklistHandler extends IdentifierHandler {

	private int count = 0;

	@Override
	public void doSomethingWithIdentifier(AttributeList response) throws DicomException {
		count++;

		System.out.println("--- Result #" + count + " ---");

		printAttribute(response, "Patient Name", TagFromName.PatientName);
		printAttribute(response, "Patient ID", TagFromName.PatientID);
		printAttribute(response, "Birth Date", TagFromName.PatientBirthDate);
		printAttribute(response, "Accession #", TagFromName.AccessionNumber);
		printAttribute(response, "Procedure", TagFromName.RequestedProcedureDescription);

		// Get scheduled details from sequence
		AttributeTag spsTag = new AttributeTag(0x0040, 0x0100);
		Attribute seqAttr = response.get(spsTag);
		if (seqAttr instanceof SequenceAttribute) {
			SequenceAttribute seq = (SequenceAttribute) seqAttr;
			if (seq.getNumberOfItems() > 0) {
				AttributeList item = seq.getItem(0).getAttributeList();
				printAttribute(item, "  Modality", TagFromName.Modality);
				printAttribute(item, "  Date", TagFromName.ScheduledProcedureStepStartDate);
				printAttribute(item, "  Time", TagFromName.ScheduledProcedureStepStartTime);
				printAttribute(item, "  Station", TagFromName.ScheduledStationAETitle);
			}
		}

		System.out.println();
	}

	private void printAttribute(AttributeList list, String label, AttributeTag tag) {
		Attribute attr = list.get(tag);
		String value = (attr != null) ? attr.getSingleStringValueOrNull() : null;
		System.out.println(label + ": " + (value != null ? value : "(empty)"));
	}
}
