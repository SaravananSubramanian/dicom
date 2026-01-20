package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

/**
 * DICOM Encapsulated PDF Creation Demo
 *
 * PREREQUISITES: (Optional) PDF file to encapsulate - update 'pdfFilePath'.
 * Output directory (C:\temp) must exist. If no PDF provided, shows structure.
 *
 * DICOM allows encapsulating non-DICOM documents (PDF, CDA) within
 * DICOM objects. This enables storing documents alongside images in PACS.
 *
 * Common Use Cases:
 * - Radiology reports
 * - Pathology reports
 * - Lab results
 * - Consent forms
 * - Prior exam reports
 * - Referral letters
 *
 * SOP Classes:
 * - Encapsulated PDF Storage (1.2.840.10008.5.1.4.1.1.104.1)
 * - Encapsulated CDA Storage (1.2.840.10008.5.1.4.1.1.104.2)
 * - Encapsulated STL Storage (1.2.840.10008.5.1.4.1.1.104.3) - 3D printing
 * - Encapsulated OBJ Storage (1.2.840.10008.5.1.4.1.1.104.4) - 3D models
 * - Encapsulated MTL Storage (1.2.840.10008.5.1.4.1.1.104.5) - Materials
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreateEncapsulatedPdfDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Encapsulated PDF Creation Demo ===\n");

			// Source PDF file
			String pdfFilePath = "C:\\path\\to\\report.pdf";

			// Output DICOM file
			String outputFile = "C:\\temp\\encapsulated_pdf.dcm";

			// Check if PDF exists
			File pdfFile = new File(pdfFilePath);
			if (!pdfFile.exists()) {
				System.out.println("Note: Update 'pdfFilePath' with an actual PDF file.");
				demonstrateEncapsulatedPdfStructure();
				return;
			}

			// Read the PDF file
			byte[] pdfData = readFileBytes(pdfFilePath);
			System.out.println("PDF file size: " + pdfData.length + " bytes");

			// Create the encapsulated PDF DICOM object
			AttributeList list = createEncapsulatedPdf(
				pdfData,
				"Doe^John",
				"PAT123",
				"19700101",
				"M",
				"ACC456",
				"Chest CT Report",
				"1.2.3.4.5.6.7.8.9" // Study Instance UID to link to
			);

			// Add file meta information
			FileMetaInformation.addFileMetaInformation(
				list,
				TransferSyntax.ExplicitVRLittleEndian,
				"OurAET"
			);

			// Write the file
			list.write(outputFile);

			System.out.println("\nEncapsulated PDF created: " + outputFile);
			System.out.println("This DICOM object can now be stored in PACS.");

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			demonstrateEncapsulatedPdfStructure();
		}
	}

	/**
	 * Create an Encapsulated PDF DICOM object
	 */
	private static AttributeList createEncapsulatedPdf(
			byte[] pdfData,
			String patientName,
			String patientId,
			String patientBirthDate,
			String patientSex,
			String accessionNumber,
			String documentTitle,
			String studyInstanceUID) throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === SOP Common Module ===
		Attribute sopClass = new UniqueIdentifierAttribute(TagFromName.SOPClassUID);
		sopClass.addValue(SOPClass.EncapsulatedPDFStorage);
		list.put(sopClass);

		Attribute sopInstance = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
		sopInstance.addValue(UniqueIdentifierAttribute.createUID());
		list.put(sopInstance);

		// === Patient Module ===
		Attribute patNameAttr = new PersonNameAttribute(TagFromName.PatientName);
		patNameAttr.addValue(patientName);
		list.put(patNameAttr);

		Attribute patIdAttr = new LongStringAttribute(TagFromName.PatientID);
		patIdAttr.addValue(patientId);
		list.put(patIdAttr);

		Attribute patBirthDateAttr = new DateAttribute(TagFromName.PatientBirthDate);
		patBirthDateAttr.addValue(patientBirthDate);
		list.put(patBirthDateAttr);

		Attribute patSexAttr = new CodeStringAttribute(TagFromName.PatientSex);
		patSexAttr.addValue(patientSex);
		list.put(patSexAttr);

		// === General Study Module ===
		Attribute studyUIDAttr = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
		studyUIDAttr.addValue(studyInstanceUID);
		list.put(studyUIDAttr);

		Attribute studyDateAttr = new DateAttribute(TagFromName.StudyDate);
		studyDateAttr.addValue(currentDate);
		list.put(studyDateAttr);

		Attribute studyTimeAttr = new TimeAttribute(TagFromName.StudyTime);
		studyTimeAttr.addValue(currentTime);
		list.put(studyTimeAttr);

		Attribute accessionAttr = new ShortStringAttribute(TagFromName.AccessionNumber);
		accessionAttr.addValue(accessionNumber);
		list.put(accessionAttr);

		Attribute referringPhysAttr = new PersonNameAttribute(TagFromName.ReferringPhysicianName);
		referringPhysAttr.addValue("Smith^Jane^Dr");
		list.put(referringPhysAttr);

		Attribute studyIdAttr = new ShortStringAttribute(TagFromName.StudyID);
		studyIdAttr.addValue("STUDY001");
		list.put(studyIdAttr);

		// === Encapsulated Document Series Module ===
		Attribute modalityAttr = new CodeStringAttribute(TagFromName.Modality);
		modalityAttr.addValue("DOC"); // Document
		list.put(modalityAttr);

		Attribute seriesUIDAttr = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
		seriesUIDAttr.addValue(UniqueIdentifierAttribute.createUID());
		list.put(seriesUIDAttr);

		Attribute seriesNumAttr = new IntegerStringAttribute(TagFromName.SeriesNumber);
		seriesNumAttr.addValue("1");
		list.put(seriesNumAttr);

		// === SC Equipment Module ===
		Attribute conversionType = new CodeStringAttribute(TagFromName.ConversionType);
		conversionType.addValue("WSD"); // Workstation
		list.put(conversionType);

		// === Encapsulated Document Module ===
		Attribute instanceNumAttr = new IntegerStringAttribute(TagFromName.InstanceNumber);
		instanceNumAttr.addValue("1");
		list.put(instanceNumAttr);

		Attribute contentDateAttr = new DateAttribute(TagFromName.ContentDate);
		contentDateAttr.addValue(currentDate);
		list.put(contentDateAttr);

		Attribute contentTimeAttr = new TimeAttribute(TagFromName.ContentTime);
		contentTimeAttr.addValue(currentTime);
		list.put(contentTimeAttr);

		Attribute acquisitionDateTimeAttr = new com.pixelmed.dicom.DateTimeAttribute(TagFromName.AcquisitionDateTime);
		acquisitionDateTimeAttr.addValue(currentDate + currentTime);
		list.put(acquisitionDateTimeAttr);

		// Burned In Annotation - important for PHI
		Attribute burnedIn = new CodeStringAttribute(TagFromName.BurnedInAnnotation);
		burnedIn.addValue("YES"); // PDF likely contains patient info
		list.put(burnedIn);

		// Document Title
		Attribute docTitleAttr = new LongStringAttribute(TagFromName.DocumentTitle);
		docTitleAttr.addValue(documentTitle);
		list.put(docTitleAttr);

		// MIME Type
		Attribute mimeType = new LongStringAttribute(TagFromName.MIMETypeOfEncapsulatedDocument);
		mimeType.addValue("application/pdf");
		list.put(mimeType);

		// === Encapsulated Document (the actual PDF) ===
		OtherByteAttribute encapDoc = new OtherByteAttribute(TagFromName.EncapsulatedDocument);
		encapDoc.setValues(pdfData);
		list.put(encapDoc);

		return list;
	}

	/**
	 * Read a file into a byte array
	 */
	private static byte[] readFileBytes(String filePath) throws Exception {
		File file = new File(filePath);
		byte[] data = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(data);
		fis.close();
		return data;
	}

	/**
	 * Demonstrate encapsulated PDF structure
	 */
	private static void demonstrateEncapsulatedPdfStructure() {

		System.out.println("\n=== Encapsulated PDF Structure ===\n");

		System.out.println("Key Attributes:");
		System.out.println("  (0008,0016) SOP Class UID = 1.2.840.10008.5.1.4.1.1.104.1");
		System.out.println("  (0008,0060) Modality = DOC");
		System.out.println("  (0008,0064) Conversion Type = WSD");
		System.out.println("  (0028,0301) Burned In Annotation = YES");
		System.out.println("  (0042,0010) Document Title = (your title)");
		System.out.println("  (0042,0012) MIME Type = application/pdf");
		System.out.println("  (0042,0011) Encapsulated Document = (PDF bytes)");

		System.out.println("\n=== Supported Document Types ===");
		System.out.println();
		System.out.println("Encapsulated PDF (1.2.840.10008.5.1.4.1.1.104.1):");
		System.out.println("  MIME Type: application/pdf");
		System.out.println("  Use: Reports, forms, documents");
		System.out.println();
		System.out.println("Encapsulated CDA (1.2.840.10008.5.1.4.1.1.104.2):");
		System.out.println("  MIME Type: text/xml");
		System.out.println("  Use: HL7 Clinical Document Architecture");
		System.out.println();
		System.out.println("Encapsulated STL (1.2.840.10008.5.1.4.1.1.104.3):");
		System.out.println("  MIME Type: model/stl");
		System.out.println("  Use: 3D printing models");
		System.out.println();
		System.out.println("Encapsulated OBJ (1.2.840.10008.5.1.4.1.1.104.4):");
		System.out.println("  MIME Type: model/obj");
		System.out.println("  Use: 3D surface models");
		System.out.println();
		System.out.println("Encapsulated MTL (1.2.840.10008.5.1.4.1.1.104.5):");
		System.out.println("  MIME Type: model/mtl");
		System.out.println("  Use: 3D model materials");

		System.out.println("\n=== Privacy Considerations ===");
		System.out.println("- Burned In Annotation should be YES if PDF contains PHI");
		System.out.println("- PDF text cannot be anonymized by DICOM tools");
		System.out.println("- Consider using redacted PDFs for teaching/research");
		System.out.println("- Document Title should not contain PHI");
	}
}
