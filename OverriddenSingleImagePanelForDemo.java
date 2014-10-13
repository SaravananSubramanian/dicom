package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import com.pixelmed.display.SingleImagePanel;
import com.pixelmed.display.SourceImage;
import com.pixelmed.display.event.FrameSelectionChangeEvent;
import com.pixelmed.event.ApplicationEventDispatcher;
import com.pixelmed.event.EventContext;

public class OverriddenSingleImagePanelForDemo extends SingleImagePanel {
	
	//initialize these to default values
	private static final long serialVersionUID = 1L;
	private int frameIndex = 1;
	private int MaxFrames = 1;

	public OverriddenSingleImagePanelForDemo(SourceImage sImg) {
		super(sImg);
		MaxFrames = sImg.getNumberOfFrames();
		this.setSideAndViewAnnotationString(getTextToDisplay(),0, "SansSerif",Font.BOLD, 14, Color.WHITE,true);
	}

	private String getTextToDisplay() {
		return " Window Width->" + (int) this.windowWidth 
				+ " Level(or Center)->" + (int) this.windowCenter 
				+ " Frame Index->" + frameIndex;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		frameIndex++;
		if(frameIndex >= MaxFrames){
			frameIndex = 1;
		}
		ApplicationEventDispatcher.getApplicationEventDispatcher()
		.processEvent(new FrameSelectionChangeEvent(new EventContext("Pass event here"), frameIndex));
		this.sideAndViewAnnotationString = getTextToDisplay();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		this.sideAndViewAnnotationString = getTextToDisplay();
	}

}
