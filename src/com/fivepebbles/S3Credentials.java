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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

/**
 * 
 * @author Brainerd Dharmaraj
 *
 */

public class S3Credentials implements S3Backup {

		//Gets AWS Credentials saved in local JSON file
		@Override
		public String [] getCredentials() {
					
			String [] keysarr = new String[2];
				
		   	try {
					JsonReader jsReader1 = Json.createReader(new FileReader(new File("target","awskeys.json")));
					JsonObject jsRetObj = jsReader1.readObject();
					
					String keystring0  = jsRetObj.get("AWSKey1").toString();
					String keystring1  = jsRetObj.get("AWSKey2").toString();
						
					//Remove enclosing square brackets and double quotes 
					keysarr[0] = keystring0.substring(2, keystring0.length()-2);
					keysarr[1] = keystring1.substring(2, keystring1.length()-2);
		    	}
					
				catch (FileNotFoundException e3) {
					//***TODO *** Log Msg 
		    		e3.printStackTrace();
				}
		    	return keysarr;
		}	   	
					
				
		//Saves AWS Credentials in a local JSON file
		@Override
		public void setCredentials (String keyid, String seckey) {
				
			JSONObject jsObj = new JSONObject();       
			                
		    try {
				jsObj.append("AWSKey1", keyid);
				jsObj.append("AWSKey2", seckey);
		    }     
			catch (JSONException e1) {
		       	//***TODO *** log msg
				e1.printStackTrace();
			}
				                
		    //Save keys in file system
		    try {
		       	FileWriter fWriter = new FileWriter(new File("target", "awskeys.json"));
		    	fWriter.write(jsObj.toString());
		    	fWriter.flush();
		    	fWriter.close();
		    } 
		    catch (IOException e2) {
		    	//***TODO *** log msg
		    	e2.printStackTrace();
		    }
		}       			
	

}

