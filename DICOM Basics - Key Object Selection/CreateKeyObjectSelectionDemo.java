package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.ContentItem;
import com.pixelmed.dicom.ContentItemFactory;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.StructuredReport;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

/**
 * DICOM Key Object Selection (KOS) Creation Demo
 *
 * Key Object Selection documents are used to mark significant images
 * for various purposes:
 *
 * Use Cases:
 * - Teaching Files: Mark exemplary cases for education
 * - Quality Assurance: Flag images for review
 * - Clinical: Highlight key findings for referring physician
 * - Manifest: List images being transmitted or exported
 * - Rejected for Quality: Mark images as rejected
 *
 * KOS Structure:
 * - Based on DICOM SR (Structured Report)
 * - Contains references to selected images
 * - Includes purpose/reason codes
 * - Can reference images across multiple studies/series
 *
 * SOP Class: 1.2.840.10008.5.1.4.1.1.88.59 (Key Object Selection Document)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreateKeyObjectSelectionDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Key Object Selection Creation Demo ===\n");

			// Images to mark as key objects
			String[][] selectedImages = {
				// {StudyUID, SeriesUID, SOPInstanceUID, SOPClassUID}
				{"1.2.3.4.5.6.7.8.9", "1.2.3.4.5.6.7.8.9.1", "1.2.3.4.5.6.7.8.9.1.1", SOPClass.CTImageStorage},
				{"1.2.3.4.5.6.7.8.9", "1.2.3.4.5.6.7.8.9.1", "1.2.3.4.5.6.7.8.9.1.5", SOPClass.CTImageStorage},
				{"1.2.3.4.5.6.7.8.9", "1.2.3.4.5.6.7.8.9.2", "1.2.3.4.5.6.7.8.9.2.10", SOPClass.CTImageStorage}
			};

			// Output file
			String outputFile = "C:\\temp\\key_object_selection.dcm";

			// Create the KOS
			AttributeList kosList = createKeyObjectSelection(
				selectedImages,
				"113000",  // Code value for "Of Interest"
				"DCM",     // Coding scheme
				"Of Interest"  // Code meaning
			);

			// Add file meta information
			FileMetaInformation.addFileMetaInformation(
				kosList,
				TransferSyntax.ExplicitVRLittleEndian,
				"OurAET"
			);

			// Write to file
			kosList.write(outputFile);

			System.out.println("Key Object Selection created: " + outputFile);
			System.out.println("Number of referenced images: " + selectedImages.length);
			System.out.println("Purpose: Of Interest (Teaching File)");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			demonstrateKOSConcepts();
		}
	}

	/**
	 * Create a Key Object Selection document
	 */
	private static AttributeList createKeyObjectSelection(
			String[][] selectedImages,
			String purposeCodeValue,
			String purposeCodingScheme,
			String purposeCodeMeaning) throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === SOP Common Module ===
		Attribute sopClass = new UniqueIdentifierAttribute(TagFromName.SOPClassUID);
		sopClass.addValue(SOPClass.KeyObjectSelectionDocumentStorage);
		list.put(sopClass);

		Attribute sopInstance = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
		sopInstance.addValue(UniqueIdentifierAttribute.createUID());
		list.put(sopInstance);

		// === Patient Module ===
		Attribute patientName = new PersonNameAttribute(TagFromName.PatientName);
		patientName.addValue("Doe^John");
		list.put(patientName);

		Attribute patientID = new LongStringAttribute(TagFromName.PatientID);
		patientID.addValue("PAT123");
		list.put(patientID);

		Attribute patientBirthDate = new DateAttribute(TagFromName.PatientBirthDate);
		patientBirthDate.addValue("19700101");
		list.put(patientBirthDate);

		Attribute patientSex = new CodeStringAttribute(TagFromName.PatientSex);
		patientSex.addValue("M");
		list.put(patientSex);

		// === Study Module ===
		// Use the study UID from the first referenced image
		Attribute studyInstanceUID = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
		studyInstanceUID.addValue(selectedImages[0][0]);
		list.put(studyInstanceUID);

		Attribute studyDate = new DateAttribute(TagFromName.StudyDate);
		studyDate.addValue(currentDate);
		list.put(studyDate);

		Attribute studyTime = new TimeAttribute(TagFromName.StudyTime);
		studyTime.addValue(currentTime);
		list.put(studyTime);

		Attribute accessionNumber = new ShortStringAttribute(TagFromName.AccessionNumber);
		accessionNumber.addValue("ACC123");
		list.put(accessionNumber);

		Attribute referringPhysician = new PersonNameAttribute(TagFromName.ReferringPhysicianName);
		referringPhysician.addValue("Smith^Jane^Dr");
		list.put(referringPhysician);

		Attribute studyID = new ShortStringAttribute(TagFromName.StudyID);
		studyID.addValue("STUDY001");
		list.put(studyID);

		// === KOS Series Module ===
		Attribute modality = new CodeStringAttribute(TagFromName.Modality);
		modality.addValue("KO"); // Key Object
		list.put(modality);

		Attribute seriesInstanceUID = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
		seriesInstanceUID.addValue(UniqueIdentifierAttribute.createUID());
		list.put(seriesInstanceUID);

		Attribute seriesNumber = new IntegerStringAttribute(TagFromName.SeriesNumber);
		seriesNumber.addValue("999");
		list.put(seriesNumber);

		// === SR Document Module ===
		Attribute instanceNumber = new IntegerStringAttribute(TagFromName.InstanceNumber);
		instanceNumber.addValue("1");
		list.put(instanceNumber);

		Attribute contentDate = new DateAttribute(TagFromName.ContentDate);
		contentDate.addValue(currentDate);
		list.put(contentDate);

		Attribute contentTime = new TimeAttribute(TagFromName.ContentTime);
		contentTime.addValue(currentTime);
		list.put(contentTime);

		// === Build KOS Content Tree ===
		ContentItemFactory cif = new ContentItemFactory();

		// Root container with document title
		ContentItem root = cif.new ContainerContentItem(
			null,
			null,
			new com.pixelmed.dicom.CodedSequenceItem("113030", "DCM", "Key Object Selection"),
			true,
			"DCMR",
			"2010" // KOS template
		);

		// Add purpose/flag
		ContentItem purposeCode = cif.new CodeContentItem(
			root,
			"HAS CONCEPT MOD",
			new com.pixelmed.dicom.CodedSequenceItem("113012", "DCM", "Key Object Description"),
			new com.pixelmed.dicom.CodedSequenceItem(purposeCodeValue, purposeCodingScheme, purposeCodeMeaning)
		);

		// Add each selected image as an IMAGE content item
		for (String[] image : selectedImages) {
			String studyUID = image[0];
			String seriesUID = image[1];
			String instanceUID = image[2];
			String classUID = image[3];

			// Create IMAGE content item referencing the selected image
			ContentItem imageRef = cif.new ImageContentItem(
				root,
				"CONTAINS",
				null, // concept name (optional for images in KOS)
				classUID,  // Referenced SOP Class UID
				instanceUID, // Referenced SOP Instance UID
				0, // Referenced Frame Number (0 for all frames)
				0, // Referenced Segment Number
				null, // Presentation State SOP Class
				null  // Presentation State SOP Instance
			);
		}

		// Convert content tree to attributes
		StructuredReport sr = new StructuredReport(root);
		AttributeList srContent = sr.getAttributeList();
		list.putAll(srContent);

		return list;
	}

	/**
	 * Demonstrate KOS concepts
	 */
	private static void demonstrateKOSConcepts() {

		System.out.println("\n=== Key Object Selection Concepts ===\n");

		System.out.println("--- Common KOS Purpose Codes (CID 7010) ---");
		System.out.println("Code Value | Meaning");
		System.out.println("-----------|--------------------------------");
		System.out.println("113000     | Of Interest");
		System.out.println("113001     | Rejected for Quality Reasons");
		System.out.println("113002     | For Referring Provider");
		System.out.println("113003     | For Surgery");
		System.out.println("113004     | For Teaching");
		System.out.println("113005     | For Conference");
		System.out.println("113006     | For Therapy");
		System.out.println("113007     | For Patient");
		System.out.println("113008     | For Peer Review");
		System.out.println("113009     | For Research");
		System.out.println("113010     | Quality Issue");
		System.out.println("113013     | Best In Set");
		System.out.println("113018     | For Printing");
		System.out.println("113020     | For Report Attachment");
		System.out.println("113030     | Manifest");
		System.out.println("113031     | Signed Manifest");
		System.out.println("113032     | Complete Study Content");

		System.out.println("\n--- Rejection Reason Codes (CID 7011) ---");
		System.out.println("113001     | Rejected for Quality Reasons");
		System.out.println("113037     | Rejected for Patient Safety");
		System.out.println("113038     | Incorrect Modality Worklist Entry");
		System.out.println("113039     | Data Retention Policy Expired");

		System.out.println("\n--- Use Cases ---");
		System.out.println();
		System.out.println("1. Teaching File Export:");
		System.out.println("   - Radiologist marks interesting cases");
		System.out.println("   - KOS document lists all selected images");
		System.out.println("   - Export system retrieves images listed in KOS");
		System.out.println();
		System.out.println("2. Quality Rejection:");
		System.out.println("   - Technologist marks motion-blurred image");
		System.out.println("   - KOS with rejection code created");
		System.out.println("   - PACS hides rejected images from display");
		System.out.println();
		System.out.println("3. Referring Physician Report:");
		System.out.println("   - Radiologist marks key finding images");
		System.out.println("   - KOS attached to radiology report");
		System.out.println("   - Physician sees most important images first");
		System.out.println();
		System.out.println("4. Study Manifest:");
		System.out.println("   - List all images being sent to another system");
		System.out.println("   - Receiver can verify all images received");
	}
}
