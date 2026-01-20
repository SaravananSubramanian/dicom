package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * DICOM STOW-RS (Store Over the Web - RESTful Services) Demo
 *
 * PREREQUISITES: (1) Orthanc with DICOMweb plugin on localhost:8042.
 * (2) DICOM file to upload - update 'dicomFilePath' variable.
 * If no file provided, demo shows request structure without actual upload.
 *
 * STOW-RS is the DICOMweb standard for storing DICOM objects
 * via HTTP/HTTPS RESTful services. It's the RESTful equivalent of C-STORE.
 *
 * Key endpoint:
 * - POST /studies - Store instances (creates new study if needed)
 * - POST /studies/{studyUID} - Store instances to specific study
 *
 * Request format: multipart/related; type="application/dicom"
 * Each part is a DICOM object with Content-Type: application/dicom
 *
 * Response: XML or JSON with storage results
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class StowRsStoreDemo {

	// Orthanc DICOMweb base URL (requires DICOMweb plugin)
	private static final String DICOMWEB_BASE_URL = "http://localhost:8042/dicom-web";

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM STOW-RS Store Demo ===\n");

			// Example DICOM file to store
			String dicomFilePath = "C:\\path\\to\\your\\dicom\\file.dcm";

			// Check if file exists
			File dicomFile = new File(dicomFilePath);
			if (!dicomFile.exists()) {
				System.out.println("Note: Update 'dicomFilePath' variable with an actual DICOM file path.");
				System.out.println("Demonstrating STOW-RS request structure...\n");
				demonstrateStowRsStructure();
				return;
			}

			// Store single DICOM file
			System.out.println("--- Storing DICOM file via STOW-RS ---");
			storeDicomFile(dicomFilePath);

			System.out.println("\n=== STOW-RS Demo Complete ===");

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Store a single DICOM file via STOW-RS
	 */
	private static void storeDicomFile(String dicomFilePath) {

		try {
			String url = DICOMWEB_BASE_URL + "/studies";
			System.out.println("URL: " + url);

			// Create unique boundary for multipart request
			String boundary = "----DicomBoundary" + UUID.randomUUID().toString();

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
				"multipart/related; type=\"application/dicom\"; boundary=" + boundary);
			conn.setRequestProperty("Accept", "application/dicom+json");

			// Read DICOM file
			File file = new File(dicomFilePath);
			FileInputStream fis = new FileInputStream(file);
			byte[] fileBytes = new byte[(int) file.length()];
			fis.read(fileBytes);
			fis.close();

			// Build multipart request body
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			// Start boundary
			dos.writeBytes("--" + boundary + "\r\n");
			dos.writeBytes("Content-Type: application/dicom\r\n");
			dos.writeBytes("\r\n");

			// Write DICOM file content
			dos.write(fileBytes);
			dos.writeBytes("\r\n");

			// End boundary
			dos.writeBytes("--" + boundary + "--\r\n");
			dos.flush();
			dos.close();

			// Get response
			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200 || responseCode == 202) {
				InputStream is = conn.getInputStream();
				byte[] buffer = new byte[4096];
				StringBuilder response = new StringBuilder();
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					response.append(new String(buffer, 0, bytesRead));
				}
				is.close();

				System.out.println("Store successful!");
				System.out.println("Response: " + response.toString());
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
				InputStream es = conn.getErrorStream();
				if (es != null) {
					byte[] buffer = new byte[4096];
					StringBuilder error = new StringBuilder();
					int bytesRead;
					while ((bytesRead = es.read(buffer)) != -1) {
						error.append(new String(buffer, 0, bytesRead));
					}
					es.close();
					System.out.println("Error details: " + error.toString());
				}
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error storing DICOM: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Store multiple DICOM files in a single request
	 */
	public static void storeMultipleDicomFiles(String[] dicomFilePaths) {

		try {
			String url = DICOMWEB_BASE_URL + "/studies";
			System.out.println("URL: " + url);

			String boundary = "----DicomBoundary" + UUID.randomUUID().toString();

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
				"multipart/related; type=\"application/dicom\"; boundary=" + boundary);
			conn.setRequestProperty("Accept", "application/dicom+json");

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			// Add each DICOM file as a part
			for (String filePath : dicomFilePaths) {
				File file = new File(filePath);
				if (!file.exists()) {
					System.out.println("Skipping non-existent file: " + filePath);
					continue;
				}

				FileInputStream fis = new FileInputStream(file);
				byte[] fileBytes = new byte[(int) file.length()];
				fis.read(fileBytes);
				fis.close();

				dos.writeBytes("--" + boundary + "\r\n");
				dos.writeBytes("Content-Type: application/dicom\r\n");
				dos.writeBytes("\r\n");
				dos.write(fileBytes);
				dos.writeBytes("\r\n");

				System.out.println("Added: " + file.getName() + " (" + fileBytes.length + " bytes)");
			}

			// End boundary
			dos.writeBytes("--" + boundary + "--\r\n");
			dos.flush();
			dos.close();

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error storing DICOM files: " + e.getMessage());
		}
	}

	/**
	 * Demonstrate the structure of a STOW-RS request
	 */
	private static void demonstrateStowRsStructure() {

		System.out.println("=== STOW-RS Request Structure ===\n");

		System.out.println("HTTP Method: POST");
		System.out.println("URL: " + DICOMWEB_BASE_URL + "/studies");
		System.out.println();

		System.out.println("Request Headers:");
		System.out.println("  Content-Type: multipart/related; type=\"application/dicom\"; boundary=DicomBoundary123");
		System.out.println("  Accept: application/dicom+json");
		System.out.println();

		System.out.println("Request Body (multipart):");
		System.out.println("  --DicomBoundary123");
		System.out.println("  Content-Type: application/dicom");
		System.out.println("  ");
		System.out.println("  [DICOM file 1 binary content]");
		System.out.println("  --DicomBoundary123");
		System.out.println("  Content-Type: application/dicom");
		System.out.println("  ");
		System.out.println("  [DICOM file 2 binary content]");
		System.out.println("  --DicomBoundary123--");
		System.out.println();

		System.out.println("Success Response (200 OK):");
		System.out.println("  {");
		System.out.println("    \"00081190\": { \"vr\": \"UR\", \"Value\": [\"http://...url.../studies/1.2.3...\"] },");
		System.out.println("    \"00081199\": { \"vr\": \"SQ\", \"Value\": [");
		System.out.println("      {");
		System.out.println("        \"00081150\": { \"vr\": \"UI\", \"Value\": [\"1.2.840.10008.5.1.4.1.1.2\"] },");
		System.out.println("        \"00081155\": { \"vr\": \"UI\", \"Value\": [\"1.2.3.4.5.6.7.8.9\"] },");
		System.out.println("        \"00081190\": { \"vr\": \"UR\", \"Value\": [\"http://...url.../instances/...\"] }");
		System.out.println("      }");
		System.out.println("    ]}");
		System.out.println("  }");
		System.out.println();

		System.out.println("=== Response Status Codes ===");
		System.out.println("200 OK - All instances stored successfully");
		System.out.println("202 Accepted - Request accepted, processing may continue");
		System.out.println("400 Bad Request - Invalid request format");
		System.out.println("401 Unauthorized - Authentication required");
		System.out.println("403 Forbidden - Not authorized to store");
		System.out.println("409 Conflict - SOP Instance already exists");
		System.out.println("503 Service Unavailable - Server temporarily unavailable");
	}
}
