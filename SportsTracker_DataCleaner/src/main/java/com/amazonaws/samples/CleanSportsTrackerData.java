package com.amazonaws.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.KeyPair;

public class CleanSportsTrackerData {

	static KeyPair keyPair; 
	static AmazonEC2 ec2;
	static final String IMAGE_ID = "ami-0947d2ba12ee1ff75"; //replace 
	private static File uploadFile;
	
	public CleanSportsTrackerData() throws IOException {
		this.uploadFile = createSampleFile();
	}
	
	
	//TODO: replace this method with yours
	private static File createSampleFile() throws IOException {
        File file = File.createTempFile("My-aws-java-sdk-proseminar", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("This is a test file for the\n");
        writer.write("Proseminar in DCI\n");
        writer.close();

        return file;
    }



	public static File getUploadFile() {
		return uploadFile;
	}

	public static AmazonEC2 getEC2Instance() {
		return ec2;
	}
	
	
}
