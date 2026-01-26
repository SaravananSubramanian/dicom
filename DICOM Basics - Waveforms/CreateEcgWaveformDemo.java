package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DecimalStringAttribute;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UIDGenerator;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.dicom.UnsignedLongAttribute;
import com.pixelmed.dicom.UnsignedShortAttribute;

/**
 * DICOM ECG Waveform Creation Demo
 *
 * DICOM supports storing waveform data such as ECG, EEG, and other
 * physiological signals. This enables integrating waveform data with
 * imaging in PACS.
 *
 * Waveform SOP Classes:
 * - 12-Lead ECG (1.2.840.10008.5.1.4.1.1.9.1.1)
 * - General ECG (1.2.840.10008.5.1.4.1.1.9.1.2)
 * - Ambulatory ECG (1.2.840.10008.5.1.4.1.1.9.1.3)
 * - Hemodynamic Waveform (1.2.840.10008.5.1.4.1.1.9.2.1)
 * - Basic Cardiac EP (1.2.840.10008.5.1.4.1.1.9.3.1)
 * - Arterial Pulse Waveform (1.2.840.10008.5.1.4.1.1.9.5.1)
 * - Respiratory Waveform (1.2.840.10008.5.1.4.1.1.9.6.1)
 * - General Audio Waveform (1.2.840.10008.5.1.4.1.1.9.4.1)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreateEcgWaveformDemo {

	// Missing DICOM tags
	private static final AttributeTag WaveformSampleInterpretation = new AttributeTag(0x5400, 0x1006);
	private static final AttributeTag ChannelBaseline = new AttributeTag(0x5400, 0x0114);

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM ECG Waveform Creation Demo ===\n");

			String outputFile = "C:\\temp\\ecg_waveform.dcm";

			// Generate sample ECG data (simulated)
			short[] ecgData = generateSampleEcgData(5000, 500.0); // 10 seconds at 500 Hz

			// Create the waveform DICOM object
			AttributeList list = create12LeadEcg(
				ecgData,
				500.0,  // Sampling frequency (Hz)
				"Doe^John",
				"PAT123",
				"19700101",
				"M"
			);

			// Add file meta information
			FileMetaInformation.addFileMetaInformation(
				list,
				TransferSyntax.ExplicitVRLittleEndian,
				"ECG_SOURCE"
			);

			// Write the file
			list.write(outputFile);

			System.out.println("ECG Waveform created: " + outputFile);
			System.out.println("Duration: 10 seconds");
			System.out.println("Sampling Rate: 500 Hz");
			System.out.println("Channels: 12 leads");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			demonstrateWaveformConcepts();
		}
	}

	/**
	 * Create a 12-Lead ECG DICOM object
	 */
	private static AttributeList create12LeadEcg(
			short[] ecgData,
			double samplingFrequency,
			String patientName,
			String patientId,
			String patientBirthDate,
			String patientSex) throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === SOP Common Module ===
		Attribute sopClass = new UniqueIdentifierAttribute(TagFromName.SOPClassUID);
		sopClass.addValue(SOPClass.TwelveLeadECGStorage);
		list.put(sopClass);

		Attribute sopInstance = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
		sopInstance.addValue(new UIDGenerator().getNewUID());
		list.put(sopInstance);

		// === Patient Module ===
		Attribute patNameAttr = new PersonNameAttribute(TagFromName.PatientName);
		patNameAttr.addValue(patientName);
		list.put(patNameAttr);

		Attribute patIdAttr = new LongStringAttribute(TagFromName.PatientID);
		patIdAttr.addValue(patientId);
		list.put(patIdAttr);

		Attribute patBirthDateAttr = new DateAttribute(TagFromName.PatientBirthDate);
		patBirthDateAttr.addValue(patientBirthDate);
		list.put(patBirthDateAttr);

		Attribute patSexAttr = new CodeStringAttribute(TagFromName.PatientSex);
		patSexAttr.addValue(patientSex);
		list.put(patSexAttr);

		// === General Study Module ===
		Attribute studyUID = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
		studyUID.addValue(new UIDGenerator().getNewUID());
		list.put(studyUID);

		Attribute studyDate = new DateAttribute(TagFromName.StudyDate);
		studyDate.addValue(currentDate);
		list.put(studyDate);

		Attribute studyTime = new TimeAttribute(TagFromName.StudyTime);
		studyTime.addValue(currentTime);
		list.put(studyTime);

		Attribute accessionNumber = new ShortStringAttribute(TagFromName.AccessionNumber);
		accessionNumber.addValue("ECG123");
		list.put(accessionNumber);

		Attribute studyId = new ShortStringAttribute(TagFromName.StudyID);
		studyId.addValue("ECG001");
		list.put(studyId);

		// === General Series Module ===
		Attribute modality = new CodeStringAttribute(TagFromName.Modality);
		modality.addValue("ECG");
		list.put(modality);

		Attribute seriesUID = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
		seriesUID.addValue(new UIDGenerator().getNewUID());
		list.put(seriesUID);

		Attribute seriesNumber = new IntegerStringAttribute(TagFromName.SeriesNumber);
		seriesNumber.addValue("1");
		list.put(seriesNumber);

		// === Waveform Module ===
		Attribute instanceNumber = new IntegerStringAttribute(TagFromName.InstanceNumber);
		instanceNumber.addValue("1");
		list.put(instanceNumber);

		Attribute contentDate = new DateAttribute(TagFromName.ContentDate);
		contentDate.addValue(currentDate);
		list.put(contentDate);

		Attribute contentTime = new TimeAttribute(TagFromName.ContentTime);
		contentTime.addValue(currentTime);
		list.put(contentTime);

		Attribute acquisitionDateTime = new com.pixelmed.dicom.DateTimeAttribute(TagFromName.AcquisitionDateTime);
		acquisitionDateTime.addValue(currentDate + currentTime);
		list.put(acquisitionDateTime);

		// === Waveform Sequence ===
		SequenceAttribute waveformSeq = new SequenceAttribute(TagFromName.WaveformSequence);
		AttributeList waveformItem = new AttributeList();

		// Number of channels (12-lead ECG)
		Attribute numChannels = new UnsignedShortAttribute(TagFromName.NumberOfWaveformChannels);
		numChannels.addValue(12);
		waveformItem.put(numChannels);

		// Number of samples per channel
		int samplesPerChannel = ecgData.length / 12;
		Attribute numSamples = new UnsignedLongAttribute(TagFromName.NumberOfWaveformSamples);
		numSamples.addValue(samplesPerChannel);
		waveformItem.put(numSamples);

		// Sampling frequency
		Attribute sampFreq = new DecimalStringAttribute(TagFromName.SamplingFrequency);
		sampFreq.addValue(String.valueOf(samplingFrequency));
		waveformItem.put(sampFreq);

		// Waveform bits allocated
		Attribute bitsAlloc = new UnsignedShortAttribute(TagFromName.WaveformBitsAllocated);
		bitsAlloc.addValue(16);
		waveformItem.put(bitsAlloc);

		// Waveform sample interpretation
		Attribute sampleInterp = new CodeStringAttribute(WaveformSampleInterpretation);
		sampleInterp.addValue("SS"); // Signed Short
		waveformItem.put(sampleInterp);

		// === Channel Definition Sequence ===
		SequenceAttribute channelDefSeq = new SequenceAttribute(TagFromName.ChannelDefinitionSequence);

		// Standard 12-lead names
		String[] leadNames = {"I", "II", "III", "aVR", "aVL", "aVF", "V1", "V2", "V3", "V4", "V5", "V6"};

		for (int i = 0; i < 12; i++) {
			AttributeList channelItem = new AttributeList();

			// Channel Source Sequence (identifies the lead)
			SequenceAttribute channelSourceSeq = new SequenceAttribute(TagFromName.ChannelSourceSequence);
			AttributeList sourceItem = new AttributeList();

			Attribute codeValue = new ShortStringAttribute(TagFromName.CodeValue);
			codeValue.addValue("5.6.3-9-" + (i + 1)); // MDC codes for ECG leads
			sourceItem.put(codeValue);

			Attribute codingScheme = new ShortStringAttribute(TagFromName.CodingSchemeDesignator);
			codingScheme.addValue("MDC");
			sourceItem.put(codingScheme);

			Attribute codeMeaning = new LongStringAttribute(TagFromName.CodeMeaning);
			codeMeaning.addValue("Lead " + leadNames[i]);
			sourceItem.put(codeMeaning);

			channelSourceSeq.addItem(sourceItem);
			channelItem.put(channelSourceSeq);

			// Channel sensitivity
			Attribute sensitivity = new DecimalStringAttribute(TagFromName.ChannelSensitivity);
			sensitivity.addValue("1.0");
			channelItem.put(sensitivity);

			// Channel sensitivity units
			SequenceAttribute sensUnitsSeq = new SequenceAttribute(TagFromName.ChannelSensitivityUnitsSequence);
			AttributeList unitsItem = new AttributeList();
			unitsItem.put(createCodeAttribute(TagFromName.CodeValue, "uV"));
			unitsItem.put(createCodeAttribute(TagFromName.CodingSchemeDesignator, "UCUM"));
			unitsItem.put(createCodeAttribute(TagFromName.CodeMeaning, "microvolt"));
			sensUnitsSeq.addItem(unitsItem);
			channelItem.put(sensUnitsSeq);

			// Channel baseline
			Attribute baseline = new IntegerStringAttribute(ChannelBaseline);
			baseline.addValue("0");
			channelItem.put(baseline);

			// Bits stored
			Attribute bitsStored = new UnsignedShortAttribute(TagFromName.WaveformBitsStored);
			bitsStored.addValue(16);
			channelItem.put(bitsStored);

			channelDefSeq.addItem(channelItem);
		}

		waveformItem.put(channelDefSeq);

		// === Waveform Data ===
		OtherWordAttribute waveformData = new OtherWordAttribute(TagFromName.WaveformData);
		waveformData.setValues(ecgData);
		waveformItem.put(waveformData);

		waveformSeq.addItem(waveformItem);
		list.put(waveformSeq);

		return list;
	}

	/**
	 * Generate sample ECG-like data (simulated sine wave)
	 */
	private static short[] generateSampleEcgData(int samplesPerChannel, double samplingFrequency) {
		int numChannels = 12;
		short[] data = new short[samplesPerChannel * numChannels];

		for (int ch = 0; ch < numChannels; ch++) {
			double phaseOffset = ch * Math.PI / 6; // Different phase for each lead
			double amplitude = 500 + ch * 50; // Different amplitude

			for (int s = 0; s < samplesPerChannel; s++) {
				double t = s / samplingFrequency;
				// Simulate ECG with heart rate ~72 bpm (1.2 Hz)
				double value = amplitude * Math.sin(2 * Math.PI * 1.2 * t + phaseOffset);

				// Add QRS-like spike
				double qrsPhase = (t * 1.2) % 1.0;
				if (qrsPhase > 0.4 && qrsPhase < 0.5) {
					value += amplitude * 2 * Math.exp(-Math.pow((qrsPhase - 0.45) * 50, 2));
				}

				data[s * numChannels + ch] = (short) value;
			}
		}

		return data;
	}

	private static Attribute createCodeAttribute(com.pixelmed.dicom.AttributeTag tag, String value) throws Exception {
		Attribute attr = new ShortStringAttribute(tag);
		attr.addValue(value);
		return attr;
	}

	/**
	 * Demonstrate waveform concepts
	 */
	private static void demonstrateWaveformConcepts() {

		System.out.println("\n=== DICOM Waveform Concepts ===\n");

		System.out.println("--- Waveform IOD Structure ---");
		System.out.println("Waveform Sequence (5400,0100)");
		System.out.println("  +-- Number of Channels (003A,0005)");
		System.out.println("  +-- Number of Samples (003A,0010)");
		System.out.println("  +-- Sampling Frequency (003A,001A)");
		System.out.println("  +-- Channel Definition Sequence (003A,0200)");
		System.out.println("  |     +-- Channel Source");
		System.out.println("  |     +-- Channel Sensitivity");
		System.out.println("  |     +-- Channel Sensitivity Units");
		System.out.println("  +-- Waveform Data (5400,1010)");

		System.out.println("\n--- Waveform SOP Classes ---");
		System.out.println("12-Lead ECG: 1.2.840.10008.5.1.4.1.1.9.1.1");
		System.out.println("General ECG: 1.2.840.10008.5.1.4.1.1.9.1.2");
		System.out.println("Ambulatory ECG: 1.2.840.10008.5.1.4.1.1.9.1.3");
		System.out.println("Hemodynamic: 1.2.840.10008.5.1.4.1.1.9.2.1");
		System.out.println("Cardiac EP: 1.2.840.10008.5.1.4.1.1.9.3.1");
		System.out.println("Audio: 1.2.840.10008.5.1.4.1.1.9.4.1");

		System.out.println("\n--- 12-Lead ECG Leads ---");
		System.out.println("Limb leads: I, II, III");
		System.out.println("Augmented leads: aVR, aVL, aVF");
		System.out.println("Precordial leads: V1, V2, V3, V4, V5, V6");
	}
}
