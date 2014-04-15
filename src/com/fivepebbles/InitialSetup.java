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

/**
 * 
 * @author Brainerd Dharmaraj 
 * 
 * 
 * The initial setup of AWS Credentials and files/folders to be backed up in S3
 * This program will create/update the following file locally:
 * 
 *    awskeys.json - holds the AWS Access ID and Secret Key
 *    s3files.xml  - holds files/folders against different bucket names to be uploaded to S3
 *    
 */

public class InitialSetup {
	
	public static void main(String[] args) {
		
		  //Set credentials
		  S3Credentials a1 = new S3Credentials();
		  a1.setCredentials("AWS Access ID goes here", "AWS Secret Key goes here");
				
		  //Set S3 bucket name (index 0) and Files/Folders to backup
		  
		  //Enter the files/folders that needed to be backed up in S3 with their bucket names
		  //Bucket Names are always in index 0 of the inner array
		  String [] [] mult1 = new String[3][4];
		     				
		  mult1 [0] [0] = "brainerddharmaraj.bucket1";
		  mult1 [0] [1] = "/home/";  //if you need to backup the entire "home" directory, this is the only entry needed
		  			
		  mult1 [1] [0] = "brainerddharmaraj.bucket2";
		  mult1 [1] [1] = "/media/Windows7_OS/Documents/github.PDF";
		  mult1 [1] [2] = "/media/Windows7_OS/Documents/personal/resume.docx";
		  mult1 [1] [3] = "/media/Windows7_OS/pictures";
		  				  
		  mult1 [2] [0] = "brainerddharmaraj.bucket3";
		  mult1 [2] [1] = "/media/Windows7_OS/videos";
		
				  
		  ProcessFiles p2 = new ProcessFiles();
		  p2.setFiles(mult1);

		}

}
