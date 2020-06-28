package com.saravanansubramanian.dicom.pixelmedtutorial;

import java.util.LinkedList;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

public class AssociationsInDicomRejectionDemo {
	
	public static void main(String[] args) {
		
		try {
            LinkedList<PresentationContext> prestnContexts = new LinkedList<PresentationContext>();
			LinkedList<String> transferSyntaxList = new LinkedList<String>();
			transferSyntaxList.add(TransferSyntax.JPEG2000Lossless);
			
			byte prentnContextIdOfVerfSopClass = 1;//use any number here for tracking
			String verificationSopClass = SOPClass.Verification; //this is the UID for the Verification SOP class
			
			//Print it to console so you can see its UID
			System.out.println("The UID of the SOP class that we are using as Abstract Syntax is " + verificationSopClass);
			
			//Make a list of presentation contexts consisting of the Abstract syntax and the list of transfer syntaxes
			//We will ask the Called AE to see whether it supports these
			prestnContexts.add(new PresentationContext(prentnContextIdOfVerfSopClass, verificationSopClass,transferSyntaxList));
			
			System.out.println("Attempting association establishment with remote peer...");
			
			//Attempt to create the association to David Clunie's public DICOM server
			Association association = AssociationFactory.createNewAssociation("www.dicomserver.co.uk", //their hostname or IP address
																				11112, //the port their entity is listening on
																				"MedConnServer", //their Application Entity Title
																				"OurJavaClient", //our Application Entity Title
																				prestnContexts,
																				null,
																				false,
																				2);
			
			//Pass an unsupported/meaningless transfer syntax for this SOP class and see what happens. This should throw an DICOM network exception
			try {
				byte supportedContextId = association.getSuitablePresentationContextID(verificationSopClass,TransferSyntax.JPEG2000Lossless);
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("The transfer syntax JPEG2000Lossless UID of " + TransferSyntax.JPEG2000Lossless + " is not supported");
			}
        
        } catch (Exception e) {
        	//In real-life, do something about these exceptions
			e.printStackTrace();
		}
	}

}
