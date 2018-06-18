package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.network.VerificationSOPClassSCU;

public class DicomVerificationServiceDemoUsingLocalOrthancServer {
	
	public static void main(String[] args) {
		try {
			//Demonstration of code to verify connectivity against local Orthanc DICOM server
			
			String remoteEntityHostName = "localhost"; //their hostname or IP address
			int remoteEntityPort = 4242; //the port their entity is listening on
			String calledAETitle = "ORTHANC"; //their Application Entity Title
			String callingAETitle = "JAVACLIENT"; //our Application Entity Title
			boolean secureTransport = false; //optional - we wont need to use it for our example 
			int debugLevel = 2; // zero for no debugging messages, higher values for more verbose messages
			
			//Call the constructor for this class passing the various arguments whose values are listed above
		    new VerificationSOPClassSCU(remoteEntityHostName,remoteEntityPort, calledAETitle, callingAETitle, secureTransport,2);
		    
		    //See documentation at: http://www.dclunie.com/pixelmed/software/javadoc/index.html
		    //for class -> VerificationSOPClassSCU
		    //Successful connection, association negotiation and C-ECHO command success status is indicated by the lack of an exception.
		}
		catch (Exception e) {
			
			//the following exceptions may be thrown in C-ECHO fails
			//java.io.IOException
			//DicomException
			//DicomNetworkException - if the connection is refused, the association reqeust is reject, or the C-ECHO command reports other than a success status
			
		    e.printStackTrace(); //in real life, do something about this exception
		}
	}

}
