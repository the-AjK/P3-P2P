/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: IServer.java
|	Package: server
|	Version: 0.1 - creazione scheletro interfaccia
|			 0.2 - 
|
\****************************************************************************************/
package server;

import java.rmi.*;
//import java.util.*; //vector
//import client.IClient;
//import client.Client;

public interface IServer extends Remote
{
	public void connectMEServer(String _serverName) throws RemoteException;

	
}