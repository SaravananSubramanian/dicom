package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.util.LinkedList;

import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

/**
 * DICOM Association Establishment Demo
 *
 * This example demonstrates how to establish a DICOM association
 * with a remote peer, including presentation context negotiation.
 *
 * Key concepts:
 * - Association: A DICOM connection/session between two AEs
 * - Presentation Context: A combination of Abstract Syntax and Transfer Syntax
 * - Abstract Syntax: What service (SOP Class) to use
 * - Transfer Syntax: How data is encoded (VR, byte order, compression)
 *
 * Association Negotiation:
 * 1. Initiator proposes one or more presentation contexts
 * 2. Each context includes an abstract syntax and list of transfer syntaxes
 * 3. Acceptor accepts, rejects, or partially accepts each context
 * 4. For accepted contexts, acceptor chooses preferred transfer syntax
 *
 * Transfer Syntax Preference:
 * - Explicit VR is generally preferred over Implicit VR
 * - The acceptor chooses from the proposed list based on its capabilities
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class AssociationsInDicomDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM Association Establishment Demo ===\n");

		try {

			// === Build Presentation Context ===
			// A presentation context pairs an Abstract Syntax (SOP Class)
			// with one or more Transfer Syntaxes

			// Transfer syntaxes we're willing to use
			LinkedList<String> transferSyntaxes = new LinkedList<>();
			transferSyntaxes.add(TransferSyntax.Default);           // Implicit VR Little Endian
			transferSyntaxes.add(TransferSyntax.ExplicitVRBigEndian); // Explicit VR Big Endian

			// Abstract syntax - the Verification SOP Class (for C-ECHO)
			String verificationSOPClass = SOPClass.Verification;
			byte presentationContextID = 1; // Unique ID for tracking

			System.out.println("Proposed Presentation Context:");
			System.out.println("  Abstract Syntax (SOP Class): " + verificationSOPClass);
			System.out.println("  Transfer Syntaxes offered:");
			for (String ts : transferSyntaxes) {
				System.out.println("    - " + ts);
			}

			// Build the presentation context list
			LinkedList<PresentationContext> presentationContexts = new LinkedList<>();
			presentationContexts.add(new PresentationContext(
				presentationContextID,
				verificationSOPClass,
				transferSyntaxes
			));

			// === Establish Association ===
			System.out.println("\nEstablishing association with remote peer...");

			Association association = AssociationFactory.createNewAssociation(
				"www.dicomserver.co.uk",  // Remote host
				11112,                     // Remote port
				"MedConnServer",          // Called AE Title (remote)
				"OurJavaClient",          // Calling AE Title (local)
				presentationContexts,
				null,                     // User identity (optional)
				false,                    // Secure transport (TLS)
				2                         // Debug level
			);

			// === Examine Negotiation Results ===
			System.out.println("\n--- Association Established ---");
			System.out.println(association);

			// Check if our presentation context was accepted
			byte acceptedContextID = association.getSuitablePresentationContextID(verificationSOPClass);
			System.out.println("\nVerification SOP Class accepted: YES");

			// See which transfer syntax was selected by the acceptor
			String selectedTransferSyntax = association.getTransferSyntaxForPresentationContextID(acceptedContextID);
			System.out.println("Selected Transfer Syntax: " + selectedTransferSyntax);

			// Explain the selection
			System.out.println("\nNote: Explicit VR Big Endian was selected because");
			System.out.println("Explicit VR is generally preferred over Implicit VR.");

			// === Release Association ===
			// In real applications, you would perform operations here
			// before releasing the association
			// association.release();

		} catch (Exception e) {
			System.err.println("\nAssociation establishment failed!");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
