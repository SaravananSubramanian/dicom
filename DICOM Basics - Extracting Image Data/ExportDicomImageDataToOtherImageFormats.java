package com.saravanansubramanian.dicom.pixelmedtutorial;
import java.io.File;

import com.pixelmed.display.ConsumerFormatImageMaker;

public class ExportDicomImageDataToOtherImageFormats {
	
	public static void main(String[] args) {
		
		String dicomRootDirectory = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images";
		
		String dicomFile = dicomRootDirectory + File.separatorChar + "CT-MONO2-16-ankle";
		
		String outputJpgFile = dicomRootDirectory + File.separatorChar + "MR-MONO2-16-head.jpg";
        String outputPngFile = dicomRootDirectory + File.separatorChar + "MR-MONO2-16-head.png";
        String outputTiffFile = dicomRootDirectory + File.separatorChar + "MR-MONO2-16-head.tiff";
		
		try {
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputJpgFile, "jpeg", 0);
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputPngFile, "png", 0);
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputTiffFile, "tiff", 0);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
