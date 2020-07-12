package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

public class DicomCFindFunctionalityWithCustomIdentifierHandlerDemo {
	
	public static void main(String arg[]) {
		
		try {
			// use the default character set for VR encoding - override this as necessary
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[])null);
			AttributeList identifier = new AttributeList();
			
			//build the attributes that you would like to retrieve as well as passing in any search criteria
			identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY"); //specific query root
			identifier.putNewAttribute(TagFromName.PatientName,specificCharacterSet).addValue("Bowen*");
			identifier.putNewAttribute(TagFromName.PatientID,specificCharacterSet);
			identifier.putNewAttribute(TagFromName.PatientBirthDate);
			identifier.putNewAttribute(TagFromName.PatientSex);
			identifier.putNewAttribute(TagFromName.StudyInstanceUID);
			identifier.putNewAttribute(TagFromName.SOPInstanceUID);
			identifier.putNewAttribute(TagFromName.StudyDescription);
			identifier.putNewAttribute(TagFromName.StudyDate);
			
			//retrieve all studies belonging to patient with name 'Bowen'
			new FindSOPClassSCU("www.dicomserver.co.uk",
					104,
					"MEDCONNEC",
					"OURCLIENT",
					SOPClass.StudyRootQueryRetrieveInformationModelFind,identifier,
					new OurCustomFindIdentifierHandler());
			
		}
		catch (Exception e) {
			e.printStackTrace(System.err); // in real life, do something about this exception
			System.exit(0);
		}
	}

}

class OurCustomFindIdentifierHandler extends IdentifierHandler {
	
	//add additional constructors here as necessary to pass more information handling
	
	@Override
	public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {
		System.out.println("Matched result:" + attributeListForFindResult);
		
		String studyInstanceUID = attributeListForFindResult.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
		System.out.println("studyInstanceUID:" + studyInstanceUID);
		
		//do other things you need to do with the matched results
	}
	
}


