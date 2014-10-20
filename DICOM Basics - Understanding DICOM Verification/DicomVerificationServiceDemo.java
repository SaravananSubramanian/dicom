package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.network.VerificationSOPClassSCU;

public class DicomVerificationServiceDemo {
	
	public static void main(String[] args) {
		try {
			//Demonstration of code to verify connectivity against David Clunie's public server hosted on the Amazon cloud
			
			String remoteEntityHostName = "184.73.255.26"; //their hostname or IP address
			int remoteEntityPort = 11112; //the port their entity is listening on
			String calledAETitle = "AWSPIXELMEDPUB"; //their Application Entity Title
			String callingAETitle = "LOCAL"; //our Application Entity Title
			boolean secureTransport = false; //optional - we wont need to use it for our example 
			int debugLevel = 2; // zero for no debugging messages, higher values for more verbose messages
			
			//Call the constructor for this class passing the various arguments whose values are listed above
		    new VerificationSOPClassSCU(remoteEntityHostName,remoteEntityPort, calledAETitle, callingAETitle, secureTransport, debugLevel);
		}
		catch (Exception e) {
		    e.printStackTrace(); //in real life, do something about this exception
		}
	}

}
