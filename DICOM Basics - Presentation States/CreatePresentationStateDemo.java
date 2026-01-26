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
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.dicom.UIDGenerator;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.dicom.UnsignedShortAttribute;

/**
 * DICOM Grayscale Softcopy Presentation State (GSPS) Creation Demo
 *
 * Presentation States allow storing display settings separate from the image:
 * - Window width/level (brightness/contrast)
 * - Zoom and pan
 * - Rotation and flip
 * - Annotations (text, graphics)
 * - Display shutters
 * - Displayed area selection
 *
 * Types of Presentation States:
 * - Grayscale Softcopy PS (1.2.840.10008.5.1.4.1.1.11.1)
 * - Color Softcopy PS (1.2.840.10008.5.1.4.1.1.11.2)
 * - Pseudo-Color Softcopy PS (1.2.840.10008.5.1.4.1.1.11.3)
 * - Blending Softcopy PS (1.2.840.10008.5.1.4.1.1.11.4)
 * - XA/XRF Grayscale Softcopy PS (1.2.840.10008.5.1.4.1.1.11.5)
 * - Advanced Blending PS (1.2.840.10008.5.1.4.1.1.11.8)
 *
 * For more information: http://www.saravanansubramanian.com
 */
public class CreatePresentationStateDemo {

	// Missing DICOM tags
	private static final AttributeTag SoftcopyVOILUTSequence = new AttributeTag(0x0028, 0x3110);
	private static final AttributeTag GraphicLayer = new AttributeTag(0x0070, 0x0002);
	private static final AttributeTag TextObjectSequence = new AttributeTag(0x0070, 0x0008);
	private static final AttributeTag BoundingBoxTopLeftHandCorner = new AttributeTag(0x0070, 0x0010);
	private static final AttributeTag BoundingBoxBottomRightHandCorner = new AttributeTag(0x0070, 0x0011);
	private static final AttributeTag UnformattedTextValue = new AttributeTag(0x0070, 0x0006);
	private static final AttributeTag AnchorPoint = new AttributeTag(0x0070, 0x0014);
	private static final AttributeTag AnchorPointAnnotationUnits = new AttributeTag(0x0070, 0x0004);
	private static final AttributeTag GraphicLayerSequence = new AttributeTag(0x0070, 0x0060);
	private static final AttributeTag GraphicLayerOrder = new AttributeTag(0x0070, 0x0062);
	private static final AttributeTag GraphicLayerDescription = new AttributeTag(0x0070, 0x0068);

	public static void main(String[] args) {

		try {

			System.out.println("=== DICOM Presentation State Creation Demo ===\n");

			// The image this presentation state references
			String referencedStudyUID = "1.2.3.4.5.6.7.8.9";
			String referencedSeriesUID = "1.2.3.4.5.6.7.8.9.1";
			String referencedSOPInstanceUID = "1.2.3.4.5.6.7.8.9.1.1";
			String referencedSOPClassUID = SOPClass.CTImageStorage;

			// Output file
			String outputFile = "C:\\temp\\presentation_state.dcm";

			// Create the presentation state
			AttributeList psList = createGrayscalePresentationState(
				referencedStudyUID,
				referencedSeriesUID,
				referencedSOPInstanceUID,
				referencedSOPClassUID
			);

			// Add file meta information
			FileMetaInformation.addFileMetaInformation(
				psList,
				TransferSyntax.ExplicitVRLittleEndian,
				"OurAET"
			);

			// Write to file
			psList.write(outputFile);

			System.out.println("Presentation State created: " + outputFile);
			System.out.println("\nThis PS references image: " + referencedSOPInstanceUID);
			System.out.println("Window Width: 400");
			System.out.println("Window Center: 40");
			System.out.println("Annotation: Sample text overlay");

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Create a Grayscale Softcopy Presentation State
	 */
	private static AttributeList createGrayscalePresentationState(
			String studyUID, String seriesUID, String sopInstanceUID, String sopClassUID)
			throws Exception {

		AttributeList list = new AttributeList();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String currentDate = dateFormat.format(now);
		String currentTime = timeFormat.format(now);

		// === SOP Common Module ===
		Attribute sopClass = new UniqueIdentifierAttribute(TagFromName.SOPClassUID);
		sopClass.addValue(SOPClass.GrayscaleSoftcopyPresentationStateStorage);
		list.put(sopClass);

		Attribute sopInstance = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
		sopInstance.addValue(new UIDGenerator().getNewUID());
		list.put(sopInstance);

		// === Patient Module (copied from referenced image) ===
		list.put(createPersonName(TagFromName.PatientName, "Doe^John"));
		list.put(createString(TagFromName.PatientID, LongStringAttribute.class, "PAT123"));
		list.put(createDate(TagFromName.PatientBirthDate, "19700101"));
		list.put(createCode(TagFromName.PatientSex, "M"));

		// === General Study Module ===
		list.put(createUID(TagFromName.StudyInstanceUID, studyUID));
		list.put(createDate(TagFromName.StudyDate, currentDate));
		list.put(createTime(TagFromName.StudyTime, currentTime));
		list.put(createString(TagFromName.AccessionNumber, ShortStringAttribute.class, "ACC123"));
		list.put(createPersonName(TagFromName.ReferringPhysicianName, "Smith^Jane^Dr"));
		list.put(createString(TagFromName.StudyID, ShortStringAttribute.class, "STUDY001"));

		// === Presentation State Module ===
		list.put(createCode(TagFromName.Modality, "PR")); // Presentation State

		Attribute psSeriesUID = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
		psSeriesUID.addValue(new UIDGenerator().getNewUID());
		list.put(psSeriesUID);

		list.put(createInt(TagFromName.SeriesNumber, 100));
		list.put(createInt(TagFromName.InstanceNumber, 1));

		// Content Label - user-friendly name
		list.put(createCode(TagFromName.ContentLabel, "CT_LUNG_WINDOW"));

		// Content Description
		list.put(createString(TagFromName.ContentDescription, LongStringAttribute.class,
			"Lung window preset for CT chest"));

		// Presentation Creation Date/Time
		list.put(createDate(TagFromName.PresentationCreationDate, currentDate));
		list.put(createTime(TagFromName.PresentationCreationTime, currentTime));

		// Content Creator's Name
		list.put(createPersonName(TagFromName.ContentCreatorName, "Tech^John"));

		// === Referenced Series Sequence ===
		SequenceAttribute refSeriesSeq = new SequenceAttribute(TagFromName.ReferencedSeriesSequence);
		AttributeList refSeriesItem = new AttributeList();

		refSeriesItem.put(createUID(TagFromName.SeriesInstanceUID, seriesUID));

		// Referenced Image Sequence
		SequenceAttribute refImageSeq = new SequenceAttribute(TagFromName.ReferencedImageSequence);
		AttributeList refImageItem = new AttributeList();

		refImageItem.put(createUID(TagFromName.ReferencedSOPClassUID, sopClassUID));
		refImageItem.put(createUID(TagFromName.ReferencedSOPInstanceUID, sopInstanceUID));

		refImageSeq.addItem(refImageItem);
		refSeriesItem.put(refImageSeq);
		refSeriesSeq.addItem(refSeriesItem);
		list.put(refSeriesSeq);

		// === Softcopy VOI LUT Module (Window Width/Level) ===
		SequenceAttribute voiLutSeq = new SequenceAttribute(SoftcopyVOILUTSequence);
		AttributeList voiLutItem = new AttributeList();

		// Reference the image this applies to
		SequenceAttribute refImageSeq2 = new SequenceAttribute(TagFromName.ReferencedImageSequence);
		AttributeList refImageItem2 = new AttributeList();
		refImageItem2.put(createUID(TagFromName.ReferencedSOPClassUID, sopClassUID));
		refImageItem2.put(createUID(TagFromName.ReferencedSOPInstanceUID, sopInstanceUID));
		refImageSeq2.addItem(refImageItem2);
		voiLutItem.put(refImageSeq2);

		// Window Center and Width for lung window
		Attribute windowCenter = new DecimalStringAttribute(TagFromName.WindowCenter);
		windowCenter.addValue("-600"); // Lung window center
		voiLutItem.put(windowCenter);

		Attribute windowWidth = new DecimalStringAttribute(TagFromName.WindowWidth);
		windowWidth.addValue("1500"); // Lung window width
		voiLutItem.put(windowWidth);

		// VOI LUT Function
		Attribute voiFunc = new CodeStringAttribute(TagFromName.VOILUTFunction);
		voiFunc.addValue("LINEAR");
		voiLutItem.put(voiFunc);

		voiLutSeq.addItem(voiLutItem);
		list.put(voiLutSeq);

		// === Graphic Annotation Module (Text Overlay) ===
		SequenceAttribute graphicAnnotationSeq = new SequenceAttribute(TagFromName.GraphicAnnotationSequence);
		AttributeList annotationItem = new AttributeList();

		// Reference the image
		SequenceAttribute refImageSeq3 = new SequenceAttribute(TagFromName.ReferencedImageSequence);
		AttributeList refImageItem3 = new AttributeList();
		refImageItem3.put(createUID(TagFromName.ReferencedSOPClassUID, sopClassUID));
		refImageItem3.put(createUID(TagFromName.ReferencedSOPInstanceUID, sopInstanceUID));
		refImageSeq3.addItem(refImageItem3);
		annotationItem.put(refImageSeq3);

		// Graphic Layer
		annotationItem.put(createCode(GraphicLayer, "LAYER1"));

		// Text Object Sequence
		SequenceAttribute textObjectSeq = new SequenceAttribute(TextObjectSequence);
		AttributeList textItem = new AttributeList();

		// Bounding box for text
		Attribute boundingBoxTLHC = new DecimalStringAttribute(BoundingBoxTopLeftHandCorner);
		boundingBoxTLHC.addValue("10");
		boundingBoxTLHC.addValue("10");
		textItem.put(boundingBoxTLHC);

		Attribute boundingBoxBRHC = new DecimalStringAttribute(BoundingBoxBottomRightHandCorner);
		boundingBoxBRHC.addValue("200");
		boundingBoxBRHC.addValue("50");
		textItem.put(boundingBoxBRHC);

		// The text to display
		Attribute unformattedText = new com.pixelmed.dicom.ShortTextAttribute(UnformattedTextValue);
		unformattedText.addValue("Lung Window Preset");
		textItem.put(unformattedText);

		// Anchor point
		Attribute anchorPoint = new DecimalStringAttribute(AnchorPoint);
		anchorPoint.addValue("10");
		anchorPoint.addValue("10");
		textItem.put(anchorPoint);

		textItem.put(createCode(AnchorPointAnnotationUnits, "PIXEL"));

		textObjectSeq.addItem(textItem);
		annotationItem.put(textObjectSeq);

		graphicAnnotationSeq.addItem(annotationItem);
		list.put(graphicAnnotationSeq);

		// === Graphic Layer Module ===
		SequenceAttribute graphicLayerSeq = new SequenceAttribute(GraphicLayerSequence);
		AttributeList layerItem = new AttributeList();

		layerItem.put(createCode(GraphicLayer, "LAYER1"));
		layerItem.put(createInt(GraphicLayerOrder, 1));
		layerItem.put(createString(GraphicLayerDescription, LongStringAttribute.class,
			"Annotation layer"));

		graphicLayerSeq.addItem(layerItem);
		list.put(graphicLayerSeq);

		return list;
	}

	// Helper methods for attribute creation
	private static Attribute createUID(com.pixelmed.dicom.AttributeTag tag, String value) throws Exception {
		Attribute attr = new UniqueIdentifierAttribute(tag);
		attr.addValue(value);
		return attr;
	}

	private static Attribute createPersonName(com.pixelmed.dicom.AttributeTag tag, String value) throws Exception {
		Attribute attr = new PersonNameAttribute(tag);
		attr.addValue(value);
		return attr;
	}

	private static Attribute createString(com.pixelmed.dicom.AttributeTag tag, Class<?> clazz, String value) throws Exception {
		Attribute attr = (Attribute) clazz.getConstructor(com.pixelmed.dicom.AttributeTag.class).newInstance(tag);
		attr.addValue(value);
		return attr;
	}

	private static Attribute createCode(com.pixelmed.dicom.AttributeTag tag, String value) throws Exception {
		Attribute attr = new CodeStringAttribute(tag);
		attr.addValue(value);
		return attr;
	}

	private static Attribute createDate(com.pixelmed.dicom.AttributeTag tag, String value) throws Exception {
		Attribute attr = new DateAttribute(tag);
		attr.addValue(value);
		return attr;
	}

	private static Attribute createTime(com.pixelmed.dicom.AttributeTag tag, String value) throws Exception {
		Attribute attr = new TimeAttribute(tag);
		attr.addValue(value);
		return attr;
	}

	private static Attribute createInt(com.pixelmed.dicom.AttributeTag tag, int value) throws Exception {
		Attribute attr = new IntegerStringAttribute(tag);
		attr.addValue(value);
		return attr;
	}
}
