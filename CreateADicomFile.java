package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.ImageToDicom;
import com.pixelmed.dicom.SOPClass;

public class CreateADicomFile {
	
	public static void main(String[] args) {
		String scJpegFilePath = "/Users/saravanan/PixelMedTutorial/ChestGeneralImage.jpg";
		String newDicomFile = "/Users/saravanan/PixelMedTutorial/Saravanan.dcm";
        try {
        	//generate the DICOM file from the jpeg file and the other attributes supplied
			new ImageToDicom(scJpegFilePath, //path to secondary capture image 
					         newDicomFile, //output DICOM file with full path
					         "Saravanan Subramanian", //name of patient
					         "12121221", //patient id
					         "2323232322", //study id
					         "3232323232", //series number
					         "42423232234", //instance number
					          "CT", //modality
					          SOPClass.CTImageStorage); //SOP class
		    //now, dump the contents of the DICOM file to the console
			AttributeList list = new AttributeList();
    		list.read(newDicomFile);
    		System.out.println(list.toString());
        
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
