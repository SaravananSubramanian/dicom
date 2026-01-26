package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * DICOM QIDO-RS (Query based on ID for DICOM Objects - RESTful Services) Demo
 *
 * PREREQUISITES: Orthanc with DICOMweb plugin on localhost:8042.
 * Enable plugin: Add "DICOMweb" to Orthanc config, restart server.
 * Store some DICOM studies in Orthanc for query results.
 *
 * QIDO-RS is the DICOMweb standard for searching/querying DICOM objects
 * via HTTP/HTTPS RESTful services. It's the RESTful equivalent of C-FIND.
 *
 * Key endpoints:
 * - /studies - Search for studies
 * - /studies/{studyUID}/series - Search for series within a study
 * - /studies/{studyUID}/series/{seriesUID}/instances - Search for instances
 *
 * Query parameters:
 * - PatientName, PatientID - Patient matching
 * - StudyDate, StudyTime - Date/time filtering
 * - Modality, ModalitiesInStudy - Modality filtering
 * - AccessionNumber - Accession matching
 * - limit, offset - Pagination
 * - includefield - Specify fields to return
 *
 * Response format: application/dicom+json
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class QidoRsQueryDemo {

	// Orthanc DICOMweb base URL (requires DICOMweb plugin)
	private static final String DICOMWEB_BASE_URL = "http://localhost:8042/dicom-web";

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM QIDO-RS Query Demo ===\n");

			// Demo 1: Query all studies
			System.out.println("--- Demo 1: Query All Studies (limit 10) ---");
			queryStudies(null, null, null, 10, 0);

			// Demo 2: Query by patient name
			System.out.println("\n--- Demo 2: Query by Patient Name ---");
			queryStudies("Doe*", null, null, 10, 0);

			// Demo 3: Query by modality
			System.out.println("\n--- Demo 3: Query CT Studies ---");
			queryStudies(null, "CT", null, 10, 0);

			// Demo 4: Query by date range
			System.out.println("\n--- Demo 4: Query by Date Range ---");
			queryStudies(null, null, "20240101-20241231", 10, 0);

			// Demo 5: Query series within a study
			System.out.println("\n--- Demo 5: Query Series in a Study ---");
			String exampleStudyUID = "1.2.3.4.5.6.7.8.9";
			querySeriesInStudy(exampleStudyUID);

			// Demo 6: Query instances within a series
			System.out.println("\n--- Demo 6: Query Instances in a Series ---");
			String exampleSeriesUID = "1.2.3.4.5.6.7.8.9.1";
			queryInstancesInSeries(exampleStudyUID, exampleSeriesUID);

			System.out.println("\n=== QIDO-RS Demo Complete ===");

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Query for studies with optional filters
	 */
	private static void queryStudies(String patientName, String modality,
			String studyDate, int limit, int offset) {

		try {
			StringBuilder urlBuilder = new StringBuilder(DICOMWEB_BASE_URL + "/studies?");

			// Add query parameters
			if (patientName != null && !patientName.isEmpty()) {
				urlBuilder.append("PatientName=").append(URLEncoder.encode(patientName, "UTF-8")).append("&");
			}
			if (modality != null && !modality.isEmpty()) {
				urlBuilder.append("ModalitiesInStudy=").append(URLEncoder.encode(modality, "UTF-8")).append("&");
			}
			if (studyDate != null && !studyDate.isEmpty()) {
				urlBuilder.append("StudyDate=").append(URLEncoder.encode(studyDate, "UTF-8")).append("&");
			}

			// Pagination
			urlBuilder.append("limit=").append(limit).append("&");
			urlBuilder.append("offset=").append(offset);

			// Specify fields to include in response
			urlBuilder.append("&includefield=").append(URLEncoder.encode("00100010", "UTF-8")); // PatientName
			urlBuilder.append("&includefield=").append(URLEncoder.encode("00100020", "UTF-8")); // PatientID
			urlBuilder.append("&includefield=").append(URLEncoder.encode("0020000D", "UTF-8")); // StudyInstanceUID
			urlBuilder.append("&includefield=").append(URLEncoder.encode("00080020", "UTF-8")); // StudyDate
			urlBuilder.append("&includefield=").append(URLEncoder.encode("00080061", "UTF-8")); // ModalitiesInStudy
			urlBuilder.append("&includefield=").append(URLEncoder.encode("00081030", "UTF-8")); // StudyDescription

			String url = urlBuilder.toString();
			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/dicom+json");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				String jsonResponse;
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					jsonResponse = response.toString();
				}
				System.out.println("Response length: " + jsonResponse.length() + " chars");

				// Pretty print first part of response
				if (jsonResponse.length() > 800) {
					System.out.println("Response (truncated):\n" + jsonResponse.substring(0, 800) + "...");
				} else {
					System.out.println("Response:\n" + jsonResponse);
				}
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error querying studies: " + e.getMessage());
		}
	}

	/**
	 * Query for series within a specific study
	 */
	private static void querySeriesInStudy(String studyUID) {
		try {
			String url = DICOMWEB_BASE_URL + "/studies/" + studyUID + "/series";

			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/dicom+json");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					System.out.println("Response length: " + response.length() + " chars");
				}
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error querying series: " + e.getMessage());
		}
	}

	/**
	 * Query for instances within a specific series
	 */
	private static void queryInstancesInSeries(String studyUID, String seriesUID) {
		try {
			String url = DICOMWEB_BASE_URL + "/studies/" + studyUID +
						"/series/" + seriesUID + "/instances";

			System.out.println("URL: " + url);

			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/dicom+json");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					System.out.println("Response length: " + response.length() + " chars");
				}
			} else {
				System.out.println("Error: " + conn.getResponseMessage());
			}

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Error querying instances: " + e.getMessage());
		}
	}
}
