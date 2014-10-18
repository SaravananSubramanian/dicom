package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomDirectoryBrowser;

public class CreatingDicomDirectoryFileDemo {
	
public static void main(String[] args) {
		
		String dicomDirectoryFileName = "/Users/Saravanan/PixelMedTutorial/RandomImagesForDicomDirectoryCreationDemo/DICOMDIR";
		String imageFile1 = "/Users/Saravanan/PixelMedTutorial/RandomImagesForDicomDirectoryCreationDemo/MR3_UNC";
		String imageFile2 = "/Users/Saravanan/PixelMedTutorial/RandomImagesForDicomDirectoryCreationDemo/CT1_RLE";
		String imageFile3 = "/Users/Saravanan/PixelMedTutorial/RandomImagesForDicomDirectoryCreationDemo/CT2_RLE";
		String imageFile4 = "/Users/Saravanan/PixelMedTutorial/RandomImagesForDicomDirectoryCreationDemo/VL3_UNC";
		
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
