package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;

import com.pixelmed.display.ConsumerFormatImageMaker;

/**
 * DICOM to Standard Image Format Export Demo
 *
 * PREREQUISITES: Requires a DICOM image file. Update 'dicomFile' variable.
 * Output directory will be created automatically if it doesn't exist.
 *
 * This example demonstrates how to convert DICOM images to standard
 * consumer image formats (JPEG, PNG, TIFF) for use outside DICOM systems.
 *
 * Key concepts:
 * - ConsumerFormatImageMaker handles the conversion process
 * - Window/level is applied during conversion (affects brightness/contrast)
 * - Output is 8-bit regardless of original bit depth
 * - Useful for creating thumbnails, web display, or sharing with non-DICOM systems
 *
 * Supported output formats:
 * - jpeg: JPEG format (lossy compression, small files)
 * - png: PNG format (lossless compression, transparency support)
 * - tiff: TIFF format (lossless, professional/archival use)
 *
 * Note: For multi-frame DICOM files, multiple output files are created
 * (one per frame), with frame numbers appended to the filename.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ExportDicomImageDataToOtherImageFormats {

	public static void main(String[] args) {

		// === Configuration ===
		String inputDirectory = "C:\\temp\\sample_images";
		String outputDirectory = inputDirectory + File.separator + "exports";

		// Input DICOM file (single frame image)
		String dicomFile = inputDirectory + File.separator + "CT-MONO2-16-ankle";

		// Output files in different formats
		String outputJpgFile = outputDirectory + File.separator + "CT-ankle.jpg";
		String outputPngFile = outputDirectory + File.separator + "CT-ankle.png";
		String outputTiffFile = outputDirectory + File.separator + "CT-ankle.tiff";

		try {

			System.out.println("=== DICOM to Standard Image Format Export Demo ===\n");

			// Create output directory if it doesn't exist
			new File(outputDirectory).mkdirs();

			System.out.println("Input DICOM file: " + dicomFile);
			System.out.println("Output directory: " + outputDirectory);
			System.out.println();

			// === Export to JPEG ===
			System.out.println("Exporting to JPEG...");
			ConsumerFormatImageMaker.convertFileToEightBitImage(
				dicomFile,      // Input DICOM file
				outputJpgFile,  // Output file path
				"jpeg",         // Output format
				0               // Frame number (0 = first/all frames)
			);
			System.out.println("  Created: " + outputJpgFile);

			// === Export to PNG ===
			System.out.println("Exporting to PNG...");
			ConsumerFormatImageMaker.convertFileToEightBitImage(
				dicomFile,
				outputPngFile,
				"png",
				0
			);
			System.out.println("  Created: " + outputPngFile);

			// === Export to TIFF ===
			System.out.println("Exporting to TIFF...");
			ConsumerFormatImageMaker.convertFileToEightBitImage(
				dicomFile,
				outputTiffFile,
				"tiff",
				0
			);
			System.out.println("  Created: " + outputTiffFile);

			System.out.println("\nExport complete!");
			System.out.println("\nNote: Window/level values are automatically applied");
			System.out.println("during conversion to optimize display appearance.");

		} catch (Exception e) {
			System.err.println("Error exporting DICOM file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
