package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.ImageToDicom;

/**
 * DICOM File Creation Demo
 *
 * PREREQUISITES: Requires a source JPEG image file. Update 'sourceJpegFile' path.
 * Output directory (C:\temp\sample_images) must exist or be created.
 *
 * This example demonstrates how to create a DICOM file from a standard
 * image format (JPEG) using the PixelMed library.
 *
 * Key concepts:
 * - DICOM files contain both pixel data and metadata (patient info, study info, etc.)
 * - The ImageToDicom utility wraps an image with required DICOM attributes
 * - Minimum required attributes include patient name, ID, study/series/instance identifiers
 *
 * Common use cases:
 * - Converting scanned documents to DICOM for PACS storage
 * - Creating Secondary Capture images from screenshots
 * - Importing non-DICOM images into a DICOM workflow
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreateADicomFile {

	public static void main(String[] args) {

		// === Input/Output Configuration ===
		String sourceJpegFile = "C:\\temp\\sample_images\\ChestGeneralImage.jpg";
		String outputDicomFile = "C:\\temp\\sample_images\\NewDicomFile.dcm";
		String outputTextFile = "C:\\temp\\dicom_contents.txt";

		try {

			System.out.println("=== DICOM File Creation Demo ===\n");
			System.out.println("Source JPEG: " + sourceJpegFile);
			System.out.println("Output DICOM: " + outputDicomFile);

			// === Create DICOM from JPEG ===
			// The ImageToDicom utility creates a Secondary Capture DICOM object
			// from an existing image file with the specified patient/study metadata
			System.out.println("\nCreating DICOM file...");

			new ImageToDicom(
				sourceJpegFile,           // Path to source JPEG image
				outputDicomFile,          // Output DICOM file path
				"Doe^John",               // Patient Name (DICOM format: Last^First^Middle)
				"PAT123456",              // Patient ID
				"STUDY001",               // Study ID
				"1",                      // Series Number
				"1"                       // Instance Number
			);

			System.out.println("DICOM file created successfully.");

			// === Read and Display the Created File ===
			System.out.println("\nReading created DICOM file...");

			AttributeList list = new AttributeList();
			list.read(outputDicomFile);

			// Display to console
			System.out.println("\n--- DICOM File Contents ---");
			System.out.println(list.toString());

			// === Save Contents to Text File ===
			File textFile = new File(outputTextFile);
			textFile.getParentFile().mkdirs(); // Create directories if needed

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFile))) {
				writer.write(list.toString());
			}

			System.out.println("\nDICOM contents saved to: " + outputTextFile);

		} catch (Exception e) {
			System.err.println("Error creating DICOM file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
