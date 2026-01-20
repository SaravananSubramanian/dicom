package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;

/**
 * DICOM Transfer Syntax Conversion Demo
 *
 * PREREQUISITES: Requires DICOM files. Update 'inputFile' and 'outputFile' paths.
 * If file not found, demo displays transfer syntax reference information.
 *
 * Transfer Syntax defines how DICOM data is encoded (VR, byte ordering, compression).
 *
 * Common Transfer Syntaxes:
 *
 * Uncompressed:
 * - 1.2.840.10008.1.2    - Implicit VR Little Endian (default)
 * - 1.2.840.10008.1.2.1  - Explicit VR Little Endian
 * - 1.2.840.10008.1.2.2  - Explicit VR Big Endian (retired)
 *
 * Lossless Compression:
 * - 1.2.840.10008.1.2.5       - RLE Lossless
 * - 1.2.840.10008.1.2.4.57    - JPEG Lossless (Process 14)
 * - 1.2.840.10008.1.2.4.70    - JPEG Lossless (Process 14, Selection Value 1)
 * - 1.2.840.10008.1.2.4.90    - JPEG 2000 Lossless Only
 * - 1.2.840.10008.1.2.4.80    - JPEG-LS Lossless
 *
 * Lossy Compression:
 * - 1.2.840.10008.1.2.4.50    - JPEG Baseline (8-bit)
 * - 1.2.840.10008.1.2.4.51    - JPEG Extended (12-bit)
 * - 1.2.840.10008.1.2.4.91    - JPEG 2000 (lossy)
 * - 1.2.840.10008.1.2.4.81    - JPEG-LS Near-Lossless
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class TransferSyntaxConversionDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Transfer Syntax Conversion Demo ===\n");

			String inputFile = "C:\\path\\to\\input.dcm";
			String outputFile = "C:\\path\\to\\output.dcm";

			// Read the DICOM file
			AttributeList list = new AttributeList();
			list.read(inputFile);

			// Get current transfer syntax
			String currentTS = list.get(TagFromName.TransferSyntaxUID).getSingleStringValueOrNull();
			System.out.println("Current Transfer Syntax: " + currentTS);
			System.out.println("Description: " + getTransferSyntaxDescription(currentTS));

			// Convert to different transfer syntax
			String targetTS = TransferSyntax.ExplicitVRLittleEndian;
			System.out.println("\nConverting to: " + targetTS);
			System.out.println("Description: " + getTransferSyntaxDescription(targetTS));

			// Remove old file meta information
			list.removeMetaInformationHeaderAttributes();

			// Add new file meta information with target transfer syntax
			FileMetaInformation.addFileMetaInformation(list, targetTS, "OurSourceAET");

			// Write the file
			list.write(outputFile);

			System.out.println("\nFile saved to: " + outputFile);

		} catch (Exception e) {
			System.out.println("Note: Update file paths with actual DICOM files.");
			System.out.println("Error: " + e.getMessage());
			demonstrateTransferSyntaxes();
		}
	}

	/**
	 * Get a human-readable description of a transfer syntax UID
	 */
	public static String getTransferSyntaxDescription(String tsUID) {
		if (tsUID == null) return "Unknown";

		switch (tsUID) {
			// Uncompressed
			case "1.2.840.10008.1.2":
				return "Implicit VR Little Endian";
			case "1.2.840.10008.1.2.1":
				return "Explicit VR Little Endian";
			case "1.2.840.10008.1.2.1.99":
				return "Deflated Explicit VR Little Endian";
			case "1.2.840.10008.1.2.2":
				return "Explicit VR Big Endian (Retired)";

			// JPEG
			case "1.2.840.10008.1.2.4.50":
				return "JPEG Baseline (Lossy, 8-bit)";
			case "1.2.840.10008.1.2.4.51":
				return "JPEG Extended (Lossy, 12-bit)";
			case "1.2.840.10008.1.2.4.57":
				return "JPEG Lossless (Process 14)";
			case "1.2.840.10008.1.2.4.70":
				return "JPEG Lossless (Process 14, SV1) - Recommended";

			// JPEG 2000
			case "1.2.840.10008.1.2.4.90":
				return "JPEG 2000 Lossless Only";
			case "1.2.840.10008.1.2.4.91":
				return "JPEG 2000 (Lossy)";

			// JPEG-LS
			case "1.2.840.10008.1.2.4.80":
				return "JPEG-LS Lossless";
			case "1.2.840.10008.1.2.4.81":
				return "JPEG-LS Near-Lossless";

			// RLE
			case "1.2.840.10008.1.2.5":
				return "RLE Lossless";

			// MPEG
			case "1.2.840.10008.1.2.4.100":
				return "MPEG2 Main Profile @ Main Level";
			case "1.2.840.10008.1.2.4.101":
				return "MPEG2 Main Profile @ High Level";
			case "1.2.840.10008.1.2.4.102":
				return "MPEG-4 AVC/H.264 High Profile";
			case "1.2.840.10008.1.2.4.103":
				return "MPEG-4 AVC/H.264 BD-compatible";

			// HEVC
			case "1.2.840.10008.1.2.4.107":
				return "HEVC/H.265 Main Profile";
			case "1.2.840.10008.1.2.4.108":
				return "HEVC/H.265 Main 10 Profile";

			default:
				return "Unknown Transfer Syntax";
		}
	}

	/**
	 * Demonstrate transfer syntax information
	 */
	private static void demonstrateTransferSyntaxes() {

		System.out.println("\n=== Common DICOM Transfer Syntaxes ===\n");

		System.out.println("--- Uncompressed ---");
		System.out.println(TransferSyntax.ImplicitVRLittleEndian + " - Implicit VR Little Endian (default)");
		System.out.println(TransferSyntax.ExplicitVRLittleEndian + " - Explicit VR Little Endian");
		System.out.println(TransferSyntax.ExplicitVRBigEndian + " - Explicit VR Big Endian (retired)");

		System.out.println("\n--- Lossless Compression ---");
		System.out.println(TransferSyntax.RLE + " - RLE Lossless");
		System.out.println(TransferSyntax.JPEGLossless + " - JPEG Lossless (Process 14)");
		System.out.println(TransferSyntax.JPEGLosslessSV1 + " - JPEG Lossless SV1 (recommended)");
		System.out.println(TransferSyntax.JPEG2000Lossless + " - JPEG 2000 Lossless");

		System.out.println("\n--- Lossy Compression ---");
		System.out.println(TransferSyntax.JPEGBaseline + " - JPEG Baseline (8-bit)");
		System.out.println(TransferSyntax.JPEG2000 + " - JPEG 2000 (lossy)");

		System.out.println("\n--- Choosing Transfer Syntax ---");
		System.out.println("For archival: Use lossless (JPEG Lossless SV1 or JPEG 2000 Lossless)");
		System.out.println("For display/web: Use lossy for smaller files (JPEG Baseline, JPEG 2000)");
		System.out.println("For compatibility: Use Explicit VR Little Endian (uncompressed)");
		System.out.println("For legal compliance: Never use lossy for diagnostic images");

		System.out.println("\n--- Compression Ratios (approximate) ---");
		System.out.println("RLE: 2:1 to 3:1");
		System.out.println("JPEG Lossless: 2:1 to 3:1");
		System.out.println("JPEG 2000 Lossless: 2:1 to 3:1");
		System.out.println("JPEG Baseline (lossy): 10:1 to 20:1");
		System.out.println("JPEG 2000 (lossy): 10:1 to 50:1");
	}
}
