package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.network.VerificationSOPClassSCU;

/**
 * DICOM Verification (C-ECHO) with Local Orthanc Server Demo
 *
 * PREREQUISITES: Requires Orthanc server running on localhost:4242.
 * Download Orthanc from: https://www.orthanc-server.com/download.php
 * Default config works out of the box. Web UI at http://localhost:8042.
 *
 * This example demonstrates how to verify DICOM connectivity against
 * a locally running Orthanc DICOM server using the C-ECHO service.
 *
 * Key concepts:
 * - Orthanc: Free, open-source, lightweight DICOM server
 * - C-ECHO: Verifies that a DICOM endpoint is reachable (DICOM "ping")
 * - Local testing: Use localhost to test against a local Orthanc instance
 * - Default Orthanc DICOM port: 4242
 *
 * Setting up Orthanc:
 * 1. Download Orthanc from https://www.orthanc-server.com/
 * 2. Run Orthanc (default configuration is usually sufficient)
 * 3. Orthanc listens on port 4242 for DICOM with AE Title "ORTHANC"
 * 4. Web interface available at http://localhost:8042
 *
 * Orthanc Configuration (orthanc.json):
 * - DicomAet: "ORTHANC" (default)
 * - DicomPort: 4242 (default)
 * - DicomModalities: Configure known remote AE Titles here
 *
 * Success is indicated by the absence of exceptions.
 * This verifies:
 * - Network connectivity to Orthanc
 * - DICOM association establishment
 * - Basic protocol communication
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomVerificationServiceDemoUsingLocalOrthancServer {

	public static void main(String[] args) {

		System.out.println("=== DICOM Verification with Local Orthanc Server ===\n");

		try {

			// === Orthanc Server Configuration ===
			String remoteHost = "localhost";        // Orthanc running locally
			int remotePort = 4242;                  // Default Orthanc DICOM port
			String calledAETitle = "ORTHANC";       // Default Orthanc AE Title
			String callingAETitle = "JAVACLIENT";   // Our client AE Title

			// Connection options
			boolean secureTransport = false;  // TLS not used in this demo
			int debugLevel = 2;               // Verbose output for debugging

			System.out.println("Target: " + remoteHost + ":" + remotePort);
			System.out.println("Called AE Title: " + calledAETitle);
			System.out.println("Calling AE Title: " + callingAETitle);
			System.out.println("\nAttempting C-ECHO to Orthanc...\n");

			// === Perform C-ECHO ===
			// The constructor establishes the connection, performs the echo,
			// and closes the association. Success is indicated by no exception.
			new VerificationSOPClassSCU(
				remoteHost,
				remotePort,
				calledAETitle,
				callingAETitle,
				secureTransport,
				debugLevel
			);

			// If we reach here, C-ECHO was successful
			System.out.println("\nC-ECHO successful!");
			System.out.println("Orthanc server is running and responding.");
			System.out.println("\nYou can now:");
			System.out.println("  - Store DICOM files using C-STORE");
			System.out.println("  - Query studies using C-FIND");
			System.out.println("  - Retrieve images using C-GET/C-MOVE");
			System.out.println("  - Access the web interface at http://localhost:8042");

		} catch (Exception e) {
			System.err.println("\nC-ECHO to Orthanc failed!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nTroubleshooting steps:");
			System.err.println("  1. Ensure Orthanc is running");
			System.err.println("  2. Check if port 4242 is accessible");
			System.err.println("  3. Verify the AE Title in orthanc.json");
			System.err.println("  4. Check for firewall blocking the connection");
			System.err.println("  5. Review Orthanc logs for rejection reasons");
			e.printStackTrace();
		}
	}
}
