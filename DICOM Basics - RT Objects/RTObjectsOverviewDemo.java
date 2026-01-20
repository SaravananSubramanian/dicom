package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.TagFromName;

/**
 * DICOM Radiation Therapy (RT) Objects Overview Demo
 *
 * DICOM RT objects are used in radiation oncology to store treatment
 * planning and delivery information. They are complex and typically
 * created by treatment planning systems (TPS).
 *
 * RT IODs (Information Object Definitions):
 *
 * 1. RT Structure Set (1.2.840.10008.5.1.4.1.1.481.3)
 *    - Contours defining anatomical structures and targets
 *    - ROIs (Regions of Interest) drawn on planning CT/MR
 *    - Links to referenced image series
 *
 * 2. RT Plan (1.2.840.10008.5.1.4.1.1.481.5)
 *    - Treatment beam parameters
 *    - Dose prescription
 *    - Fraction schemes
 *    - References RT Structure Set
 *
 * 3. RT Dose (1.2.840.10008.5.1.4.1.1.481.2)
 *    - 3D dose distribution grid
 *    - Dose-volume histograms (DVH)
 *    - References RT Plan
 *
 * 4. RT Image (1.2.840.10008.5.1.4.1.1.481.1)
 *    - Portal images from treatment machine
 *    - DRRs (Digitally Reconstructed Radiographs)
 *
 * 5. RT Beams Treatment Record (1.2.840.10008.5.1.4.1.1.481.4)
 *    - Actual delivered treatment parameters
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class RTObjectsOverviewDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM Radiation Therapy Objects Overview ===\n");

		// Display RT SOP Classes
		System.out.println("--- RT SOP Classes ---\n");

		System.out.println("RT Structure Set: " + SOPClass.RTStructureSetStorage);
		System.out.println("  Contains: Anatomical contours (organs, tumors)");
		System.out.println("  Key Tags:");
		System.out.println("    (3006,0020) Structure Set ROI Sequence");
		System.out.println("    (3006,0039) ROI Contour Sequence");
		System.out.println("    (3006,0080) RT ROI Observations Sequence");
		System.out.println();

		System.out.println("RT Plan: " + SOPClass.RTPlanStorage);
		System.out.println("  Contains: Treatment beam parameters, prescriptions");
		System.out.println("  Key Tags:");
		System.out.println("    (300A,00B0) Beam Sequence");
		System.out.println("    (300A,0070) Fraction Group Sequence");
		System.out.println("    (300C,0060) Referenced Structure Set Sequence");
		System.out.println();

		System.out.println("RT Dose: " + SOPClass.RTDoseStorage);
		System.out.println("  Contains: 3D dose distribution, DVH");
		System.out.println("  Key Tags:");
		System.out.println("    (3004,0002) Dose Units");
		System.out.println("    (3004,0004) Dose Type");
		System.out.println("    (3004,000A) Dose Summation Type");
		System.out.println("    (3004,0050) DVH Sequence");
		System.out.println();

		System.out.println("RT Image: " + SOPClass.RTImageStorage);
		System.out.println("  Contains: Portal images, DRRs");
		System.out.println("  Key Tags:");
		System.out.println("    (3002,0002) RT Image Label");
		System.out.println("    (3002,000C) RT Image Plane");
		System.out.println("    (3002,0012) RT Image SID");
		System.out.println();

		// Demonstrate RT Structure Set structure
		System.out.println("\n--- RT Structure Set Example ---\n");
		demonstrateRTStructureSet();

		// Demonstrate RT Plan structure
		System.out.println("\n--- RT Plan Example ---\n");
		demonstrateRTPlan();

		// Demonstrate RT Dose structure
		System.out.println("\n--- RT Dose Example ---\n");
		demonstrateRTDose();

		// RT workflow
		System.out.println("\n--- RT Workflow ---\n");
		demonstrateRTWorkflow();
	}

	/**
	 * Demonstrate RT Structure Set structure
	 */
	private static void demonstrateRTStructureSet() {

		System.out.println("RT Structure Set contains ROIs defined on images:");
		System.out.println();
		System.out.println("Structure Set ROI Sequence (3006,0020):");
		System.out.println("  Item 1:");
		System.out.println("    ROI Number: 1");
		System.out.println("    ROI Name: PTV (Planning Target Volume)");
		System.out.println("    ROI Generation Algorithm: MANUAL");
		System.out.println("  Item 2:");
		System.out.println("    ROI Number: 2");
		System.out.println("    ROI Name: Spinal Cord");
		System.out.println("    ROI Generation Algorithm: MANUAL");
		System.out.println("  Item 3:");
		System.out.println("    ROI Number: 3");
		System.out.println("    ROI Name: Left Lung");
		System.out.println("    ROI Generation Algorithm: AUTOMATIC");
		System.out.println();
		System.out.println("ROI Contour Sequence (3006,0039):");
		System.out.println("  Item 1 (ROI 1 - PTV):");
		System.out.println("    Contour Sequence:");
		System.out.println("      - Slice 1: 45 points, geometric type CLOSED_PLANAR");
		System.out.println("      - Slice 2: 52 points, geometric type CLOSED_PLANAR");
		System.out.println("      - ... (contour for each CT slice)");
		System.out.println("    ROI Display Color: 255\\0\\0 (Red)");
	}

	/**
	 * Demonstrate RT Plan structure
	 */
	private static void demonstrateRTPlan() {

		System.out.println("RT Plan contains treatment beam parameters:");
		System.out.println();
		System.out.println("Plan Information:");
		System.out.println("  RT Plan Label: LUNG_SBRT_5FX");
		System.out.println("  RT Plan Date: 20240115");
		System.out.println("  Plan Intent: CURATIVE");
		System.out.println();
		System.out.println("Fraction Group Sequence (300A,0070):");
		System.out.println("  Fraction Group Number: 1");
		System.out.println("  Number of Fractions Planned: 5");
		System.out.println("  Number of Beams: 7");
		System.out.println();
		System.out.println("Beam Sequence (300A,00B0):");
		System.out.println("  Beam 1:");
		System.out.println("    Beam Number: 1");
		System.out.println("    Beam Name: AP");
		System.out.println("    Beam Type: STATIC");
		System.out.println("    Radiation Type: PHOTON");
		System.out.println("    Nominal Beam Energy: 6 MV");
		System.out.println("    Gantry Angle: 0.0");
		System.out.println("    Collimator Angle: 0.0");
		System.out.println("    Couch Angle: 0.0");
		System.out.println("    MLC Sequence: (leaf positions)");
		System.out.println("  Beam 2:");
		System.out.println("    Beam Number: 2");
		System.out.println("    Beam Name: LAO_45");
		System.out.println("    Gantry Angle: 45.0");
		System.out.println("    ...");
	}

	/**
	 * Demonstrate RT Dose structure
	 */
	private static void demonstrateRTDose() {

		System.out.println("RT Dose contains dose distribution:");
		System.out.println();
		System.out.println("Dose Information:");
		System.out.println("  Dose Units: GY");
		System.out.println("  Dose Type: PHYSICAL");
		System.out.println("  Dose Summation Type: PLAN");
		System.out.println("  Dose Grid Scaling: 0.0001");
		System.out.println();
		System.out.println("Dose Grid:");
		System.out.println("  Rows: 256");
		System.out.println("  Columns: 256");
		System.out.println("  Number of Frames: 80");
		System.out.println("  Pixel Spacing: 2.0\\2.0 mm");
		System.out.println("  Grid Frame Offset Vector: (slice positions)");
		System.out.println();
		System.out.println("DVH Sequence (3004,0050):");
		System.out.println("  DVH 1 (PTV):");
		System.out.println("    DVH Type: CUMULATIVE");
		System.out.println("    Dose Units: GY");
		System.out.println("    DVH Dose Scaling: 0.01");
		System.out.println("    DVH Volume Units: CM3");
		System.out.println("    DVH Data: (dose-volume pairs)");
		System.out.println("  DVH 2 (Spinal Cord):");
		System.out.println("    DVH Max Dose: 8.5 Gy");
		System.out.println("    DVH Mean Dose: 2.3 Gy");
	}

	/**
	 * Demonstrate RT workflow
	 */
	private static void demonstrateRTWorkflow() {

		System.out.println("Typical RT Workflow:");
		System.out.println();
		System.out.println("1. Imaging (CT Simulation)");
		System.out.println("   - Patient positioned and CT scan acquired");
		System.out.println("   - CT images sent to TPS");
		System.out.println("   - Output: CT Image Series");
		System.out.println();
		System.out.println("2. Contouring");
		System.out.println("   - Physician/dosimetrist draws ROIs");
		System.out.println("   - Targets: GTV, CTV, PTV");
		System.out.println("   - OARs: Spinal cord, lungs, heart, etc.");
		System.out.println("   - Output: RT Structure Set");
		System.out.println();
		System.out.println("3. Treatment Planning");
		System.out.println("   - Dosimetrist designs beam arrangement");
		System.out.println("   - Optimizer calculates MLC positions");
		System.out.println("   - Dose calculated on CT grid");
		System.out.println("   - Output: RT Plan + RT Dose");
		System.out.println();
		System.out.println("4. Plan Approval");
		System.out.println("   - Physician reviews plan and DVH");
		System.out.println("   - Plan approved for treatment");
		System.out.println();
		System.out.println("5. Treatment Delivery");
		System.out.println("   - Plan sent to treatment machine");
		System.out.println("   - Treatment delivered");
		System.out.println("   - Output: RT Beams Treatment Record");
		System.out.println();
		System.out.println("6. Verification Imaging");
		System.out.println("   - Portal images or CBCT acquired");
		System.out.println("   - Output: RT Image");
	}
}
