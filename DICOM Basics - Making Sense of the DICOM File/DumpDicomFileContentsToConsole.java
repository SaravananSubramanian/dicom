package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.AttributeList;

public class DumpDicomFileContentsToConsole {

	public static void main(String[] args) {
		String dicomFile = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images\\MR-MONO2-16-head";
		try {
			AttributeList list = new AttributeList();
			list.read(dicomFile);
			System.out.println(list.toString());
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
}
