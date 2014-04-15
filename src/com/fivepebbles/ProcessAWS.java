/**
 * This file is part of AWSS3Backup which is licensed under the MIT License.
 * 
 * The MIT License (MIT)

 * Copyright (c) [2013] [Brainerd Dharmaraj]

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fivepebbles;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * 
 * @author Brainerd Dharmaraj
 * 
 * All the S3 processes
 *
 */


public class ProcessAWS {
		private AmazonS3 s3client;
		private	AWSCredentials myCredentials;
		
		ProcessAWS(String keyId, String secKey) {
			this.myCredentials = new BasicAWSCredentials(keyId,secKey);
			this.s3client = new AmazonS3Client(myCredentials);
		}
		
		//Check if Bucket exists in AWS
		public boolean checkBucket(String bucketName) {
			boolean b1 = false;
			
			try {
				 b1 = s3client.doesBucketExist(bucketName);
			}
			catch (AmazonServiceException ase) {
				//***TODO*** Log message
			}
			
			catch (AmazonClientException ace) {
				//***TODO*** Log message
			}
			return b1;
		}
				
		
		//Create Bucket in AWS
			public boolean createBucket(String bucketName) {
				boolean b3 = true;
				
				try {
					 Bucket bck3 = s3client.createBucket(bucketName);
				}
				catch (AmazonServiceException ase) {
					//***TODO*** Log message
					b3 = false;
				}
				
				catch (AmazonClientException ace) {
					//***TODO*** Log message
					b3 = false;
				}
				return b3;
			}
		
			
		//Put objects in AWS 
		public boolean putAWSObject(String myKey, File myFile, String bucketName) {
			boolean b2 = true;
		
			try {
				PutObjectRequest objreq    = new PutObjectRequest(bucketName,myKey,myFile);
				PutObjectResult  objresult = s3client.putObject(objreq);
			}
			
			catch (AmazonServiceException ase) {
				//***TODO*** Log message
				b2 = false;
			}
			
			catch (AmazonClientException ace) {
				//***TODO*** Log message
				b2 = false;
			}
			return b2;
		}	
			

}
