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
 * DICOM Modality Performed Procedure Step (MPPS) - N-SET Demo (COMPLETED)
 *
 * PREREQUISITES: DICOM server supporting MPPS (N-SET) on localhost:4242.
 * An MPPS instance must exist from prior N-CREATE. Update 'mppsSOPInstanceUID'.
 *
 * This demo shows how to complete an MPPS by sending an N-SET request
 * with the status changed to "COMPLETED".
 *
 * The N-SET operation includes:
 * - End date/time of the procedure
 * - Final status (COMPLETED or DISCONTINUED)
 * - Performed Series Sequence (what was actually acquired)
 * - Optional dose information (for CT/XA)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class MPPSCompletedDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM MPPS N-SET Demo (COMPLETED) ===\n");

			// Connection parameters
			String remoteHost = "localhost";
			int remotePort = 4242;
			String remoteAETitle = "ORTHANC";
			String localAETitle = "MPPSSCU";

			// This should be the SOP Instance UID from the previous N-CREATE
			// In real usage, you would store this from the N-CREATE response
			String mppsSOPInstanceUID = "1.2.3.4.5.6.7.8.9.10.11.12";

			System.out.println("Completing MPPS with SOP Instance UID: " + mppsSOPInstanceUID);

			// Build the MPPS N-SET attributes for COMPLETED status
			AttributeList mppsAttributes = buildMPPSCompletedAttributes();

			System.out.println("\nMPPS Update:");
			System.out.println("  Status: COMPLETED");
			System.out.println("  Series acquired: 1");

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

				System.out.println("\n--- MPPS N-SET Request Built ---");
				System.out.println("Would send N-SET with:");
				System.out.println("  Requested SOP Class: " + SOPClass.ModalityPerformedProcedureStepSOPClass);
				System.out.println("  Requested SOP Instance: " + mppsSOPInstanceUID);

				// Print the full attribute list
				System.out.println("\n--- N-SET Modification List ---");
				System.out.println(mppsAttributes.toString());

				association.release();
				System.out.println("\nAssociation released.");

				System.out.println("\nMPPS workflow complete.");
				System.out.println("The RIS/PACS now knows the procedure is finished.");

			} else {
				System.err.println("Failed to establish association.");
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * Build the attribute list for MPPS N-SET (COMPLETED state)
	 */
	private static AttributeList buildMPPSCompletedAttributes() throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === Performed Procedure Step End Information ===
		Attribute ppsEndDate = new DateAttribute(TagFromName.PerformedProcedureStepEndDate);
		ppsEndDate.addValue(currentDate);
		list.put(ppsEndDate);

		Attribute ppsEndTime = new TimeAttribute(TagFromName.PerformedProcedureStepEndTime);
		ppsEndTime.addValue(currentTime);
		list.put(ppsEndTime);

		// Status changes to "COMPLETED" (or "DISCONTINUED" if aborted)
		Attribute ppsStatus = new CodeStringAttribute(TagFromName.PerformedProcedureStepStatus);
		ppsStatus.addValue("COMPLETED");
		list.put(ppsStatus);

		// === Performed Series Sequence ===
		// This tells the RIS/PACS what was actually acquired
		SequenceAttribute performedSeriesSeq = new SequenceAttribute(TagFromName.PerformedSeriesSequence);
		AttributeList seriesItem = new AttributeList();

		// Series information
		Attribute seriesInstanceUID = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
		seriesInstanceUID.addValue(UniqueIdentifierAttribute.createUID());
		seriesItem.put(seriesInstanceUID);

		Attribute seriesDescription = new LongStringAttribute(TagFromName.SeriesDescription);
		seriesDescription.addValue("Axial CT Head");
		seriesItem.put(seriesDescription);

		Attribute performingPhysician = new LongStringAttribute(TagFromName.PerformingPhysicianName);
		performingPhysician.addValue("Smith^Jane^Dr");
		seriesItem.put(performingPhysician);

		Attribute protocolName = new LongStringAttribute(TagFromName.ProtocolName);
		protocolName.addValue("CT HEAD WO");
		seriesItem.put(protocolName);

		Attribute operatorName = new LongStringAttribute(TagFromName.OperatorsName);
		operatorName.addValue("Tech^John");
		seriesItem.put(operatorName);

		// === Referenced Image Sequence (images in this series) ===
		SequenceAttribute refImageSeq = new SequenceAttribute(TagFromName.ReferencedImageSequence);

		// Add references for each image acquired
		for (int i = 1; i <= 50; i++) { // Example: 50 CT slices
			AttributeList imageItem = new AttributeList();

			Attribute refSOPClass = new UniqueIdentifierAttribute(TagFromName.ReferencedSOPClassUID);
			refSOPClass.addValue(SOPClass.CTImageStorage);
			imageItem.put(refSOPClass);

			Attribute refSOPInstance = new UniqueIdentifierAttribute(TagFromName.ReferencedSOPInstanceUID);
			refSOPInstance.addValue(UniqueIdentifierAttribute.createUID());
			imageItem.put(refSOPInstance);

			refImageSeq.addItem(imageItem);
		}

		seriesItem.put(refImageSeq);

		// === Referenced Non-Image Composite SOP Instance Sequence (e.g., dose reports) ===
		seriesItem.put(new SequenceAttribute(TagFromName.ReferencedNonImageCompositeSOPInstanceSequence));

		performedSeriesSeq.addItem(seriesItem);
		list.put(performedSeriesSeq);

		return list;
	}
}
