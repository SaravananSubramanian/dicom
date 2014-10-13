package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.awt.Color;
import javax.swing.JFrame;
import com.pixelmed.display.SourceImage;

public class ViewScrollAndWindowWidthAndLevelingDemo {
	
	public static void main(String[] args) {
		String dicomInputFile = "/Users/saravanan/PixelMedTutorial/exported_jpeg_IM-0001-0115.dcm";
		try {
    		JFrame p = new JFrame();
    		SourceImage sImg = new SourceImage(dicomInputFile);
    		System.out.println("Number of frames: " + sImg.getNumberOfFrames());
    		OverriddenSingleImagePanelForDemo singleImagePanel = new OverriddenSingleImagePanelForDemo(sImg);
    		p.add(singleImagePanel);
    		p.setBackground(Color.BLACK);
    		p.setSize(512,512);
    		p.setTitle("Demo for view, scroll and window width/level operations");
    		p.setVisible(true);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
