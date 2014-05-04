/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: IClient.java
|	Package: client
|	Version: 0.1 - creazione scheletro interfaccia
|			 0.2 - 
|
\****************************************************************************************/
package client;

import java.rmi.*;
import java.util.Vector; 
import common.Resource;

public interface IClient extends Remote
{ 
	//campi dati
	public static final String HEARTBEAT_ANSWER = "JK-CLIENT-OK";
	
	/****************************************************************************************\
	|	public String heartbeat()
	|	description: heartbeat di controllo
	\****************************************************************************************/
	public String heartbeat() throws RemoteException;
	
	/****************************************************************************************\
	|	public Vector<Resource> getResourceList()
	|	description: utilizzato dai server per richiedere la lista risorse
	\****************************************************************************************/
	public Vector<Resource> getResourceList() throws RemoteException;
	
	/****************************************************************************************\
	|	public boolean download(Resource _risorsa, int _parte, String _client) 
	|	description: utilizzato dai client per richiedere una parte di risorsa
	\****************************************************************************************/
	public boolean download(Resource _risorsa, int _parte, String _client) throws RemoteException;
	
} //IClient
