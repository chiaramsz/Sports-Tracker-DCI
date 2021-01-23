package com.amazonaws.samples;

import com.amazonaws.services.s3.model.DeleteObjectRequest;

import java.io.*;
import java.net.Socket;

import static com.amazonaws.samples.BucketAndEC2Connection.bucketName;

public class ServerThread implements Runnable{


    private Socket clientSocket;

    public ServerThread(Socket clientSocket){
        this.clientSocket = clientSocket;
    }


    public void run() {


        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.print(getUniqueTrackerId());


            String locations = in.readLine();

            File file = createFile(locations);

            uploadFile(file);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String getUniqueTrackerId() {
        //Todo kevin, generated TrackerID
        return null;
    }

    private File createFile(String locations) throws IOException {
        //Todo Kevin

        File file = File.createTempFile("My-aws-java-sdk-proseminar", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("This is a test file for the\n");
        writer.write("Proseminar in DCI\n");
        writer.close();

        return file;
    }
    private void uploadFile (File file){
        /***************** Upload cleaned data to bucket ****************/
        // https://docs.aws.amazon.com/AmazonS3/latest/dev/llJavaUploadFile.html

        String key = file.getName();
        BucketAndEC2Connection.s3.putObject(bucketName, key, file);
    }

    private void deleteFile (String key){


        /***************** delete file from bucket afterwards ****************/ // delete this method when in
        // production

        BucketAndEC2Connection.s3.deleteObject(new DeleteObjectRequest(bucketName, key));
        System.out.println("Deleted file from bucket");

    }
}
