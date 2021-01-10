package com.amazonaws.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.config.model.NoSuchBucketException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class BucketAndEC2Connection {

	static AmazonS3 s3;
	static String bucketName = "sportstrackerdci";
	
	static AmazonEC2 ec2 = CleanSportsTrackerData.getEC2Instance();

	public static void main(String[] args) throws IOException, InterruptedException {

		CleanSportsTrackerData cleaner = new CleanSportsTrackerData();
		
		/***************** Load the credentials ****************/
		AWSCredentialsProvider credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default");
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/home/dragi/.aws/credentials), and is in valid format.", e);
		}

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
			// https://docs.aws.amazon.com/AmazonS3/latest/dev/llJavaUploadFile.html
			File uploadFile = CleanSportsTrackerData.getUploadFile();
			String key = uploadFile.getName();

			s3.putObject(bucketName, key, uploadFile);

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

			/***************** delete file from bucket afterwards ****************/ // delete this method when in
																					// production
			s3.deleteObject(new DeleteObjectRequest(bucketName, key));
			System.out.println("Deleted file from bucket");

			System.out.println();
			Thread.sleep(10000);

			/***************** Delete bucket ****************/ // delete this method when in production
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

}
