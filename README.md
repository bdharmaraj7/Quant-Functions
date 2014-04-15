JAX-WS-Client
=============

A secure JAX-WS client for the IBM System i/iSeries to connect to a web service through a proxy. 

Using the WSDL, create standard client stubs.

Add BDClient.java as a wrapper around the calling iSeries program (RPG or CL). It can be instantiated and called from the iSeries. It will perform necessary data conversions both ways - received from the iSeries and returned to the iSeries. 
BDClient.java retrieves SSL parameters from a Java Properties file.

The file BDProxy.java will make the call to the remote service. This file will be part of the created stubs. It is included here just to show the additional code customizations that need to be done. Parameters required for accessing the web service are stored in a configuration file. 


