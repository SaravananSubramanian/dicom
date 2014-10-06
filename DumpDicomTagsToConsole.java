package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;

public class DumpDicomTagsToConsole {
	
	private static AttributeList list = new AttributeList();

    public static void main(String[] args) {
    	String dicomFile = "/Users/saravanan/java work/workspace/PixelMedTutorial/Sample Image/IM-0001-0001.dcm";
    	try {
    		list.read(dicomFile);
    		//File Meta Information Headers
    		System.out.println("File MetaInformation Group Length:" + getTagInformation(TagFromName.FileMetaInformationGroupLength));
    		System.out.println("File MetaInformation Version:" + getTagInformation(TagFromName.FileMetaInformationVersion));
    		System.out.println("Media Storage SOP Class UID:" + getTagInformation(TagFromName.MediaStorageSOPClassUID));
    		System.out.println("Media Storage SOP Instance UID:" + getTagInformation(TagFromName.MediaStorageSOPInstanceUID));
    		System.out.println("Transfer Syntax UID:" + getTagInformation(TagFromName.TransferSyntaxUID));
    		System.out.println("Implementation Class UID:" + getTagInformation(TagFromName.ImplementationClassUID));
    		System.out.println("Implementation Version Name:" + getTagInformation(TagFromName.ImplementationVersionName));
    		System.out.println("Source Application Entity Title:" + getTagInformation(TagFromName.SourceApplicationEntityTitle));
    		System.out.println("Private Information Creator UID:" + getTagInformation(TagFromName.PrivateInformationCreatorUID));
    		System.out.println("Private Information:" + getTagInformation(TagFromName.PrivateInformation));
    		//Data Object Elements
    		System.out.println("Study Instance UID:" + getTagInformation(TagFromName.StudyInstanceUID));
    		System.out.println("Study Comments:" + getTagInformation(TagFromName.StudyComments));
    		System.out.println("Study Status ID:" + getTagInformation(TagFromName.StudyStatusID));
    		System.out.println("Study Description:" + getTagInformation(TagFromName.StudyDescription));
    		System.out.println("Series Instance UID:" + getTagInformation(TagFromName.SeriesInstanceUID));
    		System.out.println("Series Date:" + getTagInformation(TagFromName.SeriesDate));
    		System.out.println("Series Description:" + getTagInformation(TagFromName.SeriesDescription));
    		System.out.println("Image Comments:" + getTagInformation(TagFromName.ImageComments));
    		System.out.println("SOP Class UID:" + getTagInformation(TagFromName.SOPClassUID));
    		System.out.println("SOP Instance UID:" + getTagInformation(TagFromName.SOPInstanceUID));	
            
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static String getTagInformation(AttributeTag attrTag) {
		return Attribute.getDelimitedStringValuesOrEmptyString(list, attrTag);
	}

}
