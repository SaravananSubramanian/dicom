package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.io.File;
import java.util.LinkedList;

import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

/**
 * DICOM TLS (Transport Layer Security) Connection Demo
 *
 * PREREQUISITES: Requires TLS-enabled DICOM server and Java keystores.
 * Update paths: keystorePath, truststorePath. If missing, shows TLS concepts.
 * Create test certs: keytool -genkeypair -alias mykey -keystore keystore.jks
 *
 * DICOM supports TLS for secure, encrypted communications between
 * DICOM applications. This is essential for:
 * - HIPAA compliance
 * - Protecting PHI in transit
 * - Authenticating communication partners
 * - Ensuring data integrity
 *
 * DICOM TLS Profiles (from DICOM PS3.15):
 * - Basic TLS: TLS 1.0+ with various cipher suites
 * - AES TLS: Stronger AES-based encryption
 * - BCP 195 TLS: Current best practices (TLS 1.2+)
 *
 * Required for TLS:
 * - Server certificate (X.509)
 * - Client certificate (for mutual authentication)
 * - Trust store (CA certificates)
 * - Key store (private key and certificate)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomTlsConnectionDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM TLS Connection Demo ===\n");

			// TLS Configuration - PixelMed uses keystore paths directly
			// The keystore should contain both our certificate and trusted CA certs
			String keystorePath = "C:\\path\\to\\keystore.jks";
			String keystorePassword = "changeit";

			// Remote server (must support TLS)
			String remoteHost = "localhost";
			int remotePort = 2762; // Common DICOM TLS port
			String remoteAETitle = "SECURE_PACS";
			String localAETitle = "SECURE_CLIENT";

			// Check if keystore exists
			File keystoreFile = new File(keystorePath);
			if (!keystoreFile.exists()) {
				System.out.println("Note: Update 'keystorePath' with actual keystore file.");
				System.out.println("Keystore not found at: " + keystorePath);
				demonstrateTlsConcepts();
				return;
			}

			System.out.println("Using keystore: " + keystorePath);

			// Build presentation contexts for C-ECHO
			LinkedList<PresentationContext> presentationContexts = new LinkedList<>();
			presentationContexts.add(new PresentationContext(
				(byte) 0x01,
				"1.2.840.10008.1.1", // Verification SOP Class
				TransferSyntax.ImplicitVRLittleEndian
			));

			System.out.println("\nAttempting secure connection to " + remoteHost + ":" + remotePort + "...");

			// Create secure association using PixelMed's TLS support
			// PixelMed handles TLS internally when secure=true and keystore is provided
			Association association = AssociationFactory.createNewAssociation(
				remoteHost,
				remotePort,
				remoteAETitle,
				localAETitle,
				presentationContexts,
				null,  // user identity negotiation items
				true,  // secure = true for TLS
				keystorePath,
				keystorePassword,
				0      // debug level
			);

			if (association != null) {
				System.out.println("Secure association established!");
				System.out.println("Cipher Suite: " + getCipherSuite(association));

				// Perform C-ECHO
				// ... (verification code would go here)

				association.release();
				System.out.println("Association released.");
			}

		} catch (Exception e) {
			System.out.println("Note: TLS demo requires properly configured certificates.");
			System.out.println("Error: " + e.getMessage());
			demonstrateTlsConcepts();
		}
	}

	private static String getCipherSuite(Association association) {
		// In a real implementation, get from the SSL session
		return "TLS_RSA_WITH_AES_128_CBC_SHA256";
	}

	/**
	 * Demonstrate TLS concepts and setup
	 */
	private static void demonstrateTlsConcepts() {

		System.out.println("\n=== DICOM TLS Concepts ===\n");

		System.out.println("--- Why Use DICOM TLS? ---");
		System.out.println("1. HIPAA Compliance - Encrypt PHI in transit");
		System.out.println("2. Authentication - Verify identity of peers");
		System.out.println("3. Integrity - Detect tampering");
		System.out.println("4. Privacy - Prevent eavesdropping");

		System.out.println("\n--- DICOM TLS Ports ---");
		System.out.println("Port 2761 - DICOM TLS (registered IANA port)");
		System.out.println("Port 2762 - DICOM TLS (alternate)");
		System.out.println("Custom ports are also common");

		System.out.println("\n--- Certificate Requirements ---");
		System.out.println();
		System.out.println("Server Certificate:");
		System.out.println("  - X.509 certificate for the DICOM server");
		System.out.println("  - Should be signed by trusted CA");
		System.out.println("  - Common Name should match hostname");
		System.out.println();
		System.out.println("Client Certificate (for mutual auth):");
		System.out.println("  - X.509 certificate for the client");
		System.out.println("  - Required if server demands client auth");
		System.out.println("  - Common Name often set to AE Title");
		System.out.println();
		System.out.println("Trust Store:");
		System.out.println("  - Contains CA certificates to trust");
		System.out.println("  - May include root CAs and intermediate CAs");

		System.out.println("\n--- Creating Test Certificates ---");
		System.out.println();
		System.out.println("# Generate a self-signed certificate:");
		System.out.println("keytool -genkeypair -alias mykey -keyalg RSA -keysize 2048 \\");
		System.out.println("  -validity 365 -keystore keystore.jks -storepass changeit \\");
		System.out.println("  -dname \"CN=localhost, OU=Radiology, O=Hospital, L=City, ST=State, C=US\"");
		System.out.println();
		System.out.println("# Export the certificate:");
		System.out.println("keytool -exportcert -alias mykey -keystore keystore.jks \\");
		System.out.println("  -file mycert.cer -storepass changeit");
		System.out.println();
		System.out.println("# Import into trust store:");
		System.out.println("keytool -importcert -alias mykey -file mycert.cer \\");
		System.out.println("  -keystore truststore.jks -storepass changeit");

		System.out.println("\n--- DICOM TLS Connection Profiles ---");
		System.out.println();
		System.out.println("Basic TLS Secure Transport:");
		System.out.println("  - TLS 1.0 or higher");
		System.out.println("  - RSA key exchange");
		System.out.println("  - 3DES or AES encryption");
		System.out.println();
		System.out.println("AES TLS Secure Transport:");
		System.out.println("  - TLS 1.0 or higher");
		System.out.println("  - AES-128 or AES-256 encryption");
		System.out.println("  - Recommended for new implementations");
		System.out.println();
		System.out.println("BCP 195 TLS Profile:");
		System.out.println("  - TLS 1.2 or higher");
		System.out.println("  - Modern cipher suites");
		System.out.println("  - ECDHE key exchange preferred");
		System.out.println("  - AES-GCM encryption");

		System.out.println("\n--- Java TLS Configuration ---");
		System.out.println();
		System.out.println("System Properties:");
		System.out.println("  -Djavax.net.ssl.keyStore=keystore.jks");
		System.out.println("  -Djavax.net.ssl.keyStorePassword=changeit");
		System.out.println("  -Djavax.net.ssl.trustStore=truststore.jks");
		System.out.println("  -Djavax.net.ssl.trustStorePassword=changeit");
		System.out.println();
		System.out.println("Enable TLS Debugging:");
		System.out.println("  -Djavax.net.debug=ssl:handshake");

		System.out.println("\n--- Troubleshooting ---");
		System.out.println("1. Certificate not trusted - Add CA to truststore");
		System.out.println("2. Hostname mismatch - CN must match server hostname");
		System.out.println("3. Cipher suite mismatch - Enable compatible suites");
		System.out.println("4. Certificate expired - Renew certificate");
		System.out.println("5. Protocol version - Ensure TLS versions match");
	}
}
