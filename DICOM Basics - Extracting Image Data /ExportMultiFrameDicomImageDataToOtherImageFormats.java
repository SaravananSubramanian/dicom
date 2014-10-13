package com.saravanansubramanian.dicom.pixelmedtutorial;
import com.pixelmed.display.ConsumerFormatImageMaker;

public class ExportMultiFrameDicomImageDataToOtherImageFormats {
	
	public static void main(String[] args) {
		
		String dicomFile = "/Users/Saravanan/PixelMedTutorial/MR-MONO2-8-16x-heart.dcm";
		String outputJpgFile = "/Users/Saravanan/PixelMedTutorial/MR-MONO2-8-16x-heart.jpg";
		
		try {
			//This method invocation will result in 16 jpeg files created as the input DICOM file has 16 frames
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputJpgFile, "jpeg", 0);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
