package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.util.LinkedList;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UIDGenerator;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

/**
 * DICOM Storage Commitment Demo
 *
 * PREREQUISITES: Orthanc with Storage Commitment plugin on localhost:4242.
 * Note: Full Storage Commitment requires async N-EVENT-REPORT handling.
 * This demo shows request building; actual N-ACTION sending requires extended implementation.
 *
 * Storage Commitment is a DICOM service that allows an SCU (like a modality)
 * to request confirmation from an SCP (like a PACS) that images have been
 * safely stored and can be deleted from the modality's local storage.
 *
 * How it works:
 * 1. SCU sends images via C-STORE
 * 2. SCU requests storage commitment via N-ACTION
 * 3. SCP verifies images are stored and responds via N-EVENT-REPORT
 * 4. SCU can safely delete local copies
 *
 * This is critical for:
 * - Modalities with limited storage
 * - Legal/regulatory compliance (confirming archive before deletion)
 * - Workflow automation
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class StorageCommitmentDemo {

	// Storage Commitment SOP Class UID
	private static final String STORAGE_COMMITMENT_PUSH_MODEL_SOP_CLASS = "1.2.840.10008.1.20.1";
	private static final String STORAGE_COMMITMENT_PUSH_MODEL_SOP_INSTANCE = "1.2.840.10008.1.20.1.1";

	// Missing DICOM tag
	private static final AttributeTag TransactionUID = new AttributeTag(0x0008, 0x1195);

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Storage Commitment Demo ===\n");

			// Connection parameters
			String remoteHost = "localhost";
			int remotePort = 4242;
			String remoteAETitle = "ORTHANC";
			String localAETitle = "STORAGECOMMIT";

			// Example SOP Instance UIDs that were previously stored
			// In real usage, these would be the UIDs of images you sent via C-STORE
			String[] sopInstanceUIDs = {
				"1.2.3.4.5.6.7.8.9.1",
				"1.2.3.4.5.6.7.8.9.2",
				"1.2.3.4.5.6.7.8.9.3"
			};

			// Corresponding SOP Class UIDs for each instance
			String[] sopClassUIDs = {
				SOPClass.CTImageStorage,
				SOPClass.CTImageStorage,
				SOPClass.CTImageStorage
			};

			System.out.println("Requesting storage commitment for " + sopInstanceUIDs.length + " images...\n");

			// Build the presentation contexts for Storage Commitment
			LinkedList<PresentationContext> presentationContexts = new LinkedList<>();

			// Storage Commitment Push Model
			presentationContexts.add(new PresentationContext(
				(byte) 0x01,
				STORAGE_COMMITMENT_PUSH_MODEL_SOP_CLASS,
				TransferSyntax.ImplicitVRLittleEndian
			));

			presentationContexts.add(new PresentationContext(
				(byte) 0x03,
				STORAGE_COMMITMENT_PUSH_MODEL_SOP_CLASS,
				TransferSyntax.ExplicitVRLittleEndian
			));

			System.out.println("Establishing association with " + remoteAETitle + "...");

			// Create the association
			Association association = AssociationFactory.createNewAssociation(
				remoteHost,
				remotePort,
				remoteAETitle,
				localAETitle,
				presentationContexts,
				null,  // no user identity
				false, // not secure
				0      // debugLevel
			);

			if (association != null) {
				System.out.println("Association established successfully.\n");

				// Build the N-ACTION request
				AttributeList actionInfo = buildStorageCommitmentRequest(sopInstanceUIDs, sopClassUIDs);

				System.out.println("Storage Commitment Request Details:");
				System.out.println("Transaction UID: " + actionInfo.get(TransactionUID).getSingleStringValueOrNull());
				System.out.println("Number of references: " + sopInstanceUIDs.length);

				// In a real implementation, you would:
				// 1. Send the N-ACTION request using the association
				// 2. Wait for or poll for the N-EVENT-REPORT response
				// 3. Parse the response to see which images were successfully committed

				// Note: PixelMed's full N-ACTION implementation requires additional setup.
				// This demo shows the request building process.

				System.out.println("\n--- Request Attribute List ---");
				System.out.println(actionInfo.toString());

				// Release the association
				association.release();
				System.out.println("\nAssociation released.");

			} else {
				System.err.println("Failed to establish association.");
			}

			System.out.println("\n=== Storage Commitment Demo Complete ===");
			System.out.println("\nNote: Full N-ACTION/N-EVENT-REPORT requires SCP support.");
			System.out.println("Orthanc supports Storage Commitment with the appropriate plugin.");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * Builds the attribute list for a Storage Commitment N-ACTION request
	 */
	private static AttributeList buildStorageCommitmentRequest(
			String[] sopInstanceUIDs, String[] sopClassUIDs) throws Exception {

		AttributeList actionInfo = new AttributeList();

		// Generate a unique Transaction UID for this commitment request
		String transactionUIDValue = new UIDGenerator().getNewUID();
		Attribute transactionUIDAttr = new UniqueIdentifierAttribute(TransactionUID);
		transactionUIDAttr.addValue(transactionUIDValue);
		actionInfo.put(transactionUIDAttr);

		// Build the Referenced SOP Sequence
		// This contains all the images we want committed
		SequenceAttribute referencedSOPSequence = new SequenceAttribute(TagFromName.ReferencedSOPSequence);

		for (int i = 0; i < sopInstanceUIDs.length; i++) {
			AttributeList item = new AttributeList();

			Attribute sopClassAttr = new UniqueIdentifierAttribute(TagFromName.ReferencedSOPClassUID);
			sopClassAttr.addValue(sopClassUIDs[i]);
			item.put(sopClassAttr);

			Attribute sopInstanceAttr = new UniqueIdentifierAttribute(TagFromName.ReferencedSOPInstanceUID);
			sopInstanceAttr.addValue(sopInstanceUIDs[i]);
			item.put(sopInstanceAttr);

			referencedSOPSequence.addItem(item);
		}

		actionInfo.put(referencedSOPSequence);

		return actionInfo;
	}
}
