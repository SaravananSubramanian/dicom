package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;

/**
 * DICOM Compression and Decompression Demo
 *
 * PREREQUISITES: Requires a DICOM image file with pixel data. Update 'inputFile' path.
 * Output directory (C:\temp) must exist. If file not found, demo shows concepts.
 * Note: Some compression codecs may require additional libraries.
 *
 * This example demonstrates how to compress and decompress DICOM files
 * using different transfer syntaxes.
 *
 * Important considerations:
 * - Lossy compression should NEVER be applied to diagnostic images
 * - Lossy compression is acceptable for teaching files, thumbnails, web display
 * - Original uncompressed copy should be preserved for legal purposes
 * - Some modalities (ultrasound, angiography) may arrive already compressed
 *
 * PixelMed supports:
 * - Reading most compressed formats
 * - Writing to various compression formats (with codec support)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CompressDecompressDicomDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Compression/Decompression Demo ===\n");

			String inputFile = "C:\\path\\to\\input.dcm";

			// Read the DICOM file (PixelMed automatically decompresses)
			System.out.println("Reading DICOM file: " + inputFile);
			AttributeList list = new AttributeList();
			list.read(inputFile);

			String originalTS = list.get(TagFromName.TransferSyntaxUID).getSingleStringValueOrNull();
			System.out.println("Original Transfer Syntax: " + originalTS);

			// Get pixel data to verify it was decompressed
			long pixelDataLength = 0;
			if (list.get(TagFromName.PixelData) != null) {
				pixelDataLength = list.get(TagFromName.PixelData).getVL();
				System.out.println("Pixel Data Length: " + pixelDataLength + " bytes");
			}

			// Demo 1: Decompress to explicit VR little endian
			System.out.println("\n--- Demo 1: Decompress to Explicit VR LE ---");
			saveWithTransferSyntax(list, TransferSyntax.ExplicitVRLittleEndian,
				"C:\\temp\\decompressed_explicit.dcm");

			// Demo 2: Compress with RLE (lossless)
			System.out.println("\n--- Demo 2: Compress with RLE (lossless) ---");
			saveWithTransferSyntax(list, TransferSyntax.RLE,
				"C:\\temp\\compressed_rle.dcm");

			// Demo 3: Compress with JPEG Lossless
			System.out.println("\n--- Demo 3: Compress with JPEG Lossless ---");
			saveWithTransferSyntax(list, TransferSyntax.JPEGLosslessSV1,
				"C:\\temp\\compressed_jpeg_lossless.dcm");

			// Demo 4: Compress with JPEG 2000 Lossless
			System.out.println("\n--- Demo 4: Compress with JPEG 2000 Lossless ---");
			saveWithTransferSyntax(list, TransferSyntax.JPEG2000Lossless,
				"C:\\temp\\compressed_j2k_lossless.dcm");

			System.out.println("\n=== Compression Demo Complete ===");

		} catch (Exception e) {
			System.out.println("Note: Update 'inputFile' with an actual DICOM file path.");
			System.out.println("Error: " + e.getMessage());
			demonstrateCompressionConcepts();
		}
	}

	/**
	 * Save the attribute list with a specific transfer syntax
	 */
	private static void saveWithTransferSyntax(AttributeList list, String transferSyntax,
			String outputPath) {

		try {
			// Clone the list to avoid modifying the original
			AttributeList outputList = (AttributeList) list.clone();

			// Remove existing file meta information
			outputList.removeMetaInformationHeaderAttributes();

			// Add new file meta information with target transfer syntax
			FileMetaInformation.addFileMetaInformation(outputList, transferSyntax, "OurAET");

			// Write the file
			outputList.write(outputPath);

			// Report file size
			File outputFile = new File(outputPath);
			System.out.println("Saved: " + outputPath);
			System.out.println("Size: " + outputFile.length() + " bytes");

		} catch (DicomException e) {
			System.out.println("Warning: Could not save with " + transferSyntax);
			System.out.println("Reason: " + e.getMessage());
			System.out.println("(Codec may not be available)");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Demonstrate compression concepts
	 */
	private static void demonstrateCompressionConcepts() {

		System.out.println("\n=== DICOM Compression Concepts ===\n");

		System.out.println("--- Lossless vs Lossy ---");
		System.out.println("Lossless: Original pixel values preserved exactly");
		System.out.println("  - Required for diagnostic interpretation");
		System.out.println("  - Typical ratio: 2:1 to 3:1");
		System.out.println("  - Examples: RLE, JPEG Lossless, JPEG 2000 Lossless");
		System.out.println();
		System.out.println("Lossy: Some pixel information lost");
		System.out.println("  - Acceptable for teaching, web display, thumbnails");
		System.out.println("  - Typical ratio: 10:1 to 50:1");
		System.out.println("  - Examples: JPEG Baseline, JPEG 2000 Lossy");

		System.out.println("\n--- Compression Algorithms ---");
		System.out.println();
		System.out.println("RLE (Run-Length Encoding):");
		System.out.println("  - Simple, fast, universally supported");
		System.out.println("  - Good for images with large uniform areas");
		System.out.println("  - Less effective for noisy images");
		System.out.println();
		System.out.println("JPEG Lossless (Process 14, SV1):");
		System.out.println("  - Recommended lossless format");
		System.out.println("  - Widely supported");
		System.out.println("  - Good compression for most modalities");
		System.out.println();
		System.out.println("JPEG 2000:");
		System.out.println("  - Modern wavelet-based compression");
		System.out.println("  - Better quality at same compression ratio");
		System.out.println("  - Progressive decoding support");
		System.out.println("  - Less universal support");
		System.out.println();
		System.out.println("JPEG-LS:");
		System.out.println("  - Designed specifically for medical imaging");
		System.out.println("  - Very fast encoding/decoding");
		System.out.println("  - Limited support in viewers");

		System.out.println("\n--- Best Practices ---");
		System.out.println("1. NEVER apply lossy compression to diagnostic images");
		System.out.println("2. Keep original copies for legal compliance");
		System.out.println("3. Use JPEG Lossless SV1 for maximum compatibility");
		System.out.println("4. Use JPEG 2000 Lossless for newer systems");
		System.out.println("5. Test viewer compatibility before deployment");
		System.out.println("6. Consider storage vs bandwidth tradeoffs");

		System.out.println("\n--- DICOM Lossy Compression Attributes ---");
		System.out.println("(0028,2110) Lossy Image Compression - '00' or '01'");
		System.out.println("(0028,2112) Lossy Image Compression Ratio");
		System.out.println("(0028,2114) Lossy Image Compression Method");
	}
}
