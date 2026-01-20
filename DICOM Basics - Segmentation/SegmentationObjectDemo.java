package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.SOPClass;

/**
 * DICOM Segmentation Object Demo
 *
 * DICOM Segmentation objects store image segmentation results - masks
 * identifying specific regions or structures in medical images.
 *
 * Use Cases:
 * - AI/ML model outputs (tumor segmentation, organ detection)
 * - Manual annotations for training data
 * - Volume measurements
 * - Treatment planning assistance
 * - Research and quantitative analysis
 *
 * SOP Class: Segmentation Storage (1.2.840.10008.5.1.4.1.1.66.4)
 *
 * Key Concepts:
 * - Each segment is a labeled region (e.g., "Liver", "Tumor")
 * - Segments have categories and types from standard codes
 * - Binary or fractional representation
 * - Multi-frame object with one frame per source image slice
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class SegmentationObjectDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM Segmentation Object Demo ===\n");

		System.out.println("--- Segmentation SOP Class ---");
		System.out.println("Segmentation Storage: " + SOPClass.SegmentationStorage);
		System.out.println();

		// Demonstrate segmentation structure
		demonstrateSegmentationStructure();

		// Demonstrate segment sequence
		demonstrateSegmentSequence();

		// Demonstrate segmentation types
		demonstrateSegmentationTypes();

		// Demonstrate common use cases
		demonstrateUseCases();
	}

	/**
	 * Demonstrate segmentation IOD structure
	 */
	private static void demonstrateSegmentationStructure() {

		System.out.println("--- Segmentation IOD Structure ---\n");

		System.out.println("Key Modules:");
		System.out.println("  Patient Module - Patient demographics");
		System.out.println("  General Study Module - Study information");
		System.out.println("  Segmentation Series Module:");
		System.out.println("    Modality = SEG");
		System.out.println("    Series Description = (description)");
		System.out.println("  Multi-frame Dimension Module:");
		System.out.println("    Dimension Organization Sequence");
		System.out.println("    Dimension Index Sequence");
		System.out.println("  Segmentation Image Module:");
		System.out.println("    Segmentation Type = BINARY or FRACTIONAL");
		System.out.println("    Segment Sequence (defines each segment)");
		System.out.println("  Multi-frame Functional Groups Module:");
		System.out.println("    Per-Frame Functional Groups Sequence");
		System.out.println("  Pixel Data Module:");
		System.out.println("    Pixel Data (the actual mask data)");
		System.out.println();
	}

	/**
	 * Demonstrate segment sequence structure
	 */
	private static void demonstrateSegmentSequence() {

		System.out.println("--- Segment Sequence Example ---\n");

		System.out.println("Segment Sequence (0062,0002):");
		System.out.println();
		System.out.println("  Segment 1:");
		System.out.println("    Segment Number: 1");
		System.out.println("    Segment Label: Liver");
		System.out.println("    Segment Description: Liver parenchyma segmentation");
		System.out.println("    Segment Algorithm Type: AUTOMATIC");
		System.out.println("    Segment Algorithm Name: AI Liver Seg v2.0");
		System.out.println("    Segmented Property Category Code Sequence:");
		System.out.println("      Code Value: T-D0050");
		System.out.println("      Coding Scheme: SRT");
		System.out.println("      Code Meaning: Tissue");
		System.out.println("    Segmented Property Type Code Sequence:");
		System.out.println("      Code Value: T-62000");
		System.out.println("      Coding Scheme: SRT");
		System.out.println("      Code Meaning: Liver");
		System.out.println("    Recommended Display CIELab Value: [39330, 34471, 26163] (brownish)");
		System.out.println();

		System.out.println("  Segment 2:");
		System.out.println("    Segment Number: 2");
		System.out.println("    Segment Label: Liver Tumor");
		System.out.println("    Segment Description: Hepatic lesion");
		System.out.println("    Segment Algorithm Type: SEMIAUTOMATIC");
		System.out.println("    Segmented Property Category Code Sequence:");
		System.out.println("      Code Value: M-80003");
		System.out.println("      Coding Scheme: SRT");
		System.out.println("      Code Meaning: Neoplasm");
		System.out.println("    Segmented Property Type Code Sequence:");
		System.out.println("      Code Value: M-80006");
		System.out.println("      Coding Scheme: SRT");
		System.out.println("      Code Meaning: Malignant Neoplasm");
		System.out.println("    Recommended Display CIELab Value: [62431, 54116, 45216] (reddish)");
		System.out.println();
	}

	/**
	 * Demonstrate segmentation types
	 */
	private static void demonstrateSegmentationTypes() {

		System.out.println("--- Segmentation Types ---\n");

		System.out.println("BINARY Segmentation:");
		System.out.println("  - Each pixel is either 0 (outside) or 1 (inside)");
		System.out.println("  - Bits Allocated = 1");
		System.out.println("  - Most common type");
		System.out.println("  - Efficient storage");
		System.out.println("  - Example: Organ boundaries, tumor contours");
		System.out.println();

		System.out.println("FRACTIONAL Segmentation:");
		System.out.println("  - Each pixel has a probability/fraction (0.0 to 1.0)");
		System.out.println("  - Bits Allocated = 8 (stored as 0-255)");
		System.out.println("  - Used for:");
		System.out.println("    - Probability maps from AI models");
		System.out.println("    - Partial volume effects");
		System.out.println("    - Uncertainty visualization");
		System.out.println("  - Segmentation Fractional Type:");
		System.out.println("    - PROBABILITY: Values represent confidence");
		System.out.println("    - OCCUPANCY: Values represent partial occupancy");
		System.out.println();
	}

	/**
	 * Demonstrate common use cases
	 */
	private static void demonstrateUseCases() {

		System.out.println("--- Common Use Cases ---\n");

		System.out.println("1. AI/ML Model Outputs");
		System.out.println("   - Deep learning tumor detection");
		System.out.println("   - Organ segmentation (liver, kidney, spleen)");
		System.out.println("   - Lesion characterization");
		System.out.println("   - Store model predictions in DICOM format");
		System.out.println();

		System.out.println("2. Manual Annotations");
		System.out.println("   - Radiologist annotations for teaching");
		System.out.println("   - Ground truth labels for AI training");
		System.out.println("   - Research study annotations");
		System.out.println();

		System.out.println("3. Quantitative Analysis");
		System.out.println("   - Volume measurements");
		System.out.println("   - Longitudinal tracking (tumor growth/shrinkage)");
		System.out.println("   - Treatment response assessment");
		System.out.println();

		System.out.println("4. Treatment Planning");
		System.out.println("   - Auto-contouring for radiation therapy");
		System.out.println("   - Surgical planning");
		System.out.println("   - Pre-operative visualization");
		System.out.println();

		System.out.println("--- Integration with PACS ---\n");

		System.out.println("Segmentation objects can be:");
		System.out.println("- Stored alongside original images in PACS");
		System.out.println("- Retrieved and displayed as overlays");
		System.out.println("- Referenced in Structured Reports");
		System.out.println("- Used in RT Structure Set generation");
		System.out.println("- Queried via C-FIND (Modality = SEG)");
		System.out.println();

		System.out.println("--- Tools and Libraries ---\n");

		System.out.println("Creating Segmentation objects:");
		System.out.println("- dcmqi (https://github.com/QIICR/dcmqi)");
		System.out.println("- pydicom-seg");
		System.out.println("- 3D Slicer");
		System.out.println("- ITK-SNAP");
		System.out.println("- PixelMed (manual construction)");
	}
}
