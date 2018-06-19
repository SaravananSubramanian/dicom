package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.awt.Color;
import javax.swing.JFrame;
import com.pixelmed.display.SourceImage;

public class ViewScrollAndWindowWidthAndLevelingDemo {
	
	public static void main(String[] args) {
		String dicomInputFile = "D:\\MyWebsiteWork\\javatutorialsproject\\sample_images\\XA-MONO2-8-12x-catheter";
		try {
    		JFrame frame = new JFrame();
    		SourceImage sImg = new SourceImage(dicomInputFile);
    		System.out.println("Number of frames: " + sImg.getNumberOfFrames());
    		OverriddenSingleImagePanelForDemo singleImagePanel = new OverriddenSingleImagePanelForDemo(sImg);
    		frame.add(singleImagePanel);
    		frame.setBackground(Color.BLACK);
    		frame.setSize(sImg.getWidth(),sImg.getHeight());
    		frame.setTitle("Demo for view, scroll and window width/level operations");
    		frame.setVisible(true);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
