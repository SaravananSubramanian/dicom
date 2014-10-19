package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomDirectoryBrowser;

public class CreatingDicomDirectoryFileDemo {
	
public static void main(String[] args) {
		
	    String dicomRootDirectory = "/Users/Saravanan/PixelMedTutorial/RandomImagesForDicomDirectoryCreationDemo";
		String dicomDirectoryFileName = dicomRootDirectory + File.separatorChar + "DICOMDIR";
		//These are the image files that we will include as file-set
		//remember, they may be under different directories such as patient, study & series folders usually
		String imageFile1 = dicomRootDirectory + File.separatorChar + "MR3_UNC";
		String imageFile2 = dicomRootDirectory + File.separatorChar + "CT1_RLE";
		String imageFile3 = dicomRootDirectory + File.separatorChar + "CT2_RLE";
		String imageFile4 = dicomRootDirectory + File.separatorChar + "VL3_UNC";
		
		try {
			String[] sourceFiles = new String[] {imageFile1, imageFile2, imageFile3, imageFile4};
			DicomDirectory dicomDirectory = new DicomDirectory(sourceFiles);
			dicomDirectory.write(dicomDirectoryFileName);
			System.out.println(dicomDirectory);
			DicomDirectoryBrowser.main(new String[] {dicomDirectoryFileName});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
