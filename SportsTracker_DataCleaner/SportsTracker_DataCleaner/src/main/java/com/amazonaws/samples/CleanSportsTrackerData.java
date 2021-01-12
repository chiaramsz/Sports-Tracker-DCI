package com.amazonaws.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class CleanSportsTrackerData {

	static KeyPair keyPair; 
	static AmazonEC2 ec2;
	static final String IMAGE_ID = "ami-0947d2ba12ee1ff75"; //replace 
	private static File uploadFile;
	
	static AmazonS3 s3;
	static String bucketName = "testucketsportstracker";
	
	public CleanSportsTrackerData() throws IOException {
		this.uploadFile = createSampleFile();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, IllegalStateException {
		/***************** Load the credentials ****************/
		AWSCredentialsProvider credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default");
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/home/dragi/.aws/credentials), and is in valid format.", e);
		}

		/***************** Set an AWS region ****************/		
		ec2 = AmazonEC2ClientBuilder
				.standard()
				.withCredentials(credentials)
				.withRegion(Regions.US_EAST_1)
				.build();

	
		uploadFile = createSampleFile();
		

		
		
		
		
		/***************** Create Amazon s3Client ****************/
		// https://docs.aws.amazon.com/AmazonS3/latest/dev/create-bucket-get-location-example.html
		s3 = AmazonS3ClientBuilder.standard().withCredentials(credentials).withRegion(Regions.US_EAST_1).build();

		try {
			/****************** List existing buckets ************************/
			List<Bucket> buckets = s3.listBuckets();
			for (Bucket bucket : buckets) {
				System.out.println(bucket.getName());
			}

			/***************** Check if bucket really exists ****************/
			/*
			 * HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
			 * .bucket(bucketName) .build();
			 * 
			 * try { s3.headBucket(headBucketRequest); } catch (Exception e) { throw new
			 * NoSuchBucketException(bucketName + " does not exist"); }
			 */

			/***************** Upload cleaned data to bucket ****************/
			 //https://docs.aws.amazon.com/AmazonS3/latest/dev/llJavaUploadFile.html
            File file = getUploadFile();
            String key = file.getName();
            
            // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
            // then, after each individual part has been uploaded, pass the list of ETags to 
            // the request to complete the upload.
            List<PartETag> partETags = new ArrayList<PartETag>();

            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult initResponse = s3.initiateMultipartUpload(initRequest);

            // Upload the file parts.
            long filePosition = 0;
            long contentLength = file.length();
            long partSize = 5 * 1024 * 1024; // Set part size to 5 MB. 
            
            for (int i = 1; filePosition < contentLength; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create the request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(key)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadResult = s3.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());

                filePosition += partSize;
            }
            
            // Complete the multipart upload.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, key, initResponse.getUploadId(), partETags);
            s3.completeMultipartUpload(compRequest);
            
            
            System.out.println("File should be on bucket");

			/*****************
			 * list all objects in the bucket to check that data file is inside
			 ****************/

			System.out.format("Objects in S3 bucket %s:\n", bucketName);
			ListObjectsV2Result result = s3.listObjectsV2(bucketName);
			List<S3ObjectSummary> objects = result.getObjectSummaries();
			for (S3ObjectSummary os : objects) {
				System.out.println("* " + os.getKey());
			}

			System.out.println();
			Thread.sleep(10000);

	        /*****************As part of the assignment delete file from bucket****************/
            s3.deleteObject(new DeleteObjectRequest(bucketName, key));
            System.out.println("Deleted file from bucket");

            /***************** Delete bucket****************/
            System.out.println("Deleting bucket");
    		s3.deleteBucket(new DeleteBucketRequest(bucketName));
            Thread.sleep(10000);
		
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	
	//TODO: replace this method with yours
	private static File createSampleFile() throws IOException {
        File file = File.createTempFile("cleaned-data", ".txt");
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
