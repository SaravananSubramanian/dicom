package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * DICOM WADO-RS (Web Access to DICOM Objects - RESTful Services) Demo
 *
 * PREREQUISITES: Orthanc with DICOMweb plugin on localhost:8042.
 * Enable plugin: Add "DICOMweb" to Orthanc config, restart server.
 * Download plugin: https://www.orthanc-server.com/download.php
 * Update UIDs in code with actual study/series/instance UIDs from your server.
 *
 * WADO-RS is part of the DICOMweb standard for retrieving DICOM objects
 * via HTTP/HTTPS RESTful services.
 *
 * Key endpoints:
 * - /studies/{studyUID} - Retrieve entire study
 * - /studies/{studyUID}/series/{seriesUID} - Retrieve entire series
 * - /studies/{studyUID}/series/{seriesUID}/instances/{instanceUID} - Retrieve single instance
 * - /studies/{studyUID}/series/{seriesUID}/instances/{instanceUID}/frames/{frameNumbers} - Retrieve frames
 * - /studies/{studyUID}/series/{seriesUID}/instances/{instanceUID}/rendered - Retrieve rendered image
 *
 * Accept header determines response format:
 * - multipart/related; type="application/dicom" - DICOM objects
 * - multipart/related; type="application/octet-stream" - Bulk data
 * - image/jpeg, image/png - Rendered images
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class WadoRsRetrieveDemo {

	// Orthanc DICOMweb base URL (requires DICOMweb plugin)
	private static final String DICOMWEB_BASE_URL = "http://localhost:8042/dicom-web";

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM WADO-RS Retrieve Demo ===\n");

			// Example UIDs - replace with actual UIDs from your PACS
			String studyUID = "1.2.3.4.5.6.7.8.9";
			String seriesUID = "1.2.3.4.5.6.7.8.9.1";
			String instanceUID = "1.2.3.4.5.6.7.8.9.1.1";

			// Demo 1: Retrieve study metadata
			System.out.println("--- Demo 1: Retrieve Study Metadata ---");
			retrieveStudyMetadata(studyUID);

			// Demo 2: Retrieve single instance as DICOM
			System.out.println("\n--- Demo 2: Retrieve Single Instance ---");
			retrieveInstance(studyUID, seriesUID, instanceUID, "downloaded_instance.dcm");

			// Demo 3: Retrieve rendered image (JPEG)
			System.out.println("\n--- Demo 3: Retrieve Rendered Image ---");
			retrieveRenderedImage(studyUID, seriesUID, instanceUID, "rendered_image.jpg");

			// Demo 4: Retrieve specific frame
			System.out.println("\n--- Demo 4: Retrieve Specific Frame ---");
			retrieveFrame(studyUID, seriesUID, instanceUID, 1, "frame_1.jpg");

			System.out.println("\n=== WADO-RS Demo Complete ===");

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Retrieve study metadata (JSON format)
	 */
	private static void retrieveStudyMetadata(String studyUID) {
		try {
			String url = DICOMWEB_BASE_URL + "/studies/" + studyUID + "/metadata";

			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/dicom+json");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				byte[] buffer = new byte[4096];
				StringBuilder response = new StringBuilder();
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					response.append(new String(buffer, 0, bytesRead));
				}
				is.close();

				// Print first 500 chars of response
				String jsonResponse = response.toString();
				if (jsonResponse.length() > 500) {
					System.out.println("Response (truncated): " + jsonResponse.substring(0, 500) + "...");
				} else {
					System.out.println("Response: " + jsonResponse);
				}
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error retrieving metadata: " + e.getMessage());
		}
	}

	/**
	 * Retrieve a single DICOM instance
	 */
	private static void retrieveInstance(String studyUID, String seriesUID,
			String instanceUID, String outputFile) {
		try {
			String url = DICOMWEB_BASE_URL + "/studies/" + studyUID +
						"/series/" + seriesUID + "/instances/" + instanceUID;

			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			// Request DICOM format
			conn.setRequestProperty("Accept", "multipart/related; type=\"application/dicom\"");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(outputFile);

				byte[] buffer = new byte[8192];
				int bytesRead;
				long totalBytes = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
					totalBytes += bytesRead;
				}

				fos.close();
				is.close();
				System.out.println("Saved to: " + outputFile + " (" + totalBytes + " bytes)");
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error retrieving instance: " + e.getMessage());
		}
	}

	/**
	 * Retrieve a rendered image (JPEG)
	 */
	private static void retrieveRenderedImage(String studyUID, String seriesUID,
			String instanceUID, String outputFile) {
		try {
			String url = DICOMWEB_BASE_URL + "/studies/" + studyUID +
						"/series/" + seriesUID + "/instances/" + instanceUID + "/rendered";

			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/jpeg");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(outputFile);

				byte[] buffer = new byte[8192];
				int bytesRead;
				long totalBytes = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
					totalBytes += bytesRead;
				}

				fos.close();
				is.close();
				System.out.println("Saved to: " + outputFile + " (" + totalBytes + " bytes)");
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error retrieving rendered image: " + e.getMessage());
		}
	}

	/**
	 * Retrieve a specific frame from a multi-frame instance
	 */
	private static void retrieveFrame(String studyUID, String seriesUID,
			String instanceUID, int frameNumber, String outputFile) {
		try {
			String url = DICOMWEB_BASE_URL + "/studies/" + studyUID +
						"/series/" + seriesUID + "/instances/" + instanceUID +
						"/frames/" + frameNumber + "/rendered";

			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/jpeg");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(outputFile);

				byte[] buffer = new byte[8192];
				int bytesRead;
				long totalBytes = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
					totalBytes += bytesRead;
				}

				fos.close();
				is.close();
				System.out.println("Saved to: " + outputFile + " (" + totalBytes + " bytes)");
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error retrieving frame: " + e.getMessage());
		}
	}
}
