package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.network.MultipleInstanceTransferStatusHandler;
import com.pixelmed.network.StorageSOPClassSCU;

/**
 * DICOM C-STORE Service Demo
 *
 * PREREQUISITES: (1) DICOM files to send - update paths in 'dicomFile1/2/3' variables.
 * (2) Orthanc server running on localhost:4242 (or update remoteHost/remotePort).
 * Download Orthanc: https://www.orthanc-server.com/download.php
 *
 * This example demonstrates how to send (store) DICOM files to a remote
 * DICOM server using the C-STORE service.
 *
 * Key concepts:
 * - C-STORE is used to send DICOM objects to a storage provider (SCP)
 * - Multiple files can be queued and sent in a single session
 * - Each file is sent individually over the association
 * - Status handler provides progress updates for each transfer
 *
 * C-STORE Workflow:
 * 1. Establish association with storage SCP
 * 2. Negotiate presentation contexts for each SOP Class
 * 3. Send each DICOM object using C-STORE request
 * 4. Receive C-STORE response (success/failure)
 * 5. Release association when done
 *
 * Status Handler provides:
 * - Number of files remaining
 * - Number of successful transfers
 * - Number of failed transfers
 * - Number of warnings
 * - SOP Instance UID of current file
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomCStoreFunctionalityDemo {

	public static void main(String[] args) {

		System.out.println("=== DICOM C-STORE Service Demo ===\n");

		try {

			// === Configuration ===
			String baseDirectory = "C:\\temp\\sample_images";

			// Files to send
			String dicomFile1 = baseDirectory + "\\MR-MONO2-16-head";
			String dicomFile2 = baseDirectory + "\\CT-MONO2-16-brain";
			String dicomFile3 = baseDirectory + "\\CT-MONO2-16-ankle";

			// Remote storage server (Orthanc running locally)
			String remoteHost = "localhost";
			int remotePort = 4242;
			String calledAETitle = "ORTHANC";
			String callingAETitle = "JavaStoreSCU";

			// === Build the Set of Files to Transfer ===
			SetOfDicomFiles filesToSend = new SetOfDicomFiles();
			filesToSend.add(dicomFile1);
			filesToSend.add(dicomFile2);
			filesToSend.add(dicomFile3);

			System.out.println("Files to transfer:");
			System.out.println("  1. " + dicomFile1);
			System.out.println("  2. " + dicomFile2);
			System.out.println("  3. " + dicomFile3);
			System.out.println();

			System.out.println("Destination: " + remoteHost + ":" + remotePort);
			System.out.println("Called AE Title: " + calledAETitle);
			System.out.println("\nInitiating C-STORE operation...\n");

			// === Perform C-STORE ===
			// Behind the scenes, this establishes an association,
			// negotiates presentation contexts, and sends each file
			new StorageSOPClassSCU(
				remoteHost,
				remotePort,
				calledAETitle,
				callingAETitle,
				filesToSend,
				0,  // Compression level (0 = none)
				new TransferProgressHandler()
			);

			System.out.println("\n=== C-STORE operation completed ===");

		} catch (Exception e) {
			System.err.println("\nC-STORE failed!");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\nCommon causes:");
			System.err.println("  - Storage server not running");
			System.err.println("  - Wrong host/port/AE Title");
			System.err.println("  - SOP Class not supported by server");
			System.err.println("  - File not found or invalid DICOM");
			e.printStackTrace();
		}
	}
}

/**
 * Custom handler to track C-STORE transfer progress
 */
class TransferProgressHandler extends MultipleInstanceTransferStatusHandler {

	private int totalProcessed = 0;

	@Override
	public void updateStatus(
			int nRemaining,
			int nCompleted,
			int nFailed,
			int nWarning,
			String sopInstanceUID) {

		totalProcessed++;
		System.out.println("--- Transfer #" + totalProcessed + " ---");
		System.out.println("  SOP Instance UID: " + sopInstanceUID);
		System.out.println("  Remaining: " + nRemaining);
		System.out.println("  Completed: " + nCompleted);
		System.out.println("  Failed: " + nFailed);
		System.out.println("  Warnings: " + nWarning);
		System.out.println();
	}
}
