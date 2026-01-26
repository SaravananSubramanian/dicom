package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.TagFromName;

/**
 * Storage Commitment Event Report Handler
 *
 * This class demonstrates how to parse the N-EVENT-REPORT response
 * that comes back from a Storage Commitment SCP.
 *
 * The response tells you:
 * - Which images were successfully committed (in Referenced SOP Sequence)
 * - Which images failed (in Failed SOP Sequence) and why
 *
 * Event Type IDs:
 * - 1 = Storage Commitment Request Successful (all images committed)
 * - 2 = Storage Commitment Request Complete (some may have failed)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class StorageCommitmentEventHandler {

	// Event Type IDs for Storage Commitment
	public static final int STORAGE_COMMITMENT_SUCCESSFUL = 1;
	public static final int STORAGE_COMMITMENT_COMPLETE = 2;

	// Missing DICOM tags
	private static final AttributeTag TransactionUID = new AttributeTag(0x0008, 0x1195);
	private static final AttributeTag FailedSOPSequence = new AttributeTag(0x0008, 0x1198);
	private static final AttributeTag FailureReason = new AttributeTag(0x0008, 0x1197);

	/**
	 * Parse a Storage Commitment N-EVENT-REPORT response
	 */
	public static void handleEventReport(AttributeList eventInfo, int eventTypeID) {

		System.out.println("=== Storage Commitment Event Report ===\n");

		// Get the Transaction UID - matches the one from our N-ACTION request
		String transactionUID = getStringValue(eventInfo, TransactionUID);
		System.out.println("Transaction UID: " + transactionUID);

		if (eventTypeID == STORAGE_COMMITMENT_SUCCESSFUL) {
			System.out.println("Event Type: SUCCESSFUL (all images committed)\n");
		} else if (eventTypeID == STORAGE_COMMITMENT_COMPLETE) {
			System.out.println("Event Type: COMPLETE (check for failures)\n");
		}

		// Process successfully committed images
		Attribute successSeqAttr = eventInfo.get(TagFromName.ReferencedSOPSequence);
		if (successSeqAttr != null && successSeqAttr instanceof SequenceAttribute) {
			SequenceAttribute successSequence = (SequenceAttribute) successSeqAttr;
			int successCount = successSequence.getNumberOfItems();

			System.out.println("Successfully Committed: " + successCount + " images");

			for (int i = 0; i < successCount; i++) {
				AttributeList item = successSequence.getItem(i).getAttributeList();
				String sopClass = getStringValue(item, TagFromName.ReferencedSOPClassUID);
				String sopInstance = getStringValue(item, TagFromName.ReferencedSOPInstanceUID);

				System.out.println("  [" + (i + 1) + "] SOP Instance: " + sopInstance);
				System.out.println("      SOP Class: " + sopClass);
			}
		}

		// Process failed images (if any)
		Attribute failSeqAttr = eventInfo.get(FailedSOPSequence);
		if (failSeqAttr != null && failSeqAttr instanceof SequenceAttribute) {
			SequenceAttribute failSequence = (SequenceAttribute) failSeqAttr;
			int failCount = failSequence.getNumberOfItems();

			if (failCount > 0) {
				System.out.println("\nFailed to Commit: " + failCount + " images");

				for (int i = 0; i < failCount; i++) {
					AttributeList item = failSequence.getItem(i).getAttributeList();
					String sopClass = getStringValue(item, TagFromName.ReferencedSOPClassUID);
					String sopInstance = getStringValue(item, TagFromName.ReferencedSOPInstanceUID);
					String failureReasonValue = getStringValue(item, FailureReason);

					System.out.println("  [" + (i + 1) + "] SOP Instance: " + sopInstance);
					System.out.println("      SOP Class: " + sopClass);
					System.out.println("      Failure Reason: " + getFailureReasonDescription(failureReasonValue));
				}
			}
		}

		System.out.println("\n=== End Event Report ===");
	}

	/**
	 * Get a human-readable description of the failure reason code
	 */
	private static String getFailureReasonDescription(String failureReason) {
		if (failureReason == null) {
			return "Unknown";
		}

		// Storage Commitment Failure Reason codes (from DICOM PS3.4)
		switch (failureReason) {
			case "0110":
				return "Processing failure";
			case "0112":
				return "No such object instance";
			case "0213":
				return "Resource limitation";
			case "0122":
				return "Referenced SOP Class not supported";
			case "0119":
				return "Class/Instance conflict";
			case "0131":
				return "Duplicate SOP Instance";
			default:
				return "Code: " + failureReason;
		}
	}

	private static String getStringValue(AttributeList list, AttributeTag tag) {
		Attribute attr = list.get(tag);
		return (attr != null) ? attr.getSingleStringValueOrNull() : null;
	}

	/**
	 * Demo main method showing example parsing
	 */
	public static void main(String[] args) {

		System.out.println("=== Storage Commitment Event Handler Demo ===\n");
		System.out.println("This class is used to parse N-EVENT-REPORT responses.");
		System.out.println("In a real implementation, you would:");
		System.out.println("1. Store images via C-STORE");
		System.out.println("2. Request commitment via N-ACTION");
		System.out.println("3. Receive N-EVENT-REPORT (async or via polling)");
		System.out.println("4. Use this handler to parse the response");
		System.out.println("5. Delete local copies of successfully committed images");
		System.out.println("\nExample usage:");
		System.out.println("  StorageCommitmentEventHandler.handleEventReport(eventInfo, eventTypeID);");
	}
}
