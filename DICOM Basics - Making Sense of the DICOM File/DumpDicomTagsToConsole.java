package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;

/**
 * DICOM Specific Tags Display Demo
 *
 * This example demonstrates how to read and display specific DICOM
 * attributes (tags) by name, focusing on the important UID tags.
 *
 * Key concepts:
 * - DICOM uses UIDs (Unique Identifiers) to identify objects
 * - UIDs are hierarchical: Study > Series > Instance
 * - Transfer Syntax UID determines how data is encoded
 * - SOP Class UID identifies the type of DICOM object
 *
 * Important UIDs:
 * - Study Instance UID: Identifies a complete study (exam)
 * - Series Instance UID: Identifies a series within a study
 * - SOP Instance UID: Identifies a single DICOM object (image)
 * - SOP Class UID: Identifies the type of object (CT, MR, SR, etc.)
 * - Transfer Syntax UID: How the data is encoded/compressed
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DumpDicomTagsToConsole {

	private static AttributeList attributeList = new AttributeList();

	public static void main(String[] args) {

		// === Configuration ===
		String dicomFile = "C:\\temp\\sample_images\\MR-MONO2-16-head";

		try {

			System.out.println("=== DICOM Specific Tags Display Demo ===\n");
			System.out.println("Reading file: " + dicomFile);

			// === Read the DICOM file ===
			attributeList.read(dicomFile);

			// === Display Key UIDs ===
			System.out.println("\n--- DICOM UIDs ---");
			System.out.println("Study Instance UID: " + getTagValue(TagFromName.StudyInstanceUID));
			System.out.println("Series Instance UID: " + getTagValue(TagFromName.SeriesInstanceUID));
			System.out.println("SOP Class UID: " + getTagValue(TagFromName.SOPClassUID));
			System.out.println("SOP Instance UID: " + getTagValue(TagFromName.SOPInstanceUID));
			System.out.println("Transfer Syntax UID: " + getTagValue(TagFromName.TransferSyntaxUID));

			// === UID Hierarchy Explanation ===
			System.out.println("\n--- UID Hierarchy ---");
			System.out.println("Study Instance UID -> Identifies the entire study/exam");
			System.out.println("  └── Series Instance UID -> Identifies a series within the study");
			System.out.println("        └── SOP Instance UID -> Identifies this specific image/object");

		} catch (Exception e) {
			System.err.println("Error reading DICOM file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to retrieve a tag value as a string
	 */
	private static String getTagValue(AttributeTag tag) {
		return Attribute.getDelimitedStringValuesOrEmptyString(attributeList, tag);
	}
}
