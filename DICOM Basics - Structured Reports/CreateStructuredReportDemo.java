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
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.StructuredReport;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UIDGenerator;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

/**
 * DICOM Structured Report (SR) Creation Demo
 *
 * PREREQUISITES: Output directory (C:\temp) must exist.
 * No input files required - creates SR from scratch.
 *
 * DICOM Structured Reports are a standardized way to encode clinical findings,
 * measurements, and observations. They use a tree structure with coded concepts.
 *
 * SR Template Types:
 * - Basic Text SR (1.2.840.10008.5.1.4.1.1.88.11) - Simple text reports
 * - Enhanced SR (1.2.840.10008.5.1.4.1.1.88.22) - With numeric measurements
 * - Comprehensive SR (1.2.840.10008.5.1.4.1.1.88.33) - Full functionality
 * - Comprehensive 3D SR (1.2.840.10008.5.1.4.1.1.88.34) - With 3D coordinates
 *
 * Key concepts:
 * - Content Items: Building blocks of SR (contain concepts and values)
 * - Value Types: CONTAINER, TEXT, CODE, NUM, DATE, TIME, PNAME, UIDREF, IMAGE, etc.
 * - Coded Entries: Use standard vocabularies (SNOMED, LOINC, RadLex, etc.)
 * - Relationships: CONTAINS, HAS OBS CONTEXT, HAS ACQ CONTEXT, etc.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreateStructuredReportDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Structured Report Creation Demo ===\n");

			// Output file path
			String outputFilePath = "C:\\temp\\sample_sr.dcm";

			// Create the SR
			AttributeList srList = createChestXRayReport();

			// Add file meta information
			FileMetaInformation.addFileMetaInformation(
				srList,
				TransferSyntax.ExplicitVRLittleEndian,
				"OurSourceAET"
			);

			// Write to file
			srList.write(outputFilePath);

			System.out.println("Structured Report created: " + outputFilePath);
			System.out.println("\n--- SR Contents ---");
			System.out.println(srList.toString());

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Create a sample Chest X-Ray finding report
	 */
	private static AttributeList createChestXRayReport() throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === SOP Common Module ===
		Attribute sopClassUID = new UniqueIdentifierAttribute(TagFromName.SOPClassUID);
		sopClassUID.addValue(SOPClass.BasicTextSRStorage);
		list.put(sopClassUID);

		Attribute sopInstanceUID = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
		sopInstanceUID.addValue(new UIDGenerator().getNewUID());
		list.put(sopInstanceUID);

		// === Patient Module ===
		Attribute patientName = new PersonNameAttribute(TagFromName.PatientName);
		patientName.addValue("Doe^John^A");
		list.put(patientName);

		Attribute patientID = new LongStringAttribute(TagFromName.PatientID);
		patientID.addValue("PAT123456");
		list.put(patientID);

		Attribute patientBirthDate = new DateAttribute(TagFromName.PatientBirthDate);
		patientBirthDate.addValue("19650315");
		list.put(patientBirthDate);

		Attribute patientSex = new CodeStringAttribute(TagFromName.PatientSex);
		patientSex.addValue("M");
		list.put(patientSex);

		// === General Study Module ===
		Attribute studyInstanceUID = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
		studyInstanceUID.addValue(new UIDGenerator().getNewUID());
		list.put(studyInstanceUID);

		Attribute studyDate = new DateAttribute(TagFromName.StudyDate);
		studyDate.addValue(currentDate);
		list.put(studyDate);

		Attribute studyTime = new TimeAttribute(TagFromName.StudyTime);
		studyTime.addValue(currentTime);
		list.put(studyTime);

		Attribute accessionNumber = new ShortStringAttribute(TagFromName.AccessionNumber);
		accessionNumber.addValue("ACC123456");
		list.put(accessionNumber);

		Attribute referringPhysician = new PersonNameAttribute(TagFromName.ReferringPhysicianName);
		referringPhysician.addValue("Smith^Jane^Dr");
		list.put(referringPhysician);

		Attribute studyID = new ShortStringAttribute(TagFromName.StudyID);
		studyID.addValue("STUDY001");
		list.put(studyID);

		// === SR Document Series Module ===
		Attribute modality = new CodeStringAttribute(TagFromName.Modality);
		modality.addValue("SR");
		list.put(modality);

		Attribute seriesInstanceUID = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
		seriesInstanceUID.addValue(new UIDGenerator().getNewUID());
		list.put(seriesInstanceUID);

		Attribute seriesNumber = new com.pixelmed.dicom.IntegerStringAttribute(TagFromName.SeriesNumber);
		seriesNumber.addValue("1");
		list.put(seriesNumber);

		// === SR Document General Module ===
		Attribute instanceNumber = new com.pixelmed.dicom.IntegerStringAttribute(TagFromName.InstanceNumber);
		instanceNumber.addValue("1");
		list.put(instanceNumber);

		Attribute contentDate = new DateAttribute(TagFromName.ContentDate);
		contentDate.addValue(currentDate);
		list.put(contentDate);

		Attribute contentTime = new TimeAttribute(TagFromName.ContentTime);
		contentTime.addValue(currentTime);
		list.put(contentTime);

		// Completion flag - COMPLETE or PARTIAL
		Attribute completionFlag = new CodeStringAttribute(TagFromName.CompletionFlag);
		completionFlag.addValue("COMPLETE");
		list.put(completionFlag);

		// Verification flag - VERIFIED or UNVERIFIED
		Attribute verificationFlag = new CodeStringAttribute(TagFromName.VerificationFlag);
		verificationFlag.addValue("VERIFIED");
		list.put(verificationFlag);

		// === Build the SR Content Tree ===
		// Use ContentItemFactory to build the tree
		ContentItemFactory cif = new ContentItemFactory();

		// Root container - document title
		ContentItem root = cif.new ContainerContentItem(
			null, // parent (null for root)
			null, // relationship type (null for root)
			new com.pixelmed.dicom.CodedSequenceItem("18782-3", "LN", "Radiology Study Observation"), // concept name
			true, // continuity of content: SEPARATE
			"DCMR", // template mapping resource
			"2000" // template identifier
		);

		// Add observation context
		ContentItem observerContext = cif.new CodeContentItem(
			root,
			"HAS OBS CONTEXT",
			new com.pixelmed.dicom.CodedSequenceItem("121005", "DCM", "Observer Type"),
			new com.pixelmed.dicom.CodedSequenceItem("121006", "DCM", "Person")
		);

		// Add observer name
		ContentItem observerName = cif.new PersonNameContentItem(
			root,
			"HAS OBS CONTEXT",
			new com.pixelmed.dicom.CodedSequenceItem("121008", "DCM", "Person Observer Name"),
			"Johnson^Robert^Dr"
		);

		// Add findings section
		ContentItem findingsSection = cif.new ContainerContentItem(
			root,
			"CONTAINS",
			new com.pixelmed.dicom.CodedSequenceItem("121070", "DCM", "Findings"),
			true,
			null,
			null
		);

		// Add a text finding
		ContentItem finding1 = cif.new TextContentItem(
			findingsSection,
			"CONTAINS",
			new com.pixelmed.dicom.CodedSequenceItem("121071", "DCM", "Finding"),
			"The heart size is within normal limits. No cardiomegaly."
		);

		ContentItem finding2 = cif.new TextContentItem(
			findingsSection,
			"CONTAINS",
			new com.pixelmed.dicom.CodedSequenceItem("121071", "DCM", "Finding"),
			"The lungs are clear without focal consolidation, effusion, or pneumothorax."
		);

		ContentItem finding3 = cif.new TextContentItem(
			findingsSection,
			"CONTAINS",
			new com.pixelmed.dicom.CodedSequenceItem("121071", "DCM", "Finding"),
			"The mediastinal contours are unremarkable."
		);

		// Add impression section
		ContentItem impressionSection = cif.new ContainerContentItem(
			root,
			"CONTAINS",
			new com.pixelmed.dicom.CodedSequenceItem("121072", "DCM", "Impression"),
			true,
			null,
			null
		);

		ContentItem impression = cif.new TextContentItem(
			impressionSection,
			"CONTAINS",
			new com.pixelmed.dicom.CodedSequenceItem("121073", "DCM", "Impression Description"),
			"No acute cardiopulmonary abnormality."
		);

		// Convert the content tree to DICOM attributes and add to list
		StructuredReport sr = new StructuredReport(root);
		AttributeList srContentList = sr.getAttributeList();

		// Copy SR content to our list
		list.putAll(srContentList);

		return list;
	}
}
