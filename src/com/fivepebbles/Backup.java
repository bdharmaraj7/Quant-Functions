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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author Brainerd Dharmaraj 
 *
 */

public class Backup {
	private String bucketnm;
	private String bfile;
	private ArrayList<String> filelist;
	private ArrayList<String> totalfilelist = new ArrayList<String>();
	private ArrayList<String> newfilelist = new ArrayList<String>();
	private ArrayList<String> newfilelist2 = new ArrayList<String>();
	private ArrayList<String> keylist = new ArrayList<String>();
			
	
	public void processBackup() {
	
		//Retrieve Bucket Names and related files/folders from XML saved locally
		
		Document myDocument = null;
				
		try {
			URL myURL = new File("target", "s3files.xml").toURI().toURL();
			SAXReader myReader = new SAXReader();
			myDocument = myReader.read(myURL);
		} 
		catch (MalformedURLException | DocumentException e) {
			//***TODO*** log Msg
			e.printStackTrace();
		}
		
		
		Element root = myDocument.getRootElement();
								
		for (Iterator<Element> i1 = root.elementIterator(); i1.hasNext(); ) {
            Element bucketelem = i1.next();
            
            if (bucketelem.getName() == "bucket") {
            	bucketnm = bucketelem.attributeValue("name");
            	
            	for (Iterator<Element> i2 = bucketelem.elementIterator(); i2.hasNext();) {
            		Element fileelem = i2.next();
            		            		
            		if (fileelem.getName() == "file") {
            			bfile = fileelem.getText();
            			            			
            			//Get list of files (bfile could be a folder name)
            			ProcessFiles p1 = new ProcessFiles();
            			filelist = p1.getFiles(bfile);
            			
            			//Append files to arraylist
            			if (filelist != null) {
            				totalfilelist.addAll(filelist);
            			}
            		}
            	}
            
            	
            	//Make the data good for S3
        		//Replace "\" with "/" for Windows
        		        						
        		for (int j = 0; j < totalfilelist.size(); j++) {
        								    
        				if (totalfilelist.get(j).contains("\\")) {
        					newfilelist.add(totalfilelist.get(j).replace("\\", "/"));
        				}
        				else {
        					newfilelist.add(totalfilelist.get(j));
        				}
        		}
                    		       		
        		
        		//Remove Driveletter from files/object list if present (Windows)
        		for (int k=0; k <newfilelist.size(); k++) {
        				if (newfilelist.get(k).contains("C:")) {
        				   newfilelist2.add(newfilelist.get(k).replace("C:", ""));
        		        }
        				else {
        					newfilelist2.add(newfilelist.get(k));
        				}
        		}
        	        		        		
        		
        		//Get S3 key list corresponding to the files 
        		//This is obtained by removing the "/" in index 0 from the file list since AWS key should not have a / at position 0
        		        				
        		for (int m=0; m <newfilelist2.size(); m++) {
        			 keylist.add(newfilelist2.get(m).substring(1));
        		}
        	        		        		
        		        		
                //Backup files in S3 (for this bucket)
        		
        		//Get AWS Credentials
        		String [] awskeys = new S3Credentials().getCredentials();
        		        		
        		//Set AWS credentials
        		ProcessAWS pr1 = new ProcessAWS(awskeys[0], awskeys[1]);
        		
        		//Check if Bucket exists in S3
        		if (pr1.checkBucket(bucketnm)) {
        			
        			//Put Objects in S3
        			//keylist contains S3 keys and newfilelist2 contains the files
        			for (int l=0; l < newfilelist2.size(); l++) {
        				boolean r1 = pr1.putAWSObject(keylist.get(l), new File(newfilelist2.get(l)), bucketnm);
        				
        				if (!r1) {
        					//***TODO*** Log message
        				}
        			}
        		}
        		else {
        			//Create Bucket in S3
        			boolean r2 = pr1.createBucket(bucketnm);
        			
        			if (r2) {
        				//Put Objects in S3
        				//keylist contains S3 keys and newfilelist2 contains the files
        				for (int m=0; m < newfilelist2.size(); m++) {
        					boolean r3 = pr1.putAWSObject(keylist.get(m), new File(newfilelist2.get(m)), bucketnm);
        					
        					if (!r3) {
        						//***TODO*** Log message
        					}
        				}
        								
        			}
        			else {
        				//***TODO*** Log message
        			}
        		}
        	        	
            }
            
            
            //Clear arrays for the next bucket
            totalfilelist.clear();
            newfilelist.clear();
            newfilelist2.clear();
            keylist.clear();
                     
		}  
		        
	}	
}		      
		

