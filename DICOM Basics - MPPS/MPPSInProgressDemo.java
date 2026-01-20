package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

/**
 * DICOM Modality Performed Procedure Step (MPPS) - N-CREATE Demo
 *
 * PREREQUISITES: DICOM server supporting MPPS (N-CREATE) on localhost:4242.
 * Orthanc does not support MPPS natively; use dcm4chee or commercial PACS.
 * Demo shows request building; actual N-CREATE needs additional SCP support.
 *
 * MPPS is a DICOM service that allows modalities to report the status of
 * procedures to the RIS/PACS. This enables real-time workflow tracking.
 *
 * MPPS Workflow:
 * 1. N-CREATE with status "IN PROGRESS" - when acquisition starts
 * 2. N-SET with status "COMPLETED" or "DISCONTINUED" - when acquisition ends
 *
 * Benefits:
 * - Real-time procedure tracking
 * - Automatic billing trigger
 * - Dose reporting (for CT/XA)
 * - Accurate procedure documentation
 *
 * This demo shows the N-CREATE operation to start an MPPS.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class MPPSInProgressDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM MPPS N-CREATE Demo (IN PROGRESS) ===\n");

			// Connection parameters
			String remoteHost = "localhost";
			int remotePort = 4242;
			String remoteAETitle = "ORTHANC";
			String localAETitle = "MPPSSCU";

			// Generate a unique SOP Instance UID for this MPPS instance
			String mppsSOPInstanceUID = UniqueIdentifierAttribute.createUID();

			System.out.println("Creating MPPS with SOP Instance UID: " + mppsSOPInstanceUID);

			// Build the MPPS N-CREATE attributes
			AttributeList mppsAttributes = buildMPPSInProgressAttributes(mppsSOPInstanceUID);

			System.out.println("\nMPPS Attributes:");
			System.out.println("  Status: IN PROGRESS");
			System.out.println("  Patient: " + mppsAttributes.get(TagFromName.PatientName).getSingleStringValueOrNull());
			System.out.println("  Modality: " + mppsAttributes.get(TagFromName.Modality).getSingleStringValueOrNull());

			// Build presentation contexts
			LinkedList<PresentationContext> presentationContexts = new LinkedList<>();

			presentationContexts.add(new PresentationContext(
				(byte) 0x01,
				SOPClass.ModalityPerformedProcedureStepSOPClass,
				TransferSyntax.ImplicitVRLittleEndian
			));

			presentationContexts.add(new PresentationContext(
				(byte) 0x03,
				SOPClass.ModalityPerformedProcedureStepSOPClass,
				TransferSyntax.ExplicitVRLittleEndian
			));

			System.out.println("\nEstablishing association with " + remoteAETitle + "...");

			Association association = AssociationFactory.createNewAssociation(
				remoteHost,
				remotePort,
				remoteAETitle,
				localAETitle,
				presentationContexts,
				null,
				false,
				null,
				0,
				0
			);

			if (association != null) {
				System.out.println("Association established successfully.");

				// In a full implementation, you would send the N-CREATE here
				// using the association's sendNCreateRequest method

				System.out.println("\n--- MPPS N-CREATE Request Built ---");
				System.out.println("Would send N-CREATE with:");
				System.out.println("  Affected SOP Class: " + SOPClass.ModalityPerformedProcedureStepSOPClass);
				System.out.println("  Affected SOP Instance: " + mppsSOPInstanceUID);

				// Print the full attribute list for debugging
				System.out.println("\n--- Full Attribute List ---");
				System.out.println(mppsAttributes.toString());

				association.release();
				System.out.println("\nAssociation released.");

				// Store the MPPS SOP Instance UID for later N-SET
				System.out.println("\nIMPORTANT: Save this SOP Instance UID for the N-SET operation:");
				System.out.println(mppsSOPInstanceUID);

			} else {
				System.err.println("Failed to establish association.");
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * Build the attribute list for MPPS N-CREATE (IN PROGRESS state)
	 */
	private static AttributeList buildMPPSInProgressAttributes(String mppsSOPInstanceUID) throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === Performed Procedure Step Information ===
		Attribute ppsStartDate = new DateAttribute(TagFromName.PerformedProcedureStepStartDate);
		ppsStartDate.addValue(currentDate);
		list.put(ppsStartDate);

		Attribute ppsStartTime = new TimeAttribute(TagFromName.PerformedProcedureStepStartTime);
		ppsStartTime.addValue(currentTime);
		list.put(ppsStartTime);

		// Status MUST be "IN PROGRESS" for N-CREATE
		Attribute ppsStatus = new CodeStringAttribute(TagFromName.PerformedProcedureStepStatus);
		ppsStatus.addValue("IN PROGRESS");
		list.put(ppsStatus);

		Attribute ppsID = new ShortStringAttribute(TagFromName.PerformedProcedureStepID);
		ppsID.addValue("PPS001");
		list.put(ppsID);

		Attribute ppsDescription = new LongStringAttribute(TagFromName.PerformedProcedureStepDescription);
		ppsDescription.addValue("CT Head without contrast");
		list.put(ppsDescription);

		// === Modality Information ===
		Attribute modality = new CodeStringAttribute(TagFromName.Modality);
		modality.addValue("CT");
		list.put(modality);

		Attribute stationAE = new ShortStringAttribute(TagFromName.PerformedStationAETitle);
		stationAE.addValue("CT_SCANNER_1");
		list.put(stationAE);

		Attribute stationName = new ShortStringAttribute(TagFromName.PerformedStationName);
		stationName.addValue("CT Room 1");
		list.put(stationName);

		// === Patient Information ===
		Attribute patientName = new PersonNameAttribute(TagFromName.PatientName);
		patientName.addValue("Doe^John");
		list.put(patientName);

		Attribute patientID = new LongStringAttribute(TagFromName.PatientID);
		patientID.addValue("12345678");
		list.put(patientID);

		Attribute patientBirthDate = new DateAttribute(TagFromName.PatientBirthDate);
		patientBirthDate.addValue("19800115");
		list.put(patientBirthDate);

		Attribute patientSex = new CodeStringAttribute(TagFromName.PatientSex);
		patientSex.addValue("M");
		list.put(patientSex);

		// === Study Information ===
		Attribute studyInstanceUID = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
		studyInstanceUID.addValue(UniqueIdentifierAttribute.createUID());
		list.put(studyInstanceUID);

		Attribute studyID = new ShortStringAttribute(TagFromName.StudyID);
		studyID.addValue("STUDY001");
		list.put(studyID);

		// === Referenced Study/Patient Sequences (required but can be empty) ===
		list.put(new SequenceAttribute(TagFromName.ReferencedStudySequence));
		list.put(new SequenceAttribute(TagFromName.ReferencedPatientSequence));

		// === Scheduled Step Attributes Sequence ===
		// Links this MPPS to the original worklist item
		SequenceAttribute scheduledStepSeq = new SequenceAttribute(TagFromName.ScheduledStepAttributesSequence);
		AttributeList scheduledStepItem = new AttributeList();

		Attribute accessionNumber = new ShortStringAttribute(TagFromName.AccessionNumber);
		accessionNumber.addValue("ACC123456");
		scheduledStepItem.put(accessionNumber);

		Attribute requestedProcedureID = new ShortStringAttribute(TagFromName.RequestedProcedureID);
		requestedProcedureID.addValue("REQ001");
		scheduledStepItem.put(requestedProcedureID);

		Attribute scheduledProcedureStepID = new ShortStringAttribute(TagFromName.ScheduledProcedureStepID);
		scheduledProcedureStepID.addValue("SPS001");
		scheduledStepItem.put(scheduledProcedureStepID);

		scheduledStepItem.put(new SequenceAttribute(TagFromName.ReferencedStudySequence));

		scheduledStepSeq.addItem(scheduledStepItem);
		list.put(scheduledStepSeq);

		// === Performed Series Sequence (empty for IN PROGRESS) ===
		list.put(new SequenceAttribute(TagFromName.PerformedSeriesSequence));

		return list;
	}
}
