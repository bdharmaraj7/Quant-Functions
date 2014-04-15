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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
	private List<String> a1;
	private List<String> k1;
	private List<String> a2;
	private List<String> k2;
	private ArrayList<String> filelist;
	private ArrayList<String> totalfilelist = new ArrayList<String>();
	private ArrayList<String> newfilelist = new ArrayList<String>();
	private ArrayList<String> newfilelist2 = new ArrayList<String>();
	private ArrayList<String> keylist = new ArrayList<String>();
	private CopyOnWriteArrayList<String> c1 = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> c2 = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> c3 = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> c4 = new CopyOnWriteArrayList<String>();
				
	
	public void processBackup() {
	
		//Retrieve Bucket Names and related files/folders from XML saved locally
		
		Document myDocument = null;
				
		try {
			URL myURL = new File("s3files.xml").toURI().toURL();
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
        	            		      		
        		
        		int a = newfilelist2.size();
        		
        		//If number of files > 100, Divide files into groups of 100 and submit upload job in multiple threads
        		//Each thread will process 100 files
        		//A final thread will process the remaining (remainder) files
        		if (a > 100) {
        		
        			int b = 100;
        	
        			int quotient = a/b;
        			int remainder = a%b;
        		        		        	
        			int m = 0;
        		
        			if (quotient != 0) {
        				for (int j=0; j < quotient; j++) {
        					//List<String> a1 = new ArrayList<String>();
        					//List<String> k1 = new ArrayList<String>();
        					a1 = newfilelist2.subList(m, m+100);
        					k1 = keylist.subList(m, m+100);
        				        					
        					if (a1 != null) c1.addAll(a1);
        					if (k1 != null) c2.addAll(k1);
        		
        					Runnable run1 = new BackupRunnable(bucketnm, c1, c2);
        					new Thread(run1).start();
        					
        					c1.clear();
        					c2.clear();
        					        					        				
        					m += 100;
        				}
        			}
        		        		
        			if (remainder != 0) {
        				//Submit one last job for the remaining files
        				a2 = newfilelist2.subList(m, newfilelist2.size()-1);
        				k2 = keylist.subList(m, newfilelist2.size()-1);
        			
        				//Add the last element in the list
        				a2.add(newfilelist2.get(newfilelist2.size()-1));
        				k2.add(keylist.get(keylist.size()-1));
        				        				    			
        				if (a2 != null) c3.addAll(a2);
        				if (k2 != null) c4.addAll(k2);
    			
        				Runnable run1 = new BackupRunnable(bucketnm, c3, c4);
        				new Thread(run1).start();
        			        				
        			}
        		}
        		
        		else {
        			//Backup in a single thread
        			            		
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
            					//***TODO*** Log error in putting objects
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
                        
            //Clear arrays for the next bucket
            totalfilelist.clear();
            newfilelist.clear();
            newfilelist2.clear();
            keylist.clear();
                     
		}  
		        
	}	

	
}		      
		

