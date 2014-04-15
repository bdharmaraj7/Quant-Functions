/**
 *
 * @author Brainerd Dharmaraj
 *
 */

package com.fivepebbles;

import com.ibm.wsspi.webservices.Constants;
import java.net.URL;
import java.io.*;
import com.fivepebbles.as400httpclient.*;
import com.fivepebbles.xmlparsers.sax.*;

public class BDProxy {
  private boolean _useJNDI = true;
  private String _endpoint = null;
  private URL _urlcon = null;
  private String _proxyHost = null;
  private String _proxyPort = null;
  private String _proxyUser = null;
  private String _proxyPwd = null;
    
  public BDProxy(String endpointUrl) {
	  //Set Webservice Endpoint
	  setEndpoint(endpointUrl);							
	  
	  //Create URL Object with Endpoint
	  setURL(endpointUrl);
	  
	  _initBDProxy();
	  
	  //Set Proxy Server properties
	  setProxyProp();
  }
  
  
  private void _initBDProxy() {
  	  
  if (_useJNDI) {
    try{
      javax.naming.InitialContext ctx = new javax.naming.InitialContext();
      __BD = ((com.fivepebbles.BDService)ctx.lookup("java:comp/env/service/BDService")).getBD(_urlcon);
    }
    catch (javax.naming.NamingException namingException) {}
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
    
  if (__BD != null) {
    if (_endpoint != null) {
       ((javax.xml.rpc.Stub)__BD)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
       } 
       else
       {_endpoint = (String)((javax.xml.rpc.Stub)__BD)._getProperty("javax.xml.rpc.service.endpoint.address");}
  }  
  }
  

  public void useJNDI(boolean useJNDI) {
     _useJNDI = useJNDI;
     __BD = null;
  
  }

  public void setURL(String urlString) {
	if (urlString != null) {
		try{
			_urlcon = new URL(urlString);
		}
		catch (java.net.MalformedURLException e) {}
	}
  }

  public void setProxyProp(){

	String hostname = _urlcon.getHost();
	ConfigHandler cHandler = new ConfigHandler(hostname);
	CreateParser parser = new CreateParser(cHandler);

	//Get the Configuration File and parse for Proxy Properties
	String path = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
	path = path + "bin/Config.xml";
		
	File file = new File(path);
	parser.parse(file);

	_proxyHost = cHandler.getProxyHost();
	_proxyPort = cHandler.getProxyPort();
	_proxyUser = cHandler.getUserId();
	_proxyPwd  = cHandler.getPassword();
	
	((javax.xml.rpc.Stub)__BD)._setProperty(Constants.HTTPS_PROXYHOST_PROPERTY, _proxyHost);
	((javax.xml.rpc.Stub)__BD)._setProperty(Constants.HTTPS_PROXYPORT_PROPERTY, _proxyPort);
	((javax.xml.rpc.Stub)__BD)._setProperty(Constants.HTTPS_PROXYUSER_PROPERTY, _proxyUser);
	((javax.xml.rpc.Stub)__BD)._setProperty(Constants.HTTPS_PROXYPASSWORD_PROPERTY, _proxyPwd); 
	
	//Set Connection timeout to 20 secs (20000 millisecs)
	((com.ibm.ws.webservices.engine.client.Stub)__BD).setTimeout(20000); 
  }

  public String getEndpoint() {
    return _endpoint;
  }

  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (__BD != null)
      ((javax.xml.rpc.Stub)__BD)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
  }

  public com.fivepebbles.BD getBD() {
    if (__BD == null)
    _initBDProxy();
    return __BD;
  }

  public com.fivepebbles.BDResponseData getBD(java.lang.String groupId, java.lang.String password, java.lang.String userId, java.lang.Stringxml)    throws java.rmi.RemoteException{
  if (__BD == null)
    _initBDProxy();
  return __BD.getBD(groupId, password, userId, xml);
  }


}
