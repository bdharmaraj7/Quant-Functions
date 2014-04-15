/**
 *
 * @author Brainerd Dharmaraj
 *
 */

package com.fivepebbles;

import java.io.FileReader;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;

public class BDClient {
	
	private static String groupID;
	private static String password;
	private static String userID;
	private static String agentID;
	private static String agentGroupID;
	private static String reportFormat;
	private static int version = 0;
	private static boolean detailedReport = false;
			
	private String url = null;
	private String xml = null;
	private String valueID = null;
			
	//Constructor Overload since iSeries sends parameters as byte arrays
	public BDClient(byte[] endpoint, byte[] inputXML, byte[] valuationID){
		
	        this.url  = new String(endpoint);
		this.xml  = new String(inputXML);
		this.valueID = new String(valuationID);
					
		//Get Properties file
                String path 	= getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		String propFile = path + "bin/propfile.properties";
			    
	    	//Load SSL properties 	
	    	try {
	    	    Properties props = new Properties();
	    	    FileInputStream propin = new FileInputStream(propFile); 
	    	    props.load(propin);
							
		    System.setProperty("javax.net.ssl.trustStore", props.getProperty("javax.net.ssl.trustStore"));
		    System.setProperty("javax.net.ssl.trustStorePassword", props.getProperty("javax.net.ssl.trustStorePassword"));
		    System.setProperty("javax.net.ssl.trustStoreType", props.getProperty("javax.net.ssl.trustStoreType"));
		    System.setProperty("com.ibm.ssl.performURLHostNameVerification", "true"); 
			
		    //If additional parameters required in the webservice client request
                    groupID  	  = props.getProperty("GROUPID");
		    password  	  = props.getProperty("PASSWORD");
		    userID  	  = props.getProperty("USERID");
		    reportFormat  = props.getProperty("REPORTFORMAT");
			
		    propin.close();
	    	}
		
		catch(IOException ex){
		     //logger.severe("ERROR Msg: IOException - Unable to load Properties file: " + ex.toString());
		}
	    
		catch(Exception ex2){
		     //logger.severe("ERROR Msg: Unable to set Properties: " + ex2.toString());
		}
	
	}
	
	
	//Invoke remote method through a proxy 
	public byte[] getReport(){
		String resultXML = null;
		
		try { 
			BDProxy myBDProxy = new BDProxy(url);
			
			BDResponseData response = myBDProxy.calculateBD(groupID, password, userID, xml, reportFormat);
			resultXML = response.processResponse();
					
		}
		
		catch(Exception e1) {
			resultXML = "ISOERROR: " + e1.toString();
		}
				
		//Convert the return parameter to a byte array for iSeries
		return resultXML.getBytes();		
	}
	
}
