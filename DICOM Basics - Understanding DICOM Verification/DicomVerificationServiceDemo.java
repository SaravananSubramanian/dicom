package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.network.VerificationSOPClassSCU;

/**
 * DICOM Verification Service (C-ECHO) Demo
 *
 * This example demonstrates how to verify DICOM network connectivity
 * using the C-ECHO service (also known as DICOM Ping).
 *
 * Key concepts:
 * - C-ECHO is the simplest DICOM network service
 * - Used to verify that a DICOM endpoint is reachable and responding
 * - Tests association establishment and basic communication
 * - Equivalent to "ping" but at the DICOM protocol level
 *
 * DICOM Network Terminology:
 * - SCU (Service Class User): The client that initiates the request (us)
 * - SCP (Service Class Provider): The server that responds (remote PACS)
 * - AE Title: Application Entity Title - identifies a DICOM endpoint
 * - Association: A DICOM connection/session
 *
 * Success is indicated by the absence of exceptions.
 * Common exceptions include:
 * - Connection refused: Server not running or wrong port
 * - Association rejected: AE Title not recognized or not allowed
 * - Timeout: Network issues or server not responding
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomVerificationServiceDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM Verification Service (C-ECHO) Demo ===\n");

		// === Remote Server Configuration ===
		// Using David Clunie's public DICOM server for testing
		String remoteHost = "184.73.255.26";
		int remotePort = 11112;
		String calledAETitle = "AWSPIXELMEDPUB";  // Remote server's AE Title
		String callingAETitle = "JAVACLIENT";     // Our AE Title

		// === Connection Options ===
		boolean secureTransport = false;  // TLS disabled for this demo
		int debugLevel = 2;               // 0=none, higher=more verbose

		System.out.println("Remote Server: " + remoteHost + ":" + remotePort);
		System.out.println("Called AE Title: " + calledAETitle);
		System.out.println("Calling AE Title: " + callingAETitle);
		System.out.println("\nAttempting C-ECHO...");

		try {

			// === Perform C-ECHO ===
			// The constructor establishes connection, performs echo, and closes
			// Success is indicated by normal completion (no exception)
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
			System.out.println("The remote DICOM server is reachable and responding.");

		} catch (Exception e) {
			// C-ECHO failed - could be network, configuration, or server issue
			System.err.println("\nC-ECHO failed!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nPossible causes:");
			System.err.println("  - Server not running or unreachable");
			System.err.println("  - Wrong host/port configuration");
			System.err.println("  - AE Title not recognized by server");
			System.err.println("  - Firewall blocking connection");
			e.printStackTrace();
		}
	}
}
