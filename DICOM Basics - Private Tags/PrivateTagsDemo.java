package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;

/**
 * DICOM Private Tags Demo
 *
 * Private tags allow vendors to store proprietary data in DICOM objects.
 * They use odd group numbers (0009, 0011, 0019, etc.) to avoid conflicts
 * with standard DICOM tags.
 *
 * Private Tag Structure:
 * - Private Creator Identification (gggg,00xx) - Identifies the vendor
 * - Private Data Elements (gggg,xxyy) - The actual data
 *
 * Where:
 * - gggg = odd group number (0009, 0011, 0019, etc.)
 * - xx = block number (10-FF)
 * - yy = element number within the block (00-FF)
 *
 * Example:
 * - (0019,0010) "SIEMENS MR HEADER" - Creator ID for block 10
 * - (0019,100F) - Private data element in block 10
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class PrivateTagsDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Private Tags Demo ===\n");

			// Demo 1: Reading private tags
			System.out.println("--- Demo 1: Understanding Private Tag Structure ---");
			demonstratePrivateTagStructure();

			// Demo 2: Creating private tags
			System.out.println("\n--- Demo 2: Creating Private Tags ---");
			demonstrateCreatingPrivateTags();

			// Demo 3: Common vendor private tags
			System.out.println("\n--- Demo 3: Common Vendor Private Tags ---");
			demonstrateVendorPrivateTags();

			// Demo 4: Best practices
			System.out.println("\n--- Demo 4: Best Practices ---");
			demonstrateBestPractices();

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Demonstrate private tag structure
	 */
	private static void demonstratePrivateTagStructure() {

		System.out.println("Private Tag Anatomy:");
		System.out.println();
		System.out.println("Standard tag format: (gggg,eeee)");
		System.out.println("  gggg = group number (even for standard, odd for private)");
		System.out.println("  eeee = element number");
		System.out.println();

		System.out.println("Private tag blocks:");
		System.out.println();
		System.out.println("  Group 0009, Block 10:");
		System.out.println("    (0009,0010) = Private Creator Identification");
		System.out.println("    (0009,1000) through (0009,10FF) = Private data elements");
		System.out.println();
		System.out.println("  Group 0009, Block 11:");
		System.out.println("    (0009,0011) = Private Creator Identification");
		System.out.println("    (0009,1100) through (0009,11FF) = Private data elements");
		System.out.println();

		System.out.println("Example from a GE scanner:");
		System.out.println("  (0009,0010) LO \"GEMS_IDEN_01\"         <- Creator ID for block 10");
		System.out.println("  (0009,1001) LO \"CT01\"                  <- Product ID");
		System.out.println("  (0009,1002) SH \"CT Lightspeed\"         <- Scanner model");
		System.out.println("  (0009,1027) SL 123456                   <- Internal code");
		System.out.println();

		System.out.println("Example from a Siemens scanner:");
		System.out.println("  (0019,0010) LO \"SIEMENS MR HEADER\"    <- Creator ID");
		System.out.println("  (0019,100C) IS 1                        <- Gradient mode");
		System.out.println("  (0019,100F) DS 2.3                      <- Flow compensation");
		System.out.println();
	}

	/**
	 * Demonstrate creating private tags
	 */
	private static void demonstrateCreatingPrivateTags() throws Exception {

		AttributeList list = new AttributeList();

		// Add standard attributes first
		Attribute patientName = new PersonNameAttribute(TagFromName.PatientName);
		patientName.addValue("Doe^John");
		list.put(patientName);

		// === Step 1: Reserve a private block ===
		// Use group 0009, block 10 (element 0010)
		AttributeTag creatorTag = new AttributeTag(0x0009, 0x0010);
		Attribute creatorAttr = new LongStringAttribute(creatorTag);
		creatorAttr.addValue("MY_APPLICATION");  // Your application identifier
		list.put(creatorAttr);

		System.out.println("Step 1: Reserve private block");
		System.out.println("  Tag: (0009,0010)");
		System.out.println("  Creator: MY_APPLICATION");
		System.out.println();

		// === Step 2: Add private data elements ===

		// Private element (0009,1000) - block 10, element 00
		AttributeTag privateTag1 = new AttributeTag(0x0009, 0x1000);
		Attribute privateAttr1 = new LongStringAttribute(privateTag1);
		privateAttr1.addValue("Custom Value 1");
		list.put(privateAttr1);

		// Private element (0009,1001) - block 10, element 01
		AttributeTag privateTag2 = new AttributeTag(0x0009, 0x1001);
		Attribute privateAttr2 = new LongStringAttribute(privateTag2);
		privateAttr2.addValue("Custom Value 2");
		list.put(privateAttr2);

		// Private element (0009,10FF) - block 10, element FF (last in block)
		AttributeTag privateTag3 = new AttributeTag(0x0009, 0x10FF);
		Attribute privateAttr3 = new LongStringAttribute(privateTag3);
		privateAttr3.addValue("Last element in block");
		list.put(privateAttr3);

		System.out.println("Step 2: Add private data elements");
		System.out.println("  (0009,1000) = \"Custom Value 1\"");
		System.out.println("  (0009,1001) = \"Custom Value 2\"");
		System.out.println("  (0009,10FF) = \"Last element in block\"");
		System.out.println();

		// === Step 3: Use another block if needed ===
		AttributeTag creator2Tag = new AttributeTag(0x0009, 0x0011);
		Attribute creator2Attr = new LongStringAttribute(creator2Tag);
		creator2Attr.addValue("MY_APP_EXTENDED");
		list.put(creator2Attr);

		AttributeTag privateTag4 = new AttributeTag(0x0009, 0x1100);
		Attribute privateAttr4 = new LongStringAttribute(privateTag4);
		privateAttr4.addValue("Extended data");
		list.put(privateAttr4);

		System.out.println("Step 3: Use additional block if needed");
		System.out.println("  (0009,0011) Creator: MY_APP_EXTENDED");
		System.out.println("  (0009,1100) = \"Extended data\"");
		System.out.println();

		// Show the result
		System.out.println("Resulting private attributes in AttributeList:");
		System.out.println("  " + list.get(creatorTag));
		System.out.println("  " + list.get(privateTag1));
		System.out.println("  " + list.get(privateTag2));
	}

	/**
	 * Demonstrate common vendor private tags
	 */
	private static void demonstrateVendorPrivateTags() {

		System.out.println("=== Common Vendor Private Tags ===\n");

		System.out.println("GE Healthcare:");
		System.out.println("  Creator: GEMS_IDEN_01, GEMS_ACQU_01, GEMS_IMAG_01");
		System.out.println("  Groups: 0009, 0019, 0021, 0023, 0025, 0027, 0043, 0045");
		System.out.println("  Contains: Scanner parameters, reconstruction settings");
		System.out.println();

		System.out.println("Siemens Healthineers:");
		System.out.println("  Creator: SIEMENS MR HEADER, SIEMENS CT VA0, etc.");
		System.out.println("  Groups: 0019, 0021, 0029, 0051, 7FE1");
		System.out.println("  Contains: Sequence parameters, CSA headers (MR)");
		System.out.println();

		System.out.println("Philips Healthcare:");
		System.out.println("  Creator: PHILIPS MR, Philips Imaging DD 001");
		System.out.println("  Groups: 2001, 2005, 7053");
		System.out.println("  Contains: Scanning parameters, Private Pixel Data");
		System.out.println();

		System.out.println("Canon (formerly Toshiba):");
		System.out.println("  Creator: TOSHIBA_MEC_CT_01, TOSHIBA_MEC_MR_01");
		System.out.println("  Groups: 7005, 700D");
		System.out.println("  Contains: Acquisition parameters");
		System.out.println();

		System.out.println("Useful Private Tag Databases:");
		System.out.println("  - DICOM Innolitics: https://dicom.innolitics.com/");
		System.out.println("  - dicom.offis.de/dcmtk private tags");
		System.out.println("  - Grassroots DICOM wiki");
		System.out.println();
	}

	/**
	 * Best practices for private tags
	 */
	private static void demonstrateBestPractices() {

		System.out.println("=== Private Tag Best Practices ===\n");

		System.out.println("DO:");
		System.out.println("  1. Always register a Private Creator Identification");
		System.out.println("  2. Use unique, identifiable creator names");
		System.out.println("  3. Document your private tag definitions");
		System.out.println("  4. Use appropriate VRs for your data");
		System.out.println("  5. Consider using standard attributes first");
		System.out.println("  6. Check if a standard tag already exists for your data");
		System.out.println();

		System.out.println("DON'T:");
		System.out.println("  1. Use private tags without a creator identification");
		System.out.println("  2. Assume private tags will survive transfer");
		System.out.println("  3. Store PHI in private tags without proper handling");
		System.out.println("  4. Use even group numbers for private data");
		System.out.println("  5. Conflict with known vendor private tags");
		System.out.println();

		System.out.println("Reading Private Tags:");
		System.out.println("  // Check for creator");
		System.out.println("  Attribute creator = list.get(new AttributeTag(0x0009, 0x0010));");
		System.out.println("  if (creator != null && creator.getSingleStringValueOrNull().equals(\"MY_APP\")) {");
		System.out.println("      // Safe to read our private data");
		System.out.println("      Attribute data = list.get(new AttributeTag(0x0009, 0x1000));");
		System.out.println("  }");
		System.out.println();

		System.out.println("Anonymization Consideration:");
		System.out.println("  Private tags may contain PHI!");
		System.out.println("  Options:");
		System.out.println("    1. Remove all private tags (list.removePrivateAttributes())");
		System.out.println("    2. Selectively remove known PHI-containing tags");
		System.out.println("    3. Document private tag contents in your de-identification policy");
		System.out.println();

		System.out.println("Interoperability:");
		System.out.println("  - Private tags may be stripped during transfer");
		System.out.println("  - Some PACS/archives don't preserve private tags");
		System.out.println("  - Use standard tags when data is clinically important");
		System.out.println("  - Private tags are best for supplementary/vendor-specific data");
	}
}
