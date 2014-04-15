/**
 * This file is part of AWSS3Backup which is licensed under the MIT License.
 * 
 * The MIT License (MIT)

 * Copyright (c) [2014] [Brainerd Dharmaraj]

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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author Brainerd Dharmaraj 
 *
 */

public class BackupRunnable implements Runnable {
	private String bucket;
	private CopyOnWriteArrayList<String> files;
	private CopyOnWriteArrayList<String> keys;
				
	BackupRunnable(String bucketnm, CopyOnWriteArrayList<String>filelist, CopyOnWriteArrayList<String>keylist) {
		this.bucket = bucketnm;
		
		this.files = new CopyOnWriteArrayList<String>();
		this.files.addAll(filelist);
		
		this.keys = new CopyOnWriteArrayList<String>();
		this.keys.addAll(keylist);
	}
	
				
	public void run(){
		uploadFiles();
	}
	
	
	public void uploadFiles() {
						
		//Get AWS Credentials
		String [] awskeys = new S3Credentials().getCredentials();
		        		
		//Set credentials in AWS
		ProcessAWS pr1 = new ProcessAWS(awskeys[0], awskeys[1]);
					
		
		//*** Backup files in S3 (for this bucket)  ***
		
		//Check if Bucket exists in S3
		if (pr1.checkBucket(bucket)) {
						
			for (int l=0; l < files.size(); l++) {
				boolean r1 = pr1.putAWSObject(keys.get(l), new File(files.get(l)), bucket);
							
				if (!r1) {
					//***TODO*** Log error in putting objects
				}
			}
			
		}
		else {
			//Create Bucket in S3
			boolean r2 = pr1.createBucket(bucket);
			
			if (r2) {
								
				for (int m=0; m < files.size(); m++) {
					boolean r3 = pr1.putAWSObject(keys.get(m), new File(files.get(m)), bucket);
					
					if (!r3) {
						//***TODO*** Log error in putting objects
					}
				}
												
			}
			else {
				//***TODO*** Log error in creating bucket
			}
		}
		
	}   	
		
		
}	
	
	
