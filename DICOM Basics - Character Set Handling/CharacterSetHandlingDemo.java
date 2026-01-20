package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

/**
 * DICOM Character Set Handling Demo
 *
 * DICOM supports international character sets for patient names,
 * institution names, and other text data. Proper character set
 * handling is essential for international healthcare systems.
 *
 * Key Attribute:
 * (0008,0005) Specific Character Set
 *
 * Default Character Set:
 * - If not specified, ASCII (ISO 646) is assumed
 * - Only basic Latin alphabet supported
 *
 * Extended Character Sets:
 * - ISO 8859 series (Latin, Greek, Arabic, Hebrew, etc.)
 * - JIS X 0201/0208/0212 (Japanese)
 * - KS X 1001 (Korean)
 * - GB2312/GB18030 (Chinese Simplified)
 * - ISO 2022 (code extension techniques)
 * - Unicode (ISO IR 192 = UTF-8)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CharacterSetHandlingDemo {

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Character Set Handling Demo ===\n");

			// Demo 1: Default ASCII character set
			System.out.println("--- Demo 1: Default (ASCII) ---");
			demonstrateDefaultCharacterSet();

			// Demo 2: ISO 8859-1 (Latin-1) for Western European
			System.out.println("\n--- Demo 2: ISO 8859-1 (Latin-1) ---");
			demonstrateLatin1CharacterSet();

			// Demo 3: UTF-8 (Unicode)
			System.out.println("\n--- Demo 3: UTF-8 (Unicode) ---");
			demonstrateUtf8CharacterSet();

			// Demo 4: Japanese character set
			System.out.println("\n--- Demo 4: Japanese ---");
			demonstrateJapaneseCharacterSet();

			// Character set reference
			System.out.println("\n--- Character Set Reference ---");
			demonstrateCharacterSetReference();

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Demonstrate default (ASCII) character set
	 */
	private static void demonstrateDefaultCharacterSet() throws Exception {

		AttributeList list = new AttributeList();

		// No Specific Character Set - defaults to ASCII
		// Only basic Latin characters allowed: A-Z, a-z, 0-9, basic punctuation

		Attribute patientName = new PersonNameAttribute(TagFromName.PatientName);
		patientName.addValue("Doe^John");
		list.put(patientName);

		Attribute institution = new LongStringAttribute(TagFromName.InstitutionName);
		institution.addValue("General Hospital");
		list.put(institution);

		System.out.println("Patient Name: Doe^John");
		System.out.println("Institution: General Hospital");
		System.out.println("Specific Character Set: (not specified - ASCII default)");
		System.out.println("Supported characters: A-Z, a-z, 0-9, basic punctuation");
	}

	/**
	 * Demonstrate ISO 8859-1 (Latin-1) for Western European languages
	 */
	private static void demonstrateLatin1CharacterSet() throws Exception {

		AttributeList list = new AttributeList();

		// Set ISO 8859-1 (Latin-1) character set
		Attribute charSet = new CodeStringAttribute(TagFromName.SpecificCharacterSet);
		charSet.addValue("ISO_IR 100"); // ISO 8859-1
		list.put(charSet);

		// Create SpecificCharacterSet for proper encoding
		SpecificCharacterSet scs = new SpecificCharacterSet(new String[]{"ISO_IR 100"});

		// Now we can use accented characters
		Attribute patientName = new PersonNameAttribute(TagFromName.PatientName);
		patientName.addValue("Müller^François");
		list.put(patientName);

		Attribute institution = new LongStringAttribute(TagFromName.InstitutionName);
		institution.addValue("Hôpital Général de Zürich");
		list.put(institution);

		System.out.println("Patient Name: Müller^François");
		System.out.println("Institution: Hôpital Général de Zürich");
		System.out.println("Specific Character Set: ISO_IR 100 (ISO 8859-1, Latin-1)");
		System.out.println("Supports: Western European languages (French, German, Spanish, etc.)");
	}

	/**
	 * Demonstrate UTF-8 (Unicode) character set
	 */
	private static void demonstrateUtf8CharacterSet() throws Exception {

		AttributeList list = new AttributeList();

		// Set UTF-8 character set
		Attribute charSet = new CodeStringAttribute(TagFromName.SpecificCharacterSet);
		charSet.addValue("ISO_IR 192"); // UTF-8
		list.put(charSet);

		SpecificCharacterSet scs = new SpecificCharacterSet(new String[]{"ISO_IR 192"});

		// UTF-8 supports all Unicode characters
		System.out.println("Examples of names in different scripts:");
		System.out.println();

		System.out.println("  Chinese: 王小明 (Wang Xiaoming)");
		System.out.println("  Japanese: 山田太郎 (Yamada Taro)");
		System.out.println("  Korean: 김철수 (Kim Cheolsu)");
		System.out.println("  Arabic: محمد أحمد (Muhammad Ahmad)");
		System.out.println("  Russian: Иван Петров (Ivan Petrov)");
		System.out.println("  Greek: Νίκος Παπαδόπουλος (Nikos Papadopoulos)");
		System.out.println();
		System.out.println("Specific Character Set: ISO_IR 192 (UTF-8)");
		System.out.println("Supports: All Unicode characters - RECOMMENDED for new implementations");
	}

	/**
	 * Demonstrate Japanese character set
	 */
	private static void demonstrateJapaneseCharacterSet() throws Exception {

		AttributeList list = new AttributeList();

		// Japanese requires multiple character sets with code extensions
		Attribute charSet = new CodeStringAttribute(TagFromName.SpecificCharacterSet);
		// ISO 2022 IR 87 = JIS X 0208 (Kanji)
		// Can also include ISO 2022 IR 13 (Katakana) and IR 6 (ASCII)
		charSet.addValue("\\ISO 2022 IR 87");
		list.put(charSet);

		System.out.println("Japanese names use components:");
		System.out.println("  Alphabetic: Yamada^Taro");
		System.out.println("  Ideographic: 山田^太郎");
		System.out.println("  Phonetic: やまだ^たろう");
		System.out.println();
		System.out.println("Person Name format with components:");
		System.out.println("  Alphabetic=Ideographic=Phonetic");
		System.out.println("  Yamada^Taro=山田^太郎=やまだ^たろう");
		System.out.println();
		System.out.println("Specific Character Set: \\ISO 2022 IR 87");
		System.out.println("Note: Backslash indicates code extension for second component");
	}

	/**
	 * Character set reference
	 */
	private static void demonstrateCharacterSetReference() {

		System.out.println("\n=== Character Set Code Reference ===\n");

		System.out.println("Single-Byte Character Sets:");
		System.out.println("  (empty)      - ASCII (ISO 646, default)");
		System.out.println("  ISO_IR 100   - ISO 8859-1 (Latin-1, Western European)");
		System.out.println("  ISO_IR 101   - ISO 8859-2 (Latin-2, Central European)");
		System.out.println("  ISO_IR 109   - ISO 8859-3 (Latin-3, South European)");
		System.out.println("  ISO_IR 110   - ISO 8859-4 (Latin-4, North European)");
		System.out.println("  ISO_IR 144   - ISO 8859-5 (Cyrillic)");
		System.out.println("  ISO_IR 127   - ISO 8859-6 (Arabic)");
		System.out.println("  ISO_IR 126   - ISO 8859-7 (Greek)");
		System.out.println("  ISO_IR 138   - ISO 8859-8 (Hebrew)");
		System.out.println("  ISO_IR 148   - ISO 8859-9 (Latin-5, Turkish)");
		System.out.println("  ISO_IR 166   - TIS 620 (Thai)");
		System.out.println();

		System.out.println("Multi-Byte Character Sets:");
		System.out.println("  ISO_IR 192   - UTF-8 (Unicode) *** RECOMMENDED ***");
		System.out.println("  GB18030      - Chinese (Simplified + Traditional)");
		System.out.println("  GBK          - Chinese (Simplified)");
		System.out.println();

		System.out.println("Code Extension Character Sets (ISO 2022):");
		System.out.println("  ISO 2022 IR 6    - ASCII (default)");
		System.out.println("  ISO 2022 IR 13   - JIS X 0201 (Katakana)");
		System.out.println("  ISO 2022 IR 87   - JIS X 0208 (Japanese)");
		System.out.println("  ISO 2022 IR 159  - JIS X 0212 (Japanese supplement)");
		System.out.println("  ISO 2022 IR 149  - KS X 1001 (Korean)");
		System.out.println("  ISO 2022 IR 58   - GB 2312 (Chinese Simplified)");
		System.out.println();

		System.out.println("Person Name Components:");
		System.out.println("  Format: Alphabetic=Ideographic=Phonetic");
		System.out.println("  Each component: Family^Given^Middle^Prefix^Suffix");
		System.out.println("  Example: Smith^John=スミス^ジョン=すみす^じょん");
		System.out.println();

		System.out.println("Best Practices:");
		System.out.println("  1. Use UTF-8 (ISO_IR 192) for new implementations");
		System.out.println("  2. Always specify Specific Character Set explicitly");
		System.out.println("  3. Test with international character data");
		System.out.println("  4. Handle encoding/decoding errors gracefully");
		System.out.println("  5. Be aware of legacy system character set limitations");
	}
}
