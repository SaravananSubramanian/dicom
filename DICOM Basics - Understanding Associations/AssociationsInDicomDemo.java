package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.util.LinkedList;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

public class AssociationsInDicomDemo {
	
	public static void main(String[] args) {
		
		try {
            LinkedList<PresentationContext> prestnContexts = new LinkedList<PresentationContext>();
			LinkedList<String> transferSyntaxList = new LinkedList<String>();
			transferSyntaxList.add(TransferSyntax.Default);
			transferSyntaxList.add(TransferSyntax.ExplicitVRBigEndian);
			
			byte prentnContextOfVerfSopClass = 1;//use any number here for tracking
			String verificationSopClass = SOPClass.Verification; //this is the UID for the Verification SOP class
			
			//Print it to console so you can see its UID
			System.out.println("The UID of the SOP class that we are using as Abstract Syntax is " + verificationSopClass);
			
			//Make a list of presentation contexts consisting of the Abstract syntax and the list of transfer syntaxes
			//We will ask the Called AE to see whether it supports these
			prestnContexts.add(new PresentationContext(prentnContextOfVerfSopClass, verificationSopClass,transferSyntaxList));
			
			//Attempt to create the association to David Clunie's public DICOM server
			Association association = AssociationFactory.createNewAssociation("184.73.255.26", //their hostname or IP address
																				11112, //the port their entity is listening on
																				"AWSPIXELMEDPUB", //their Application Entity Title
																				"LOCAL", //our Application Entity Title
																				prestnContexts,
																				null,
																				false,
																				2);
			
			//Check to see if the presentation context is supported by the Called AE
			byte supportedContextId = association.getSuitablePresentationContextID(verificationSopClass);
			System.out.println("The Verification SOP class is supported");
			//Check to see what transfer syntax is preferred by the Called AE
			String transferSyntaxSupported = association.getTransferSyntaxForPresentationContextID(supportedContextId);
			//You should see Explicit VR Big-endian UID - 1.2.840.10008.1.2.2 returned here. 
			//This is because an Explicit VR transfer syntax is always be preferable over Implicit (or the "Default") transfer syntax
			System.out.println("The transfer syntax supported for this presentation context is " + transferSyntaxSupported);
			
			//Pass an unsupported/meaningless transfer syntax and see what happens. This should throw an exception
			try {
				supportedContextId = association.getSuitablePresentationContextID(verificationSopClass,TransferSyntax.JPEG2000Lossless);
			} catch (Exception e) {
				System.out.println("The transfer syntax JPEG2000Lossless UID of " + TransferSyntax.JPEG2000Lossless + " is not supported");
			}
        
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
