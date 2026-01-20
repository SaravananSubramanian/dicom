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

/**
 * Custom Single Image Panel for DICOM Viewing Demo
 *
 * This class extends PixelMed's SingleImagePanel to add:
 * - Real-time display of window width and level values
 * - Frame index display for multi-frame navigation
 * - Custom event handling for keyboard frame navigation
 *
 * The panel displays a text overlay showing:
 * - Current window width (contrast)
 * - Current window center/level (brightness)
 * - Current frame index (for multi-frame images)
 *
 * Key methods overridden:
 * - keyPressed(): Handle frame navigation via keyboard
 * - mouseDragged(): Update display when window/level changes
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class OverriddenSingleImagePanelForDemo extends SingleImagePanel {

	private static final long serialVersionUID = 1L;

	// Frame navigation state
	private int currentFrameIndex = 0;
	private int totalFrames = 1;

	/**
	 * Constructor - Initialize the panel with a source image
	 */
	public OverriddenSingleImagePanelForDemo(SourceImage sourceImage) {
		super(sourceImage);

		this.totalFrames = sourceImage.getNumberOfFrames();

		// Set initial text overlay showing window/level info
		updateDisplayOverlay(1);
	}

	/**
	 * Build the display text showing current window/level and frame info
	 */
	private String buildDisplayText(int frameNumber) {
		return " Window Width: " + (int) this.windowWidth
			 + " | Level (Center): " + (int) this.windowCenter
			 + " | Frame: " + frameNumber + "/" + totalFrames;
	}

	/**
	 * Update the text overlay on the image
	 */
	private void updateDisplayOverlay(int frameNumber) {
		this.setSideAndViewAnnotationString(
			buildDisplayText(frameNumber),
			30,                    // Vertical position
			"SansSerif",           // Font family
			Font.BOLD,             // Font style
			14,                    // Font size
			Color.WHITE,           // Text color
			true                   // Show on all frames
		);
	}

	/**
	 * Handle keyboard events for frame navigation
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);

		// Increment frame index, wrap around at the end
		currentFrameIndex++;
		if (currentFrameIndex >= totalFrames) {
			currentFrameIndex = 0;
		}

		// Dispatch frame change event to update display
		ApplicationEventDispatcher.getApplicationEventDispatcher()
			.processEvent(new FrameSelectionChangeEvent(
				new EventContext("FrameNavigation"),
				currentFrameIndex
			));

		// Update the overlay text
		updateDisplayOverlay(currentFrameIndex + 1); // Display 1-based index
	}

	/**
	 * Handle mouse drag events (window/level adjustment)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);

		// Update overlay to show new window/level values
		this.sideAndViewAnnotationString = buildDisplayText(currentFrameIndex + 1);
	}
}
