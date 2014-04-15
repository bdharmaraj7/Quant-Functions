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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * @author Brainerd Dharmaraj
 *
 */

public class ProcessFiles implements S3FileList {
	private ArrayList<String> filelist = new ArrayList<String>();
	
		
	//Get list of files for a given local filepath 
	@Override
	public ArrayList<String> getFiles(String filePath) {
			
		DirectoryStream<Path> stream = null;
		DirectoryStream<Path> innerstream = null;
									
		Path dir = FileSystems.getDefault().getPath(filePath); 
				
									
		try {
						
			if (Files.isDirectory(dir)) {
				stream = Files.newDirectoryStream(dir);
												
				for (Path path : stream) {
								
					if (Files.isDirectory(path)) {
						innerstream = Files.newDirectoryStream(path);
											
						for (Path innerpath : innerstream) {
							if (Files.isDirectory(innerpath)) {
								String pathName = innerpath.toAbsolutePath().toString();
								getFiles(pathName);             
							}
							else {
								filelist.add(innerpath.toString());
							}
						}
										
					}
					else {
						filelist.add(path.toString());
					}
				}
			
			}
			else {
				filelist.add(dir.toString());
			}	
			
		} 
			
		catch (IOException e) {
			e.printStackTrace();
		}
				
		return filelist;
	}
	
	
	//Save list of files and folders that need to be backed up
	@Override
	public void setFiles (String [][] valuesin) {
		
		//Create a XML file to hold AWS bucket names and related files/folders
		Document mydoc = DocumentHelper.createDocument();
		Element root = mydoc.addElement("s3backup");
		Element bucket1 = null;
		
		for (int a1=0; a1<valuesin.length; a1++) {
			
			for (int b1=0; b1<valuesin[a1].length; b1++) {
				//Bucket names are in index 0
				if (b1 == 0) {
					bucket1 = root.addElement("bucket").addAttribute("name", valuesin[a1][b1]);
				}
				else {
					if (bucket1 != null & valuesin[a1][b1] != null) {
						bucket1.addElement("file").addText(valuesin[a1][b1]); 
					}
				}
			}
			bucket1 = null;
		}
		
		//Save files and folders in file system
		FileWriter fWriter2;
		try {
			fWriter2 = new FileWriter(new File("target", "s3files.xml"));
			mydoc.write(fWriter2);
			fWriter2.flush();
			fWriter2.close();
		} 
		catch (IOException e) {
			//***TODO*** log message
			e.printStackTrace();
		}
	}

		
}
