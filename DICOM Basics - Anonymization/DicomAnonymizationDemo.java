package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UIDGenerator;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

/**
 * DICOM Anonymization/De-identification Demo
 *
 * PREREQUISITES: Requires a DICOM file to anonymize. Update 'inputFile' path.
 * Output directory (C:\temp) must exist.
 * If file not found, demo displays anonymization concepts.
 *
 * De-identification is the process of removing or replacing Protected Health
 * Information (PHI) from DICOM objects to enable sharing for research,
 * teaching, or publication.
 *
 * DICOM PS3.15 Annex E defines the standard for de-identification:
 * - Basic Profile: Minimum de-identification
 * - Additional Options: Retain dates, retain device identity, etc.
 *
 * Categories of PHI in DICOM:
 * - Direct identifiers: Patient name, ID, address, phone, SSN
 * - Indirect identifiers: Dates, ages, locations
 * - Embedded data: Burned-in annotations, structured report text
 * - UIDs: Can be used for re-identification
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomAnonymizationDemo {

	// Map to maintain consistent UID replacement
	private static Map<String, String> uidMap = new HashMap<>();

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Anonymization Demo ===\n");

			String inputFile = "C:\\path\\to\\original.dcm";
			String outputFile = "C:\\temp\\anonymized.dcm";

			// Read the DICOM file
			AttributeList list = new AttributeList();
			list.read(inputFile);

			System.out.println("Original values:");
			printPHI(list);

			// Perform anonymization
			System.out.println("\nAnonymizing...");
			anonymizeBasicProfile(list);

			System.out.println("\nAnonymized values:");
			printPHI(list);

			// Update file meta information and save
			list.removeMetaInformationHeaderAttributes();
			FileMetaInformation.addFileMetaInformation(list,
				TransferSyntax.ExplicitVRLittleEndian, "ANONYMIZER");
			list.write(outputFile);

			System.out.println("\nAnonymized file saved to: " + outputFile);

		} catch (Exception e) {
			System.out.println("Note: Update 'inputFile' with an actual DICOM file path.");
			System.out.println("Error: " + e.getMessage());
			demonstrateAnonymizationConcepts();
		}
	}

	/**
	 * Anonymize using DICOM Basic Profile (simplified)
	 */
	private static void anonymizeBasicProfile(AttributeList list) throws Exception {

		// === Direct Identifiers - Remove or Replace ===

		// Patient Name
		replaceAttribute(list, TagFromName.PatientName, new PersonNameAttribute(TagFromName.PatientName),
			"ANONYMOUS");

		// Patient ID
		replaceAttribute(list, TagFromName.PatientID, new LongStringAttribute(TagFromName.PatientID),
			generateAnonymousID());

		// Patient Birth Date - Remove or shift
		list.remove(TagFromName.PatientBirthDate);

		// Patient Address
		list.remove(TagFromName.PatientAddress);

		// Patient Telephone Numbers
		list.remove(TagFromName.PatientTelephoneNumbers);

		// Other Patient IDs
		list.remove(TagFromName.OtherPatientIDs);
		list.remove(TagFromName.OtherPatientIDsSequence);

		// === Dates - Can be removed or shifted ===

		// Study Date - Option: shift by random offset while preserving relative timing
		// For this demo, we'll keep dates (common in research)

		// === UIDs - Replace with new UIDs ===

		// Study Instance UID
		replaceUID(list, TagFromName.StudyInstanceUID);

		// Series Instance UID
		replaceUID(list, TagFromName.SeriesInstanceUID);

		// SOP Instance UID
		replaceUID(list, TagFromName.SOPInstanceUID);

		// Frame of Reference UID
		replaceUID(list, TagFromName.FrameOfReferenceUID);

		// === Institution/Device Information ===

		// Institution Name
		replaceAttribute(list, TagFromName.InstitutionName,
			new LongStringAttribute(TagFromName.InstitutionName), "ANONYMOUS INSTITUTION");

		// Institution Address
		list.remove(TagFromName.InstitutionAddress);

		// Station Name
		replaceAttribute(list, TagFromName.StationName,
			new ShortStringAttribute(TagFromName.StationName), "ANON_STATION");

		// Device Serial Number
		list.remove(TagFromName.DeviceSerialNumber);

		// === Physician Information ===

		// Referring Physician
		replaceAttribute(list, TagFromName.ReferringPhysicianName,
			new PersonNameAttribute(TagFromName.ReferringPhysicianName), "ANONYMOUS^PHYSICIAN");

		// Performing Physician
		list.remove(TagFromName.PerformingPhysicianName);

		// Operators' Name
		list.remove(TagFromName.OperatorsName);

		// Requesting Physician
		list.remove(TagFromName.RequestingPhysician);

		// === Accession Number ===
		replaceAttribute(list, TagFromName.AccessionNumber,
			new ShortStringAttribute(TagFromName.AccessionNumber), generateAnonymousAccession());

		// === Study/Series Description - May contain PHI ===
		// Review and clean if necessary
		// list.remove(TagFromName.StudyDescription);
		// list.remove(TagFromName.SeriesDescription);

		// === Remove Private Tags (may contain PHI) ===
		list.removePrivateAttributes();

		// === Add De-identification Tags ===
		Attribute deidentMethod = new LongStringAttribute(TagFromName.DeidentificationMethod);
		deidentMethod.addValue("Basic Profile");
		list.put(deidentMethod);

		Attribute patientIdentityRemoved = new CodeStringAttribute(TagFromName.PatientIdentityRemoved);
		patientIdentityRemoved.addValue("YES");
		list.put(patientIdentityRemoved);
	}

	/**
	 * Replace a UID with a new one, maintaining mapping for consistency
	 */
	private static void replaceUID(AttributeList list, AttributeTag tag) throws Exception {
		Attribute attr = list.get(tag);
		if (attr != null) {
			String originalUID = attr.getSingleStringValueOrNull();
			if (originalUID != null) {
				String newUID = uidMap.get(originalUID);
				if (newUID == null) {
					newUID = new UIDGenerator().getNewUID();
					uidMap.put(originalUID, newUID);
				}

				Attribute newAttr = new UniqueIdentifierAttribute(tag);
				newAttr.addValue(newUID);
				list.put(newAttr);
			}
		}
	}

	/**
	 * Replace an attribute with a new value
	 */
	private static void replaceAttribute(AttributeList list, AttributeTag tag,
			Attribute newAttr, String value) throws Exception {
		if (list.get(tag) != null) {
			newAttr.addValue(value);
			list.put(newAttr);
		}
	}

	/**
	 * Generate an anonymous patient ID
	 */
	private static String generateAnonymousID() {
		return "ANON" + System.currentTimeMillis();
	}

	/**
	 * Generate an anonymous accession number
	 */
	private static String generateAnonymousAccession() {
		return "ACC" + System.currentTimeMillis();
	}

	/**
	 * Print PHI-relevant attributes
	 */
	private static void printPHI(AttributeList list) {
		printAttribute(list, "Patient Name", TagFromName.PatientName);
		printAttribute(list, "Patient ID", TagFromName.PatientID);
		printAttribute(list, "Birth Date", TagFromName.PatientBirthDate);
		printAttribute(list, "Institution", TagFromName.InstitutionName);
		printAttribute(list, "Accession #", TagFromName.AccessionNumber);
		printAttribute(list, "Study UID", TagFromName.StudyInstanceUID);
	}

	private static void printAttribute(AttributeList list, String label, AttributeTag tag) {
		Attribute attr = list.get(tag);
		String value = (attr != null) ? attr.getSingleStringValueOrNull() : "(not present)";
		System.out.println("  " + label + ": " + value);
	}

	/**
	 * Demonstrate anonymization concepts
	 */
	private static void demonstrateAnonymizationConcepts() {

		System.out.println("\n=== DICOM Anonymization Concepts ===\n");

		System.out.println("--- DICOM PS3.15 Basic Profile Actions ---");
		System.out.println("D  - Replace with dummy value");
		System.out.println("Z  - Replace with zero-length value");
		System.out.println("X  - Remove the attribute");
		System.out.println("K  - Keep (may be retained)");
		System.out.println("C  - Clean (remove if contains PHI)");
		System.out.println("U  - Replace UID");

		System.out.println("\n--- Common PHI Attributes ---");
		System.out.println("(0010,0010) Patient Name - D");
		System.out.println("(0010,0020) Patient ID - Z");
		System.out.println("(0010,0030) Patient Birth Date - X");
		System.out.println("(0010,0040) Patient Sex - K");
		System.out.println("(0008,0050) Accession Number - Z");
		System.out.println("(0008,0080) Institution Name - X/D");
		System.out.println("(0008,0090) Referring Physician - X");
		System.out.println("(0020,000D) Study Instance UID - U");
		System.out.println("(0020,000E) Series Instance UID - U");
		System.out.println("(0008,0018) SOP Instance UID - U");

		System.out.println("\n--- De-identification Options ---");
		System.out.println("Retain Longitudinal Temporal Information:");
		System.out.println("  - Keep dates but shift by consistent offset");
		System.out.println("  - Useful for research tracking patient over time");
		System.out.println();
		System.out.println("Retain Patient Characteristics:");
		System.out.println("  - Keep age, sex, ethnicity");
		System.out.println("  - Useful for demographic analysis");
		System.out.println();
		System.out.println("Retain Device Identity:");
		System.out.println("  - Keep device/manufacturer info");
		System.out.println("  - Useful for equipment studies");
		System.out.println();
		System.out.println("Retain UIDs:");
		System.out.println("  - Keep original UIDs");
		System.out.println("  - Risk: can be used for re-identification");

		System.out.println("\n--- Burned-in Annotations ---");
		System.out.println("IMPORTANT: Check (0028,0301) Burned In Annotation attribute");
		System.out.println("If 'YES', pixel data may contain PHI that cannot be");
		System.out.println("removed by attribute-level de-identification.");
		System.out.println("Consider OCR detection or exclusion of such images.");

		System.out.println("\n--- Verification ---");
		System.out.println("After anonymization, verify:");
		System.out.println("1. No PHI in standard attributes");
		System.out.println("2. Private tags removed or cleaned");
		System.out.println("3. Pixel data free of burned-in PHI");
		System.out.println("4. UIDs consistently replaced");
		System.out.println("5. De-identification method documented");
	}
}
