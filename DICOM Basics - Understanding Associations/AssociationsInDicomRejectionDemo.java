package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.util.LinkedList;

import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

/**
 * DICOM Association Rejection Demo
 *
 * This example demonstrates what happens when a presentation context
 * is rejected due to unsupported transfer syntax.
 *
 * Key concepts:
 * - Presentation contexts can be rejected by the acceptor
 * - Rejection happens when the acceptor doesn't support the proposed services
 * - Common rejection reasons:
 *   - Unsupported abstract syntax (SOP Class)
 *   - Unsupported transfer syntax
 *   - Temporary congestion or resource limits
 *
 * In this demo:
 * - We propose JPEG 2000 Lossless transfer syntax for Verification
 * - This combination doesn't make sense (Verification has no pixel data)
 * - The server will accept the association but may reject this context
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class AssociationsInDicomRejectionDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM Association Rejection Demo ===\n");

		try {

			// === Build Presentation Context with Unusual Transfer Syntax ===
			// JPEG 2000 doesn't make sense for Verification (no pixel data)
			LinkedList<String> transferSyntaxes = new LinkedList<>();
			transferSyntaxes.add(TransferSyntax.JPEG2000Lossless);

			String verificationSOPClass = SOPClass.Verification;
			byte presentationContextID = 1;

			System.out.println("Proposed Presentation Context:");
			System.out.println("  Abstract Syntax: Verification SOP Class");
			System.out.println("  Transfer Syntax: JPEG 2000 Lossless");
			System.out.println("\nNote: This is an unusual combination and may be rejected.");

			LinkedList<PresentationContext> presentationContexts = new LinkedList<>();
			presentationContexts.add(new PresentationContext(
				presentationContextID,
				verificationSOPClass,
				transferSyntaxes
			));

			// === Establish Association ===
			System.out.println("\nEstablishing association...");

			Association association = AssociationFactory.createNewAssociation(
				"www.dicomserver.co.uk",
				11112,
				"MedConnServer",
				"OurJavaClient",
				presentationContexts,
				null,
				false,
				2
			);

			// === Try to Use the Context ===
			// This should throw an exception because the transfer syntax
			// is likely not supported for this abstract syntax

			System.out.println("\nTrying to use the presentation context...");

			try {
				byte contextID = association.getSuitablePresentationContextID(
					verificationSOPClass,
					TransferSyntax.JPEG2000Lossless
				);
				System.out.println("Context accepted with ID: " + contextID);

			} catch (Exception e) {
				System.out.println("\n--- Presentation Context Rejected ---");
				System.out.println("Exception: " + e.getMessage());
				System.out.println("\nThe transfer syntax JPEG 2000 Lossless (" +
					TransferSyntax.JPEG2000Lossless + ")");
				System.out.println("is not supported for the Verification SOP Class.");
			}

			// === Explain Rejection Reasons ===
			System.out.println("\n--- Why Contexts Get Rejected ---");
			System.out.println("1. User Rejection: SCP chooses not to accept");
			System.out.println("2. No Reason: SCP cannot give a specific reason");
			System.out.println("3. Abstract Syntax Not Supported: SOP Class unknown");
			System.out.println("4. Transfer Syntax Not Supported: Encoding not supported");
			System.out.println("5. Temporary Congestion: Try again later");

		} catch (Exception e) {
			System.err.println("\nError: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
