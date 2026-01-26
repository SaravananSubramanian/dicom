package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.ContentItem;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.StructuredReport;
import com.pixelmed.dicom.TagFromName;

/**
 * DICOM Structured Report (SR) Reading Demo
 *
 * PREREQUISITES: Requires a DICOM SR file. Update 'srFilePath' variable.
 * Create an SR using CreateStructuredReportDemo, or obtain one from PACS.
 * If file not found, demo displays SR structure overview.
 *
 * This example demonstrates how to read and parse a DICOM Structured Report.
 * SR documents have a hierarchical content tree that needs to be traversed.
 *
 * Key tags in SR:
 * - (0040,A040) Value Type - TEXT, CODE, NUM, DATE, TIME, UIDREF, etc.
 * - (0040,A043) Concept Name Code Sequence - What is being reported
 * - (0040,A160) Text Value - For TEXT value types
 * - (0040,A168) Concept Code Sequence - For CODE value types
 * - (0040,A300) Measured Value Sequence - For NUM value types
 * - (0040,A730) Content Sequence - Child items
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ReadStructuredReportDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Structured Report Reading Demo ===\n");

			// Path to SR DICOM file
			String srFilePath = "C:\\path\\to\\structured_report.dcm";

			// Read the SR file
			AttributeList list = new AttributeList();
			list.read(srFilePath);

			// Print basic SR information
			printSRInfo(list);

			// Parse and display the content tree
			System.out.println("\n--- Content Tree ---\n");
			parseContentTree(list);

			// Alternative: Use PixelMed's StructuredReport class
			System.out.println("\n--- Using StructuredReport Class ---\n");
			parseUsingStructuredReportClass(list);

		} catch (Exception e) {
			System.out.println("Note: Update 'srFilePath' with an actual SR file path.");
			System.out.println("Error: " + e.getMessage());
			demonstrateSRStructure();
		}
	}

	/**
	 * Print basic SR document information
	 */
	private static void printSRInfo(AttributeList list) {

		System.out.println("=== SR Document Information ===");

		String patientName = getStringValue(list, TagFromName.PatientName);
		String patientID = getStringValue(list, TagFromName.PatientID);
		String studyDate = getStringValue(list, TagFromName.StudyDate);
		String completionFlag = getStringValue(list, TagFromName.CompletionFlag);
		String verificationFlag = getStringValue(list, TagFromName.VerificationFlag);
		String sopClass = getStringValue(list, TagFromName.SOPClassUID);

		System.out.println("Patient Name: " + patientName);
		System.out.println("Patient ID: " + patientID);
		System.out.println("Study Date: " + studyDate);
		System.out.println("Completion Flag: " + completionFlag);
		System.out.println("Verification Flag: " + verificationFlag);
		System.out.println("SOP Class UID: " + sopClass);
	}

	/**
	 * Parse the content tree manually
	 */
	private static void parseContentTree(AttributeList list) {

		// Get the Content Sequence (root of the tree)
		Attribute contentSeqAttr = list.get(TagFromName.ContentSequence);

		if (contentSeqAttr == null || !(contentSeqAttr instanceof SequenceAttribute)) {
			System.out.println("No Content Sequence found in SR document.");
			return;
		}

		SequenceAttribute contentSeq = (SequenceAttribute) contentSeqAttr;
		System.out.println("Number of root content items: " + contentSeq.getNumberOfItems());

		// Traverse each item
		for (int i = 0; i < contentSeq.getNumberOfItems(); i++) {
			AttributeList item = contentSeq.getItem(i).getAttributeList();
			traverseContentItem(item, 0);
		}
	}

	/**
	 * Recursively traverse a content item and its children
	 */
	private static void traverseContentItem(AttributeList item, int depth) {

		String indent = "  ".repeat(depth);

		// Get value type
		String valueType = getStringValue(item, TagFromName.ValueType);

		// Get concept name (what is being reported)
		String conceptName = getConceptName(item);

		// Get the value based on type
		String value = getContentValue(item, valueType);

		// Get relationship type
		String relationship = getStringValue(item, TagFromName.RelationshipType);

		// Print this item
		System.out.println(indent + "[" + valueType + "] " + conceptName);
		if (relationship != null && !relationship.isEmpty()) {
			System.out.println(indent + "  Relationship: " + relationship);
		}
		if (value != null && !value.isEmpty()) {
			System.out.println(indent + "  Value: " + value);
		}

		// Recursively process child items
		Attribute childSeqAttr = item.get(TagFromName.ContentSequence);
		if (childSeqAttr != null && childSeqAttr instanceof SequenceAttribute) {
			SequenceAttribute childSeq = (SequenceAttribute) childSeqAttr;
			for (int i = 0; i < childSeq.getNumberOfItems(); i++) {
				traverseContentItem(childSeq.getItem(i).getAttributeList(), depth + 1);
			}
		}
	}

	/**
	 * Get the concept name from Concept Name Code Sequence
	 */
	private static String getConceptName(AttributeList item) {

		Attribute seqAttr = item.get(TagFromName.ConceptNameCodeSequence);
		if (seqAttr != null && seqAttr instanceof SequenceAttribute) {
			SequenceAttribute seq = (SequenceAttribute) seqAttr;
			if (seq.getNumberOfItems() > 0) {
				AttributeList codeItem = seq.getItem(0).getAttributeList();
				String meaning = getStringValue(codeItem, TagFromName.CodeMeaning);
				String codeValue = getStringValue(codeItem, TagFromName.CodeValue);
				return meaning + " (" + codeValue + ")";
			}
		}
		return "(no concept name)";
	}

	/**
	 * Get the content value based on value type
	 */
	private static String getContentValue(AttributeList item, String valueType) {

		if (valueType == null) return null;

		switch (valueType) {
			case "TEXT":
				return getStringValue(item, TagFromName.TextValue);

			case "CODE":
				Attribute codeSeqAttr = item.get(TagFromName.ConceptCodeSequence);
				if (codeSeqAttr != null && codeSeqAttr instanceof SequenceAttribute) {
					SequenceAttribute seq = (SequenceAttribute) codeSeqAttr;
					if (seq.getNumberOfItems() > 0) {
						AttributeList codeItem = seq.getItem(0).getAttributeList();
						return getStringValue(codeItem, TagFromName.CodeMeaning);
					}
				}
				return null;

			case "NUM":
				Attribute measSeqAttr = item.get(TagFromName.MeasuredValueSequence);
				if (measSeqAttr != null && measSeqAttr instanceof SequenceAttribute) {
					SequenceAttribute seq = (SequenceAttribute) measSeqAttr;
					if (seq.getNumberOfItems() > 0) {
						AttributeList measItem = seq.getItem(0).getAttributeList();
						String numValue = getStringValue(measItem, TagFromName.NumericValue);
						// Get units
						Attribute unitsSeqAttr = measItem.get(TagFromName.MeasurementUnitsCodeSequence);
						String units = "";
						if (unitsSeqAttr != null && unitsSeqAttr instanceof SequenceAttribute) {
							SequenceAttribute unitsSeq = (SequenceAttribute) unitsSeqAttr;
							if (unitsSeq.getNumberOfItems() > 0) {
								units = " " + getStringValue(unitsSeq.getItem(0).getAttributeList(), TagFromName.CodeValue);
							}
						}
						return numValue + units;
					}
				}
				return null;

			case "DATE":
				return getStringValue(item, TagFromName.Date);

			case "TIME":
				return getStringValue(item, TagFromName.Time);

			case "PNAME":
				return getStringValue(item, TagFromName.PersonName);

			case "UIDREF":
				return getStringValue(item, TagFromName.UID);

			case "CONTAINER":
				return null; // Containers don't have values, just children

			default:
				return null;
		}
	}

	/**
	 * Use PixelMed's StructuredReport class for parsing
	 */
	private static void parseUsingStructuredReportClass(AttributeList list) {

		try {
			StructuredReport sr = new StructuredReport(list);
			ContentItem root = (ContentItem) sr.getRoot();

			if (root != null) {
				System.out.println("Root concept: " + root.getConceptNameCodeMeaning());
				printContentItem(root, 0);
			}

		} catch (Exception e) {
			System.out.println("Error parsing with StructuredReport: " + e.getMessage());
		}
	}

	private static void printContentItem(ContentItem item, int depth) {

		String indent = "  ".repeat(depth);
		String valueType = item.getValueType();
		String conceptName = item.getConceptNameCodeMeaning();

		System.out.println(indent + "[" + valueType + "] " + conceptName);

		// Print children
		int childCount = item.getChildCount();
		for (int i = 0; i < childCount; i++) {
			ContentItem child = (ContentItem) item.getChildAt(i);
			printContentItem(child, depth + 1);
		}
	}

	private static String getStringValue(AttributeList list, com.pixelmed.dicom.AttributeTag tag) {
		Attribute attr = list.get(tag);
		return (attr != null) ? attr.getSingleStringValueOrNull() : null;
	}

	/**
	 * Demonstrate SR structure when no file is available
	 */
	private static void demonstrateSRStructure() {

		System.out.println("\n=== Structured Report Structure Overview ===\n");

		System.out.println("An SR document has a tree structure of Content Items:\n");

		System.out.println("[CONTAINER] Document Title");
		System.out.println("  |-- [CODE] Observer Type: Person");
		System.out.println("  |-- [PNAME] Observer Name: Dr. Smith");
		System.out.println("  |-- [CONTAINER] Findings");
		System.out.println("  |     |-- [TEXT] Finding: Heart size normal");
		System.out.println("  |     |-- [NUM] Measurement: 12.5 mm");
		System.out.println("  |     |-- [CODE] Abnormality: None");
		System.out.println("  |-- [CONTAINER] Impression");
		System.out.println("        |-- [TEXT] Conclusion: No abnormality");

		System.out.println("\nValue Types:");
		System.out.println("  CONTAINER - Groups related content items");
		System.out.println("  TEXT      - Free text value");
		System.out.println("  CODE      - Coded value from terminology");
		System.out.println("  NUM       - Numeric measurement with units");
		System.out.println("  DATE      - Date value");
		System.out.println("  TIME      - Time value");
		System.out.println("  PNAME     - Person name");
		System.out.println("  UIDREF    - Reference to another DICOM object");
		System.out.println("  IMAGE     - Reference to image with coordinates");
		System.out.println("  COMPOSITE - Reference to composite object");
		System.out.println("  WAVEFORM  - Reference to waveform data");
	}
}
