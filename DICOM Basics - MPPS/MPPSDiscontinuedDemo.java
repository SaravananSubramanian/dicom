package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;

/**
 * DICOM Modality Performed Procedure Step (MPPS) - DISCONTINUED Demo
 *
 * Sometimes a procedure cannot be completed. The MPPS allows reporting
 * a DISCONTINUED status with a reason code explaining why.
 *
 * Common discontinuation reasons:
 * - Patient condition (claustrophobia, contrast reaction, etc.)
 * - Equipment failure
 * - Patient request
 * - Wrong patient/procedure
 * - Scheduling conflict
 *
 * This demo shows how to build an N-SET request for DISCONTINUED status.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class MPPSDiscontinuedDemo {

	// Define missing DICOM tags
	private static final AttributeTag PerformedProcedureStepEndDate = new AttributeTag(0x0040, 0x0250);
	private static final AttributeTag PerformedProcedureStepEndTime = new AttributeTag(0x0040, 0x0251);
	private static final AttributeTag PerformedProcedureStepStatus = new AttributeTag(0x0040, 0x0252);
	private static final AttributeTag ProcedureStepDiscontinuationReasonCodeSequence = new AttributeTag(0x0040, 0x0281);
	private static final AttributeTag PerformedSeriesSequence = new AttributeTag(0x0040, 0x0340);

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM MPPS N-SET Demo (DISCONTINUED) ===\n");

			// Example MPPS SOP Instance UID (from previous N-CREATE)
			String mppsSOPInstanceUID = "1.2.3.4.5.6.7.8.9.10.11.12";

			System.out.println("Discontinuing MPPS: " + mppsSOPInstanceUID);

			// Build the MPPS N-SET attributes for DISCONTINUED status
			AttributeList mppsAttributes = buildMPPSDiscontinuedAttributes(
				"DCM", "110514", "Patient condition prevented completion"
			);

			System.out.println("\nMPPS Status: DISCONTINUED");
			System.out.println("Reason: Patient condition prevented completion");

			System.out.println("\n--- N-SET Modification List ---");
			System.out.println(mppsAttributes.toString());

			System.out.println("\n=== Discontinuation Reason Codes (DICOM CID 9301) ===");
			System.out.println("Code Value | Meaning");
			System.out.println("-----------|------------------------------------------");
			System.out.println("110501     | Equipment failure");
			System.out.println("110502     | Incorrect worklist entry selected");
			System.out.println("110503     | Object incorrectly formatted");
			System.out.println("110504     | Object of insufficient content");
			System.out.println("110505     | Resource pre-empted");
			System.out.println("110506     | Resources inadequate");
			System.out.println("110507     | Duplicate order");
			System.out.println("110508     | Workflow aborted");
			System.out.println("110509     | Procedure canceled");
			System.out.println("110510     | Procedure step canceled");
			System.out.println("110513     | Discontinued for unspecified reason");
			System.out.println("110514     | Patient condition prevented completion");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * Build the attribute list for MPPS N-SET (DISCONTINUED state)
	 *
	 * @param codingScheme The coding scheme designator (e.g., "DCM")
	 * @param codeValue The discontinuation reason code
	 * @param codeMeaning Human-readable meaning of the code
	 */
	private static AttributeList buildMPPSDiscontinuedAttributes(
			String codingScheme, String codeValue, String codeMeaning) throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();

		// === End date/time ===
		Attribute ppsEndDate = new DateAttribute(PerformedProcedureStepEndDate);
		ppsEndDate.addValue(dateFormat.format(now));
		list.put(ppsEndDate);

		Attribute ppsEndTime = new TimeAttribute(PerformedProcedureStepEndTime);
		ppsEndTime.addValue(timeFormat.format(now));
		list.put(ppsEndTime);

		// === Status: DISCONTINUED ===
		Attribute ppsStatus = new CodeStringAttribute(PerformedProcedureStepStatus);
		ppsStatus.addValue("DISCONTINUED");
		list.put(ppsStatus);

		// === Procedure Step Discontinuation Reason Code Sequence ===
		// This explains WHY the procedure was discontinued
		SequenceAttribute discontinuationReasonSeq =
			new SequenceAttribute(ProcedureStepDiscontinuationReasonCodeSequence);

		AttributeList reasonItem = new AttributeList();

		Attribute codeValueAttr = new ShortStringAttribute(TagFromName.CodeValue);
		codeValueAttr.addValue(codeValue);
		reasonItem.put(codeValueAttr);

		Attribute codingSchemeAttr = new ShortStringAttribute(TagFromName.CodingSchemeDesignator);
		codingSchemeAttr.addValue(codingScheme);
		reasonItem.put(codingSchemeAttr);

		Attribute codeMeaningAttr = new LongStringAttribute(TagFromName.CodeMeaning);
		codeMeaningAttr.addValue(codeMeaning);
		reasonItem.put(codeMeaningAttr);

		discontinuationReasonSeq.addItem(reasonItem);
		list.put(discontinuationReasonSeq);

		// === Performed Series Sequence ===
		// May contain partial data if some images were acquired before discontinuation
		SequenceAttribute performedSeriesSeq = new SequenceAttribute(PerformedSeriesSequence);
		// Leave empty if no images were acquired, or populate with partial acquisition data
		list.put(performedSeriesSeq);

		return list;
	}
}
