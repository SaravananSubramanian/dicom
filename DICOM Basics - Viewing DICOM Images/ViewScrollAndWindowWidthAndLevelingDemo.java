package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.awt.Color;

import javax.swing.JFrame;

import com.pixelmed.display.SourceImage;

/**
 * DICOM Image Viewing with Window/Level Controls Demo
 *
 * PREREQUISITES: Requires a DICOM image file. Update 'dicomFile' variable.
 * Multi-frame files (XA, US cine) work best for frame scrolling demo.
 * Sample files: https://barre.dev/medical/samples/
 *
 * This example demonstrates how to display DICOM images with interactive
 * window width/level controls and frame navigation for multi-frame images.
 *
 * Key concepts:
 * - Window Width: Controls the contrast (range of displayed values)
 * - Window Center (Level): Controls the brightness (center of the range)
 * - Frame navigation: For multi-frame images (cine loops)
 *
 * Controls in this demo:
 * - Mouse drag: Adjust window width and level
 *   - Horizontal movement changes window width (contrast)
 *   - Vertical movement changes window center (brightness)
 * - Keyboard: Navigate frames in multi-frame images
 *
 * Understanding Window/Level:
 * - Window Width determines how many gray levels are displayed
 * - Window Center determines which value is the middle gray
 * - Narrower windows = higher contrast
 * - Wider windows = lower contrast (more gray levels visible)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class ViewScrollAndWindowWidthAndLevelingDemo {

	public static void main(String[] args) {

		// === Configuration ===
		// Using multi-frame X-ray angiography file for frame navigation demo
		String dicomFile = "C:\\temp\\sample_images\\XA-MONO2-8-12x-catheter";

		try {

			System.out.println("=== DICOM Image Viewer Demo ===\n");
			System.out.println("Loading: " + dicomFile);

			// === Create the Image Source ===
			SourceImage sourceImage = new SourceImage(dicomFile);

			System.out.println("Number of frames: " + sourceImage.getNumberOfFrames());
			System.out.println("Image dimensions: " + sourceImage.getWidth() + " x " + sourceImage.getHeight());

			// === Create the Viewer Window ===
			JFrame frame = new JFrame();
			frame.setTitle("DICOM Viewer - Window/Level and Frame Navigation Demo");
			frame.setBackground(Color.BLACK);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// === Create Custom Image Panel ===
			// OverriddenSingleImagePanelForDemo extends SingleImagePanel
			// with custom behavior for displaying window/level info
			OverriddenSingleImagePanelForDemo imagePanel = new OverriddenSingleImagePanelForDemo(sourceImage);

			frame.add(imagePanel);
			frame.setSize(sourceImage.getWidth() + 50, sourceImage.getHeight() + 80);
			frame.setVisible(true);

			// === Display Instructions ===
			System.out.println("\n--- Controls ---");
			System.out.println("Mouse drag: Adjust window width/level (contrast/brightness)");
			System.out.println("Arrow keys: Navigate frames (for multi-frame images)");
			System.out.println("\nWindow/level and frame info displayed in top-left corner.");

		} catch (Exception e) {
			System.err.println("Error loading DICOM image: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
