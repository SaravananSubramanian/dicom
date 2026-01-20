package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.TagFromName;

/**
 * DICOM Multi-modality Examples Demo
 *
 * Different imaging modalities have specific DICOM attributes and IODs.
 * Understanding modality-specific attributes is essential for proper
 * DICOM handling.
 *
 * Common Modalities:
 * - CT (Computed Tomography)
 * - MR (Magnetic Resonance)
 * - US (Ultrasound)
 * - XA (X-Ray Angiography)
 * - CR/DX (Computed/Digital Radiography)
 * - MG (Mammography)
 * - NM (Nuclear Medicine)
 * - PT (PET)
 * - SC (Secondary Capture)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ModalitySpecificAttributesDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM Multi-modality Examples ===\n");

		// CT-specific attributes
		System.out.println("=== CT (Computed Tomography) ===");
		demonstrateCTAttributes();

		// MR-specific attributes
		System.out.println("\n=== MR (Magnetic Resonance) ===");
		demonstrateMRAttributes();

		// US-specific attributes
		System.out.println("\n=== US (Ultrasound) ===");
		demonstrateUSAttributes();

		// XA-specific attributes
		System.out.println("\n=== XA (X-Ray Angiography) ===");
		demonstrateXAAttributes();

		// CR/DX-specific attributes
		System.out.println("\n=== CR/DX (Radiography) ===");
		demonstrateDXAttributes();

		// MG-specific attributes
		System.out.println("\n=== MG (Mammography) ===");
		demonstrateMGAttributes();

		// NM/PT-specific attributes
		System.out.println("\n=== NM/PT (Nuclear Medicine/PET) ===");
		demonstrateNMAttributes();
	}

	/**
	 * CT-specific attributes
	 */
	private static void demonstrateCTAttributes() {

		System.out.println("\nSOP Class: CT Image Storage (1.2.840.10008.5.1.4.1.1.2)");
		System.out.println();

		System.out.println("Key CT Attributes:");
		System.out.println();

		System.out.println("CT Image Module:");
		System.out.println("  (0018,0060) KVP - X-ray tube voltage (e.g., 120 kV)");
		System.out.println("  (0018,1151) X-Ray Tube Current - mA (e.g., 250)");
		System.out.println("  (0018,1150) Exposure Time - ms (e.g., 500)");
		System.out.println("  (0018,1152) Exposure - mAs (e.g., 125)");
		System.out.println("  (0018,9345) CTDIvol - dose index in mGy");
		System.out.println("  (0018,0050) Slice Thickness - mm (e.g., 1.25)");
		System.out.println("  (0018,0088) Spacing Between Slices - mm");
		System.out.println("  (0018,9306) Single Collimation Width - mm");
		System.out.println("  (0018,9307) Total Collimation Width - mm");
		System.out.println();

		System.out.println("CT Reconstruction:");
		System.out.println("  (0018,1100) Reconstruction Diameter - mm (FOV)");
		System.out.println("  (0018,1210) Convolution Kernel - e.g., \"STANDARD\", \"BONE\"");
		System.out.println("  (0018,5100) Patient Position - HFS, HFP, FFS, FFP");
		System.out.println();

		System.out.println("Hounsfield Units:");
		System.out.println("  (0028,1052) Rescale Intercept - typically -1024");
		System.out.println("  (0028,1053) Rescale Slope - typically 1");
		System.out.println("  HU = Rescale Slope * Pixel Value + Rescale Intercept");
		System.out.println("  Water = 0 HU, Air = -1000 HU, Bone = +400 to +1000 HU");
	}

	/**
	 * MR-specific attributes
	 */
	private static void demonstrateMRAttributes() {

		System.out.println("\nSOP Class: MR Image Storage (1.2.840.10008.5.1.4.1.1.4)");
		System.out.println();

		System.out.println("Key MR Attributes:");
		System.out.println();

		System.out.println("MR Image Module:");
		System.out.println("  (0018,0020) Scanning Sequence - SE, IR, GR, EP, RM");
		System.out.println("  (0018,0021) Sequence Variant - SK, MTC, SS, TRSS, SP, MP, etc.");
		System.out.println("  (0018,0022) Scan Options - PER, RG, CG, PPG, FC, PFF, etc.");
		System.out.println("  (0018,0023) MR Acquisition Type - 2D, 3D");
		System.out.println("  (0018,0080) Repetition Time (TR) - ms");
		System.out.println("  (0018,0081) Echo Time (TE) - ms");
		System.out.println("  (0018,0082) Inversion Time (TI) - ms (for IR sequences)");
		System.out.println("  (0018,0083) Number of Averages (NEX/NSA)");
		System.out.println("  (0018,0084) Imaging Frequency - MHz");
		System.out.println("  (0018,0085) Imaged Nucleus - 1H, 13C, 31P, etc.");
		System.out.println("  (0018,0087) Magnetic Field Strength - Tesla");
		System.out.println("  (0018,0091) Echo Train Length");
		System.out.println("  (0018,1314) Flip Angle - degrees");
		System.out.println();

		System.out.println("Contrast:");
		System.out.println("  (0018,0010) Contrast/Bolus Agent - e.g., \"Gadavist\"");
		System.out.println("  (0018,1040) Contrast/Bolus Route - IV");
		System.out.println("  (0018,1041) Contrast/Bolus Volume - ml");
		System.out.println();

		System.out.println("Common Sequence Types:");
		System.out.println("  T1-weighted: Short TR (~500ms), Short TE (~10-20ms)");
		System.out.println("  T2-weighted: Long TR (~2000-4000ms), Long TE (~80-120ms)");
		System.out.println("  FLAIR: Long TR, Long TE, TI ~2500ms");
		System.out.println("  DWI: Echo-planar, b-value in (0018,9087)");
	}

	/**
	 * US-specific attributes
	 */
	private static void demonstrateUSAttributes() {

		System.out.println("\nSOP Class: US Image Storage (1.2.840.10008.5.1.4.1.1.6.1)");
		System.out.println("         US Multi-frame (1.2.840.10008.5.1.4.1.1.3.1)");
		System.out.println();

		System.out.println("Key US Attributes:");
		System.out.println();

		System.out.println("US Image Module:");
		System.out.println("  (0018,6011) Sequence of Ultrasound Regions");
		System.out.println("  (0018,602C) Physical Delta X - mm per pixel");
		System.out.println("  (0018,602E) Physical Delta Y - mm per pixel");
		System.out.println("  (0008,2142) Start Trim - frame number");
		System.out.println("  (0008,2143) Stop Trim - frame number");
		System.out.println("  (0008,2144) Recommended Display Frame Rate");
		System.out.println("  (0018,6030) Transducer Type - SECTOR, LINEAR, CURVED");
		System.out.println();

		System.out.println("US Region Calibration:");
		System.out.println("  (0018,6018) Region Spatial Format - 2D, M-MODE, etc.");
		System.out.println("  (0018,601A) Region Data Type - COLOR_FLOW, TISSUE, etc.");
		System.out.println("  (0018,6024) Physical Units X Direction - cm, mm");
		System.out.println("  (0018,6026) Physical Units Y Direction - cm/s, Hz");
		System.out.println();

		System.out.println("Multi-frame:");
		System.out.println("  (0028,0008) Number of Frames - typically high (cine loops)");
		System.out.println("  (0018,1063) Frame Time - ms between frames");
		System.out.println("  (0008,1250) Related Series Sequence");
	}

	/**
	 * XA-specific attributes
	 */
	private static void demonstrateXAAttributes() {

		System.out.println("\nSOP Class: X-Ray Angiographic Image (1.2.840.10008.5.1.4.1.1.12.1)");
		System.out.println();

		System.out.println("Key XA Attributes:");
		System.out.println();

		System.out.println("XA Image Module:");
		System.out.println("  (0018,1147) Field of View Shape - RECTANGLE, ROUND");
		System.out.println("  (0018,1149) Field of View Dimension(s) - mm");
		System.out.println("  (0018,1500) Positioner Motion - STATIC, DYNAMIC");
		System.out.println("  (0018,1510) Positioner Primary Angle - LAO/RAO");
		System.out.println("  (0018,1511) Positioner Secondary Angle - CRAN/CAUD");
		System.out.println("  (0018,1114) Magnification Factor");
		System.out.println("  (0018,1164) Imager Pixel Spacing - mm/pixel");
		System.out.println();

		System.out.println("X-Ray Acquisition:");
		System.out.println("  (0018,0060) KVP");
		System.out.println("  (0018,1150) Exposure Time");
		System.out.println("  (0018,1153) Exposure in µAs");
		System.out.println("  (0018,1160) Filter Type");
		System.out.println("  (0018,7050) Filter Material - Cu, Al, Mo");
		System.out.println();

		System.out.println("Multi-frame (cine):");
		System.out.println("  (0028,0008) Number of Frames");
		System.out.println("  (0008,2142) Start Trim");
		System.out.println("  (0008,2143) Stop Trim");
		System.out.println("  (0008,2144) Recommended Display Frame Rate - fps");
	}

	/**
	 * DX/CR-specific attributes
	 */
	private static void demonstrateDXAttributes() {

		System.out.println("\nSOP Classes:");
		System.out.println("  Digital X-Ray (DX): 1.2.840.10008.5.1.4.1.1.1.1");
		System.out.println("  Computed Radiography (CR): 1.2.840.10008.5.1.4.1.1.1");
		System.out.println();

		System.out.println("Key DX/CR Attributes:");
		System.out.println();

		System.out.println("DX Anatomy Imaged Module:");
		System.out.println("  (0018,5101) View Position - AP, PA, LL, RL, etc.");
		System.out.println("  (0008,2218) Anatomic Region Sequence");
		System.out.println("  (0020,0060) Laterality - R, L");
		System.out.println();

		System.out.println("DX Positioning Module:");
		System.out.println("  (0018,1110) Distance Source to Detector - mm");
		System.out.println("  (0018,1111) Distance Source to Patient - mm");
		System.out.println("  (0018,1114) Estimated Radiographic Magnification");
		System.out.println("  (0018,1166) Grid - IN, NONE");
		System.out.println();

		System.out.println("Exposure:");
		System.out.println("  (0018,0060) KVP");
		System.out.println("  (0018,1150) Exposure Time - ms");
		System.out.println("  (0018,1151) X-Ray Tube Current - mA");
		System.out.println("  (0018,1152) Exposure - mAs");
		System.out.println("  (0018,1405) Relative X-Ray Exposure");
		System.out.println("  (0018,115E) Image and Fluoroscopy Area Dose Product");
	}

	/**
	 * MG-specific attributes
	 */
	private static void demonstrateMGAttributes() {

		System.out.println("\nSOP Class: Digital Mammography (1.2.840.10008.5.1.4.1.1.1.2)");
		System.out.println();

		System.out.println("Key MG Attributes:");
		System.out.println();

		System.out.println("Mammography Image Module:");
		System.out.println("  (0018,0060) KVP - typically 26-32 kV");
		System.out.println("  (0018,1114) Magnification Factor");
		System.out.println("  (0018,1147) Field of View Shape");
		System.out.println("  (0018,1164) Imager Pixel Spacing");
		System.out.println("  (0018,1166) Grid - IN or NONE");
		System.out.println("  (0018,7004) Detector Type - DIRECT, SCINTILLATOR");
		System.out.println("  (0018,7050) Filter Material - Mo, Rh, Al");
		System.out.println("  (0018,11A0) Body Part Thickness - compressed thickness in mm");
		System.out.println("  (0018,11A2) Compression Force - N (Newtons)");
		System.out.println();

		System.out.println("View Information:");
		System.out.println("  (0020,0060) Laterality - R, L");
		System.out.println("  (0018,5101) View Position - CC, MLO, ML, LM, etc.");
		System.out.println("  (0054,0220) View Code Sequence");
		System.out.println();

		System.out.println("BI-RADS Related:");
		System.out.println("  (0008,1084) Admitting Diagnoses Code Sequence");
		System.out.println("  Breast density and findings typically in SR");
	}

	/**
	 * NM/PT-specific attributes
	 */
	private static void demonstrateNMAttributes() {

		System.out.println("\nSOP Classes:");
		System.out.println("  NM Image: 1.2.840.10008.5.1.4.1.1.20");
		System.out.println("  PET Image: 1.2.840.10008.5.1.4.1.1.128");
		System.out.println();

		System.out.println("Key NM/PT Attributes:");
		System.out.println();

		System.out.println("NM Image Module:");
		System.out.println("  (0054,0016) Radiopharmaceutical Information Sequence");
		System.out.println("  (0018,0031) Radiopharmaceutical - e.g., Tc-99m, FDG");
		System.out.println("  (0018,1071) Radiopharmaceutical Volume - ml");
		System.out.println("  (0018,1074) Radionuclide Total Dose - MBq");
		System.out.println("  (0018,1072) Radiopharmaceutical Start DateTime");
		System.out.println();

		System.out.println("NM Detector Module:");
		System.out.println("  (0054,0022) Detector Information Sequence");
		System.out.println("  (0018,1000) Device Serial Number");
		System.out.println("  (0054,0200) Start Angle - for SPECT");
		System.out.println("  (0054,0202) Type of Detector Motion");
		System.out.println();

		System.out.println("PET-specific:");
		System.out.println("  (0054,1001) Units - BQML, CNTS, etc.");
		System.out.println("  (0054,1102) Decay Correction - START, ADMIN, NONE");
		System.out.println("  (0054,1300) Frame Reference Time");
		System.out.println("  (0054,1310) Primary Prompts Counts Source");
		System.out.println("  (0010,1030) Patient Weight - needed for SUV calculation");
		System.out.println("  SUV = Activity / (Injected Dose / Patient Weight)");
	}
}
