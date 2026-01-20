package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomDirectoryBrowser;

/**
 * Creating DICOM Directory (DICOMDIR) File Demo
 *
 * PREREQUISITES: Requires DICOM files to index. Update 'dicomRootDirectory' and
 * file paths (imageFile1-4) with your actual DICOM file locations.
 *
 * This example demonstrates how to create a DICOMDIR file that indexes
 * a collection of DICOM files (a "file-set") for easy navigation.
 *
 * Key concepts:
 * - DICOMDIR: A special DICOM file that acts as an index/directory
 * - File-set: A collection of related DICOM files (e.g., on a CD)
 * - Directory Records: Hierarchical entries (Patient -> Study -> Series -> Image)
 * - DICOMDIR enables fast navigation without reading all files
 *
 * Directory Record Hierarchy:
 * - PATIENT: Patient demographics
 *   - STUDY: Study-level information
 *     - SERIES: Series-level information
 *       - IMAGE: Individual instance references
 *
 * Common uses for DICOMDIR:
 * - Media exchange (CDs, DVDs, USB drives)
 * - Organizing DICOM archives
 * - Portable collections for patient handoff
 * - File-based PACS alternatives
 *
 * The DICOMDIR file is typically named "DICOMDIR" (no extension)
 * and placed at the root of the file-set.
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreatingDicomDirectoryFileDemo {

	public static void main(String[] args) {

		System.out.println("=== Creating DICOM Directory (DICOMDIR) Demo ===\n");

		try {

			// === Configuration ===
			String dicomRootDirectory = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images";
			String dicomDirectoryFileName = dicomRootDirectory + File.separatorChar + "SampleDICOMDIR";

			// === DICOM Files to Include in File-Set ===
			// These files will be indexed in the DICOMDIR
			// In practice, these would be organized in patient/study/series folders
			String imageFile1 = dicomRootDirectory + File.separatorChar + "CR-MONO1-10-chest";
			String imageFile2 = dicomRootDirectory + File.separatorChar + "CT-MONO2-12-lomb-an2";
			String imageFile3 = dicomRootDirectory + File.separatorChar + "CT-MONO2-16-ankle";
			String imageFile4 = dicomRootDirectory + File.separatorChar + "CT-MONO2-16-brain";

			System.out.println("Root Directory: " + dicomRootDirectory);
			System.out.println("Output DICOMDIR: " + dicomDirectoryFileName);
			System.out.println();
			System.out.println("Files to include in file-set:");
			System.out.println("  1. " + imageFile1);
			System.out.println("  2. " + imageFile2);
			System.out.println("  3. " + imageFile3);
			System.out.println("  4. " + imageFile4);
			System.out.println();

			// === Create the DICOMDIR ===
			System.out.println("Creating DICOMDIR...\n");

			String[] sourceFiles = new String[] {
				imageFile1,
				imageFile2,
				imageFile3,
				imageFile4
			};

			// DicomDirectory reads each file and builds the directory structure
			DicomDirectory dicomDirectory = new DicomDirectory(sourceFiles);

			// === Write DICOMDIR to Disk ===
			dicomDirectory.write(dicomDirectoryFileName);

			System.out.println("=== DICOMDIR Created Successfully ===\n");

			// === Display Directory Structure ===
			System.out.println("Directory Structure:");
			System.out.println(dicomDirectory);

			// === Launch Visual Browser ===
			System.out.println("\nLaunching DICOMDIR Browser...");
			DicomDirectoryBrowser.main(new String[] { dicomDirectoryFileName });

		} catch (Exception e) {
			System.err.println("\nFailed to create DICOMDIR!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nCommon causes:");
			System.err.println("  - Source files not found");
			System.err.println("  - Invalid DICOM files");
			System.err.println("  - Write permission denied");
			e.printStackTrace();
		}
	}
}
