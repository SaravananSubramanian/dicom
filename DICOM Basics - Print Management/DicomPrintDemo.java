package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.util.LinkedList;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

/**
 * DICOM Print Management Demo
 *
 * PREREQUISITES: Requires DICOM print server (film printer or software like PrintSCP).
 * If unavailable, demo displays print workflow and attribute reference.
 * Most PACS viewers have print functionality built-in.
 *
 * DICOM Print enables sending images to film printers or digital printers.
 * It's still used in radiology for creating hard copies and film jackets.
 *
 * Print Hierarchy:
 * - Film Session: Overall print job settings
 *   - Film Box: A single sheet of film
 *     - Image Box: A single image position on the film
 *
 * Key SOP Classes:
 * - Basic Film Session (1.2.840.10008.5.1.1.1)
 * - Basic Film Box (1.2.840.10008.5.1.1.2)
 * - Basic Grayscale Image Box (1.2.840.10008.5.1.1.4)
 * - Basic Color Image Box (1.2.840.10008.5.1.1.4.1)
 * - Basic Grayscale Print Meta SOP Class (1.2.840.10008.5.1.1.9)
 *
 * Print Operations:
 * - N-CREATE: Create session, film box
 * - N-SET: Set image data in image box
 * - N-ACTION: Print the film
 * - N-DELETE: Clean up
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class DicomPrintDemo {

	// Print Management SOP Class UIDs
	private static final String BASIC_FILM_SESSION = "1.2.840.10008.5.1.1.1";
	private static final String BASIC_FILM_BOX = "1.2.840.10008.5.1.1.2";
	private static final String BASIC_GRAYSCALE_IMAGE_BOX = "1.2.840.10008.5.1.1.4";
	private static final String PRINTER_SOP_CLASS = "1.2.840.10008.5.1.1.16";
	private static final String BASIC_GRAYSCALE_PRINT_META = "1.2.840.10008.5.1.1.9";

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Print Management Demo ===\n");

			// Print server connection parameters
			String printerHost = "localhost";
			int printerPort = 104;
			String printerAETitle = "PRINT_SCP";
			String localAETitle = "PRINT_SCU";

			System.out.println("Connecting to DICOM printer...");
			System.out.println("Host: " + printerHost + ":" + printerPort);
			System.out.println("AE Title: " + printerAETitle);

			// Build presentation contexts for print
			LinkedList<PresentationContext> presentationContexts = new LinkedList<>();

			byte pcId = 1;

			// Basic Grayscale Print Meta SOP Class
			presentationContexts.add(new PresentationContext(
				pcId++,
				BASIC_GRAYSCALE_PRINT_META,
				TransferSyntax.ImplicitVRLittleEndian
			));

			// Basic Film Session
			presentationContexts.add(new PresentationContext(
				pcId++,
				BASIC_FILM_SESSION,
				TransferSyntax.ImplicitVRLittleEndian
			));

			// Basic Film Box
			presentationContexts.add(new PresentationContext(
				pcId++,
				BASIC_FILM_BOX,
				TransferSyntax.ImplicitVRLittleEndian
			));

			// Basic Grayscale Image Box
			presentationContexts.add(new PresentationContext(
				pcId++,
				BASIC_GRAYSCALE_IMAGE_BOX,
				TransferSyntax.ImplicitVRLittleEndian
			));

			// Printer SOP Class (for status)
			presentationContexts.add(new PresentationContext(
				pcId++,
				PRINTER_SOP_CLASS,
				TransferSyntax.ImplicitVRLittleEndian
			));

			// Create association
			Association association = AssociationFactory.createNewAssociation(
				printerHost,
				printerPort,
				printerAETitle,
				localAETitle,
				presentationContexts,
				null, false, null, 0, 0
			);

			if (association != null) {
				System.out.println("Association established.\n");

				// Demo: Show what a print workflow would look like
				demonstratePrintWorkflow();

				association.release();
				System.out.println("\nAssociation released.");
			} else {
				System.out.println("Failed to connect to printer.");
				demonstratePrintWorkflow();
			}

		} catch (Exception e) {
			System.out.println("Note: DICOM Print requires a print server (printer or software).");
			System.out.println("Error: " + e.getMessage());
			demonstratePrintWorkflow();
		}
	}

	/**
	 * Demonstrate the print workflow and attributes
	 */
	private static void demonstratePrintWorkflow() {

		System.out.println("\n=== DICOM Print Workflow ===\n");

		System.out.println("Step 1: N-CREATE Film Session");
		System.out.println("-----------------------------------");
		AttributeList sessionAttrs = buildFilmSessionAttributes();
		System.out.println("Film Session attributes:");
		System.out.println("  Number of Copies: 1");
		System.out.println("  Print Priority: HIGH");
		System.out.println("  Medium Type: BLUE FILM");
		System.out.println("  Film Destination: MAGAZINE");

		System.out.println("\nStep 2: N-CREATE Film Box");
		System.out.println("-----------------------------------");
		System.out.println("Film Box attributes:");
		System.out.println("  Image Display Format: STANDARD\\1,1 (1x1 images)");
		System.out.println("  Film Orientation: PORTRAIT");
		System.out.println("  Film Size ID: 14INX17IN");
		System.out.println("  Magnification Type: REPLICATE");
		System.out.println("  Border Density: BLACK");
		System.out.println("  Empty Image Density: BLACK");

		System.out.println("\nStep 3: N-SET Image Box (for each image)");
		System.out.println("-----------------------------------");
		System.out.println("Image Box attributes:");
		System.out.println("  Image Position: 1");
		System.out.println("  Polarity: NORMAL");
		System.out.println("  Requested Image Size: (computed)");
		System.out.println("  + Pixel Data from source image");

		System.out.println("\nStep 4: N-ACTION on Film Box (Print)");
		System.out.println("-----------------------------------");
		System.out.println("Action Type ID: 1 (Print)");
		System.out.println("This triggers actual printing.");

		System.out.println("\nStep 5: N-DELETE Film Session (Cleanup)");
		System.out.println("-----------------------------------");
		System.out.println("Releases printer resources.");

		System.out.println("\n=== Film Layout Options ===");
		System.out.println("Image Display Format examples:");
		System.out.println("  STANDARD\\1,1  - 1 image per film");
		System.out.println("  STANDARD\\2,2  - 4 images (2x2 grid)");
		System.out.println("  STANDARD\\3,3  - 9 images (3x3 grid)");
		System.out.println("  STANDARD\\4,4  - 16 images (4x4 grid)");
		System.out.println("  STANDARD\\2,3  - 6 images (2 cols x 3 rows)");
		System.out.println("  ROW\\1,2,3     - 1+2+3 images in rows");

		System.out.println("\n=== Film Size Options ===");
		System.out.println("  8INX10IN  - 8\" x 10\"");
		System.out.println("  10INX12IN - 10\" x 12\"");
		System.out.println("  10INX14IN - 10\" x 14\"");
		System.out.println("  11INX14IN - 11\" x 14\"");
		System.out.println("  14INX14IN - 14\" x 14\"");
		System.out.println("  14INX17IN - 14\" x 17\"");
		System.out.println("  24CMX30CM - 24cm x 30cm");
		System.out.println("  A3, A4    - Standard paper sizes");

		System.out.println("\n=== Medium Types ===");
		System.out.println("  PAPER      - Paper print");
		System.out.println("  CLEAR FILM - Clear/transparent film");
		System.out.println("  BLUE FILM  - Blue-tinted film");
		System.out.println("  MAMMO CLEAR FILM - Mammography film");
		System.out.println("  MAMMO BLUE FILM  - Mammography blue film");
	}

	/**
	 * Build Film Session attributes
	 */
	private static AttributeList buildFilmSessionAttributes() {

		AttributeList list = new AttributeList();

		try {
			// Number of copies
			Attribute copies = new IntegerStringAttribute(TagFromName.NumberOfCopies);
			copies.addValue("1");
			list.put(copies);

			// Print priority
			Attribute priority = new CodeStringAttribute(TagFromName.PrintPriority);
			priority.addValue("HIGH");
			list.put(priority);

			// Medium type
			Attribute medium = new CodeStringAttribute(TagFromName.MediumType);
			medium.addValue("BLUE FILM");
			list.put(medium);

			// Film destination
			Attribute destination = new CodeStringAttribute(TagFromName.FilmDestination);
			destination.addValue("MAGAZINE");
			list.put(destination);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}
