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
import common.DeviceClient;

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
	|	public boolean downloadStart(final Resource _risorsa, final int _parte, final DeviceClient _client) 
	|	description: utilizzato dai client per richiedere l'avvio del download di una parte di risorsa
	\****************************************************************************************/
	public boolean downloadStart(final Resource _risorsa,final int _parte, final DeviceClient _client) throws RemoteException;
	
	/****************************************************************************************\
	|	public boolean downloadStop(final Resource _risorsa, final int _parte, final DeviceClient _client) 
	|	description: utilizzato dai client per notificare la fine dell'upload della parte di risorsa
	\****************************************************************************************/
	public boolean downloadStop(final Resource _risorsa,final int _parte, final DeviceClient _client) throws RemoteException;
	
} //IClient
