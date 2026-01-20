package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;

import com.pixelmed.display.ConsumerFormatImageMaker;

/**
 * Multi-frame DICOM to Standard Image Format Export Demo
 *
 * This example demonstrates how to convert multi-frame DICOM images
 * (such as X-ray angiography or ultrasound cine loops) to standard
 * image formats.
 *
 * Key concepts:
 * - Multi-frame DICOM files contain multiple images in a single file
 * - Common in XA (angiography), US (ultrasound), and MR (some sequences)
 * - Each frame is exported as a separate image file
 * - Frame numbers are automatically appended to output filenames
 *
 * Output naming convention:
 * If output file is "XA-MONO2-8-12x-catheter.jpg" and there are 16 frames:
 * - XA-MONO2-8-12x-catheter_001.jpg
 * - XA-MONO2-8-12x-catheter_002.jpg
 * - ... (through frame 16)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ExportMultiFrameDicomImageDataToOtherImageFormats {

	public static void main(String[] args) {

		// === Configuration ===
		String inputDirectory = "C:\\temp\\sample_images";
		String outputDirectory = inputDirectory + File.separator + "multiframe_exports";

		// Multi-frame DICOM file (X-ray angiography with 16 frames)
		String dicomFile = inputDirectory + File.separator + "XA-MONO2-8-12x-catheter";

		// Output file template (frame numbers will be appended)
		String outputJpgFile = outputDirectory + File.separator + "XA-MONO2-8-12x-catheter.jpg";

		try {

			System.out.println("=== Multi-frame DICOM Export Demo ===\n");

			// Create output directory if it doesn't exist
			new File(outputDirectory).mkdirs();

			System.out.println("Input DICOM file: " + dicomFile);
			System.out.println("Output directory: " + outputDirectory);
			System.out.println();

			// === Export all frames to JPEG ===
			// When frameNumber is 0, all frames are exported automatically
			// Each frame becomes a separate file with frame number appended
			System.out.println("Exporting all frames to JPEG...");
			System.out.println("(This may take a moment for large multi-frame files)");

			ConsumerFormatImageMaker.convertFileToEightBitImage(
				dicomFile,      // Input multi-frame DICOM file
				outputJpgFile,  // Output file path template
				"jpeg",         // Output format
				0               // Frame number: 0 = export all frames
			);

			System.out.println("\nExport complete!");
			System.out.println("Check the output directory for individual frame files.");
			System.out.println("Files are named with frame numbers: filename_001.jpg, filename_002.jpg, etc.");

			// === Optional: Export specific frame ===
			System.out.println("\n--- Exporting Specific Frame ---");
			String singleFrameOutput = outputDirectory + File.separator + "XA-frame5-only.jpg";

			// Export only frame 5 (frame numbers are 1-based)
			ConsumerFormatImageMaker.convertFileToEightBitImage(
				dicomFile,
				singleFrameOutput,
				"jpeg",
				5  // Export frame 5 only
			);

			System.out.println("Frame 5 exported to: " + singleFrameOutput);

		} catch (Exception e) {
			System.err.println("Error exporting multi-frame DICOM file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
