package com.saravanansubramanian.dicom.pixelmedtutorial;

import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.network.MultipleInstanceTransferStatusHandler;
import com.pixelmed.network.StorageSOPClassSCU;

public class DicomCStoreFunctionalityDemo {
	
	public static void main(String arg[]) {
		
		try {
			
			
			String dicomFile1 = "C:\\SaravananWebsiteWork\\javatutorialsproject\\sample_images\\MR-MONO2-16-head";
			String dicomFile2 = "C:\\SaravananWebsiteWork\\javatutorialsproject\\sample_images\\Saravanan.dcm";
			String dicomFile3 = "C:\\SaravananWebsiteWork\\javatutorialsproject\\sample_images\\CT-MONO2-16-brain";
			String dicomFile4 = "C:\\SaravananWebsiteWork\\javatutorialsproject\\sample_images\\CT-MONO2-16-ankle";
			
			SetOfDicomFiles dicomFilesToTransmit = new SetOfDicomFiles();
			dicomFilesToTransmit.add(dicomFile1);
			dicomFilesToTransmit.add(dicomFile2);
			dicomFilesToTransmit.add(dicomFile3);
			dicomFilesToTransmit.add(dicomFile4);
			
			System.out.println("Initiating C-STORE operation...");
			
			
			//note, behind the scenes this operation transmits the files one by one even it appears to be 'batched'
			//there is also constructor overload that takes just one file at a time. See PixelMed documentation
			
			new StorageSOPClassSCU("localhost", 
					4242, 
					"Orthanc",
					"JavaTestClient",
					dicomFilesToTransmit, 
					0, //compression level
					new OurTransferUpdateStatusHandler());
			
			System.out.println("C-STORE operation completed successfully...");
			
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
	}
	
}

class OurTransferUpdateStatusHandler extends MultipleInstanceTransferStatusHandler {

	@Override
	public void updateStatus(int nRemaining,
            				 int nCompleted,
            				 int nFailed,
            				 int nWarning,
            				 java.lang.String sopInstanceUID) {
		
		System.out.println("SOP Instance ID being transferred:" + sopInstanceUID);
		System.out.println("Items in Queue:" + nRemaining);
		System.out.println("Transfers Completed:" + nCompleted);
		System.out.println("Failed Transmits:" + nFailed);
		System.out.println("Warnings:" + nWarning);
		
	}
	
}
