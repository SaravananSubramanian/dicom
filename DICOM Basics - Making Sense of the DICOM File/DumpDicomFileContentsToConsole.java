package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.AttributeList;

/**
 * DICOM File Contents Dump Demo
 *
 * PREREQUISITES: Requires a DICOM file. Update 'dicomFile' variable with your path.
 * Sample files: https://barre.dev/medical/samples/ or https://www.dicomlibrary.com/
 *
 * This example demonstrates the simplest way to read a DICOM file
 * and display all its contents (attributes/tags) to the console.
 *
 * Key concepts:
 * - AttributeList is the main class for reading DICOM data
 * - The toString() method provides human-readable output
 * - All tags are displayed in (group,element) format with values
 *
 * This is useful for:
 * - Quick inspection of DICOM file contents
 * - Debugging DICOM data issues
 * - Understanding the structure of DICOM files
 * - Learning which tags are present in different modalities
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DumpDicomFileContentsToConsole {

	public static void main(String[] args) {

		// === Configuration ===
		String dicomFile = "C:\\temp\\sample_images\\MR-MONO2-16-head";

		try {

			System.out.println("=== DICOM File Contents Dump Demo ===\n");
			System.out.println("Reading file: " + dicomFile);
			System.out.println();

			// === Read and Display DICOM Contents ===
			AttributeList list = new AttributeList();
			list.read(dicomFile);

			// The toString() method displays all attributes in a readable format
			// Format: (gggg,eeee) VR <tag name> = <value(s)>
			System.out.println("--- Complete DICOM File Contents ---\n");
			System.out.println(list.toString());

		} catch (Exception e) {
			System.err.println("Error reading DICOM file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
