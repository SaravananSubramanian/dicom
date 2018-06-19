package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomDirectoryBrowser;

public class CreatingDicomDirectoryFileDemo {
	
public static void main(String[] args) {
		
	    String dicomRootDirectory = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images";
		String dicomDirectoryFileName = dicomRootDirectory + File.separatorChar + "SampleDICOMDIR";
		
		//These are the image files that we will include as file-set
		//remember, they may be under different directories such as patient, study & series folders usually
		String imageFile1 = dicomRootDirectory + File.separatorChar + "CR-MONO1-10-chest";
		String imageFile2 = dicomRootDirectory + File.separatorChar + "CT-MONO2-12-lomb-an2";
		String imageFile3 = dicomRootDirectory + File.separatorChar + "CT-MONO2-16-ankle";
		String imageFile4 = dicomRootDirectory + File.separatorChar + "CT-MONO2-16-brain";
		
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
