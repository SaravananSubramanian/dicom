package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomInputStream;

/**
 * Reading DICOM Directory (DICOMDIR) File Demo
 *
 * PREREQUISITES: Requires a DICOMDIR file. Update 'dicomDirectoryFileName' path.
 * DICOMDIR files are found on medical imaging CDs or can be created with CreatingDicomDirectoryFileDemo.
 *
 * This example demonstrates how to read and parse an existing DICOMDIR
 * file to navigate the contents of a DICOM file-set.
 *
 * Key concepts:
 * - DICOMDIR is read like any other DICOM file (AttributeList)
 * - The DicomDirectory class parses the directory record structure
 * - Records are organized hierarchically (Patient -> Study -> Series -> Image)
 * - Each record contains keys to locate the actual DICOM files
 *
 * Directory Record Types:
 * - PATIENT: PatientID, PatientName, etc.
 * - STUDY: StudyInstanceUID, StudyDate, StudyDescription
 * - SERIES: SeriesInstanceUID, Modality, SeriesNumber
 * - IMAGE: ReferencedSOPInstanceUID, file path reference
 *
 * Navigation workflow:
 * 1. Read DICOMDIR file into AttributeList
 * 2. Parse into DicomDirectory object
 * 3. Traverse the directory record tree
 * 4. Use file references to access actual DICOM objects
 *
 * DICOMDIR files are commonly found on:
 * - Medical imaging CDs/DVDs
 * - USB drives from healthcare facilities
 * - DICOM archive exports
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ReadDicomDirectoryFileDemo {

	public static void main(String[] args) {

		System.out.println("=== Reading DICOM Directory (DICOMDIR) Demo ===\n");

		try {

			// === Configuration ===
			String dicomDirectoryFileName = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images\\dicomdir_example\\DICOMDIR";

			System.out.println("DICOMDIR File: " + dicomDirectoryFileName);
			System.out.println();

			// === Read the DICOMDIR File ===
			System.out.println("Reading DICOMDIR...\n");

			// Step 1: Read the file into an AttributeList
			// DICOMDIR is a standard DICOM file with specific attributes
			AttributeList attributeList = new AttributeList();
			attributeList.read(new DicomInputStream(new File(dicomDirectoryFileName)));

			// Step 2: Parse the AttributeList into a DicomDirectory
			// This interprets the Directory Record Sequence
			DicomDirectory dicomDirectory = new DicomDirectory(attributeList);

			// === Display Directory Contents ===
			System.out.println("=== DICOMDIR Contents ===\n");
			System.out.println(dicomDirectory.toString());

			System.out.println("\n=== DICOMDIR Read Successfully ===");

			// In a real application, you would typically:
			// 1. Traverse the directory records to find specific studies
			// 2. Extract file references from IMAGE records
			// 3. Read the actual DICOM files using those references

		} catch (Exception e) {
			System.err.println("\nFailed to read DICOMDIR!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nCommon causes:");
			System.err.println("  - DICOMDIR file not found");
			System.err.println("  - Invalid or corrupted DICOMDIR");
			System.err.println("  - Not a valid DICOM file");
			e.printStackTrace();
		}
	}
}
