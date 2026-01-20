package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;

/**
 * DICOM Image Tag Display Demo
 *
 * PREREQUISITES: Requires a DICOM file. Update 'dicomFile' variable with your file path.
 * Sample files: https://barre.dev/medical/samples/ or https://www.dicomlibrary.com/
 *
 * This example demonstrates how to read and display key DICOM attributes
 * (tags) from a DICOM image file, including image-specific parameters.
 *
 * Key concepts:
 * - DICOM tags are identified by (group, element) pairs
 * - TagFromName provides convenient access to standard tag definitions
 * - SourceImage class provides image-specific information (dimensions, frames)
 * - Pixel data can be extracted as raw values for processing
 *
 * Important image attributes:
 * - Transfer Syntax: How the data is encoded
 * - SOP Class: What type of DICOM object this is
 * - Modality: Imaging modality (CT, MR, US, etc.)
 * - Photometric Interpretation: How to interpret pixel values
 * - Bits Allocated/Stored: Pixel depth information
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DisplayImageTagsToConsole {

	private static AttributeList attributeList = new AttributeList();

	public static void main(String[] args) {

		// === Configuration ===
		String dicomFile = "C:\\temp\\sample_images\\MR-MONO2-16-head";

		try {

			System.out.println("=== DICOM Image Tag Display Demo ===\n");
			System.out.println("Reading file: " + dicomFile);

			// === Read the DICOM file ===
			attributeList.read(dicomFile);

			// === Display Transfer Syntax and SOP Information ===
			System.out.println("\n--- File Information ---");
			System.out.println("Transfer Syntax: " + getTagValue(TagFromName.TransferSyntaxUID));
			System.out.println("SOP Class: " + getTagValue(TagFromName.SOPClassUID));
			System.out.println("Modality: " + getTagValue(TagFromName.Modality));

			// === Display Image Pixel Parameters ===
			System.out.println("\n--- Pixel Parameters ---");
			System.out.println("Samples Per Pixel: " + getTagValue(TagFromName.SamplesPerPixel));
			System.out.println("Photometric Interpretation: " + getTagValue(TagFromName.PhotometricInterpretation));
			System.out.println("Pixel Spacing: " + getTagValue(TagFromName.PixelSpacing));
			System.out.println("Bits Allocated: " + getTagValue(TagFromName.BitsAllocated));
			System.out.println("Bits Stored: " + getTagValue(TagFromName.BitsStored));
			System.out.println("High Bit: " + getTagValue(TagFromName.HighBit));

			// === Display Image Dimensions Using SourceImage ===
			System.out.println("\n--- Image Dimensions ---");
			SourceImage sourceImage = new SourceImage(attributeList);
			System.out.println("Number of Frames: " + sourceImage.getNumberOfFrames());
			System.out.println("Width: " + sourceImage.getWidth() + " pixels");
			System.out.println("Height: " + sourceImage.getHeight() + " pixels");
			System.out.println("Is Grayscale: " + sourceImage.isGrayscale());

			// === Access Raw Pixel Data ===
			System.out.println("\n--- Pixel Data ---");
			boolean hasPixelData = (attributeList.get(TagFromName.PixelData) != null);
			System.out.println("Pixel Data Present: " + hasPixelData);

			if (hasPixelData) {
				// For 16-bit images, pixel data is stored as shorts
				OtherWordAttribute pixelAttribute = (OtherWordAttribute) attributeList.get(TagFromName.PixelData);
				short[] pixelData = pixelAttribute.getShortValues();
				System.out.println("Pixel Data Length: " + pixelData.length + " values");

				// Display first few pixel values as example
				System.out.print("First 10 pixel values: ");
				for (int i = 0; i < Math.min(10, pixelData.length); i++) {
					System.out.print(pixelData[i] + " ");
				}
				System.out.println();
			}

		} catch (Exception e) {
			System.err.println("Error reading DICOM file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to retrieve a tag value as a string
	 */
	private static String getTagValue(AttributeTag tag) {
		return Attribute.getDelimitedStringValuesOrEmptyString(attributeList, tag);
	}
}
