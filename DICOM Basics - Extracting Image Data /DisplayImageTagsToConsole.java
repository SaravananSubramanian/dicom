package com.saravanansubramanian.dicom.pixelmedtutorial;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;

public class DisplayImageTagsToConsole {
	
	private static AttributeList list = new AttributeList();
	public static void main(String[] args) {
		String dicomFile = "/Users/Saravanan/PixelMedTutorial/CT1_J2KR.dcm";
		
    	try {
    		list.read(dicomFile);
    		System.out.println("Transfer Syntax:" + getTagInformation(TagFromName.TransferSyntaxUID));
    		System.out.println("SOP Class:" + getTagInformation(TagFromName.SOPClassUID));
    		System.out.println("Modality:" + getTagInformation(TagFromName.Modality));
    		System.out.println("Samples Per Pixel:" + getTagInformation(TagFromName.SamplesPerPixel));
    		System.out.println("Photometric Interpretation:" + getTagInformation(TagFromName.PhotometricInterpretation));
    		System.out.println("Pixel Spacing:" + getTagInformation(TagFromName.PixelSpacing));
    		System.out.println("Bits Allocated:" + getTagInformation(TagFromName.BitsAllocated));
    		System.out.println("Bits Stored:" + getTagInformation(TagFromName.BitsStored));
    		System.out.println("High Bit:" + getTagInformation(TagFromName.HighBit));
    		SourceImage img = new com.pixelmed.display.SourceImage(list);
    		System.out.println("Number of frames " + img.getNumberOfFrames());
    		System.out.println("Width " + img.getWidth());//all frames will have same width
    		System.out.println("Height " + img.getHeight());//all frames will have same height  
    		System.out.println("Is Grayscale? " + img.isGrayscale());
    		System.out.println("Pixel Data present:" + (list.get(TagFromName.PixelData) != null));
    		OtherWordAttribute pixelAttribute = (OtherWordAttribute)(list.get(TagFromName.PixelData));
    		//get the 16 bit pixel data values
    		short[] data = pixelAttribute.getShortValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getTagInformation(AttributeTag attrTag) {
		return Attribute.getDelimitedStringValuesOrEmptyString(list, attrTag);
	}
}
