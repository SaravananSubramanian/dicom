package com.saravanansubramanian.dicom.pixelmedtutorial;
import com.pixelmed.display.ConsumerFormatImageMaker;

public class ExportDicomImageDataToOtherImageFormats {
	
	public static void main(String[] args) {
		
		String dicomFile = "/Users/Saravanan/PixelMedTutorial/CT1_J2KR.dcm";
		String outputJpgFile = "/Users/Saravanan/PixelMedTutorial/CT1_J2KR.jpg";
		String outputPngFile = "/Users/Saravanan/PixelMedTutorial/CT1_J2KR.png";
		String outputTiffFile = "/Users/Saravanan/PixelMedTutorial/CT1_J2KR.tiff";
		
		try {
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputJpgFile, "jpeg", 0);
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputPngFile, "png", 0);
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputTiffFile, "tiff", 0);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
