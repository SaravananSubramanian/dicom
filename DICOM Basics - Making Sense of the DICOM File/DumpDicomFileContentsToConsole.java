package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.AttributeList;

public class DumpDicomFileContentsToConsole {

    public static void main(String[] args) {
    	String dicomFile = "/Users/saravanan/java work/workspace/PixelMedTutorial/Sample Image/IM-0001-0001.dcm";
    	try {
    		AttributeList list = new AttributeList();
    		list.read(dicomFile);
    		System.out.println(list.toString());
            
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
