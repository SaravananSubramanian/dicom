package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomInputStream;

public class ReadDicomDirectoryFileDemo {
	
	public static void main(String[] args) {
		
		String dicomDirectoryFileName = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images\\dicomdir_example\\DICOMDIR";
		try {
		    AttributeList list = new AttributeList();
    		list.read(new DicomInputStream(new File(dicomDirectoryFileName)));
    		DicomDirectory dicomDirectory = new DicomDirectory(list);
    		System.out.println(dicomDirectory.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
