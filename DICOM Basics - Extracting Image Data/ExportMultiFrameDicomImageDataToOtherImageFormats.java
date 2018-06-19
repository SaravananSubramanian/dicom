package com.saravanansubramanian.dicom.pixelmedtutorial;
import com.pixelmed.display.ConsumerFormatImageMaker;

public class ExportMultiFrameDicomImageDataToOtherImageFormats {
	
	public static void main(String[] args) {
		
		String dicomFile = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images\\XA-MONO2-8-12x-catheter";
		String outputJpgFile = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images\\outputs\\XA-MONO2-8-12x-catheter.jpg";
		
		try {
			//This method invocation will result in 16 jpeg files created as the input DICOM file has 16 frames
			ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputJpgFile, "jpeg", 0);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
