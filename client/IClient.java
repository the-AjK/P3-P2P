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
import common.DeviceServer;

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
	|	public void connectMEClient_answer(DeviceServer _server, boolean _youareconnected)
	|	description: utilizzato dai server per confermare la richiesta di connessione del client
	\****************************************************************************************/
	public void connectMEClient_answer(DeviceServer _server, boolean _youareconnected) throws RemoteException;
	
	/****************************************************************************************\
	|	public void disconnectMEClient_answer(DeviceServer _server, boolean _youaredisconnected)
	|	description: utilizzato dai server per confermare la richiesta di disconnessione del client
	\****************************************************************************************/
	public void disconnectMEClient_answer(DeviceServer _server, boolean _youaredisconnected) throws RemoteException;
	
	/****************************************************************************************\
	|	public void findResourceForClient_answer(Resource _risorsa, Vector<DeviceClient> _clientList)
	|	description: utilizzato dal proprio server per rispondere ad una richiesta di ricerca
	\****************************************************************************************/
	public void findResourceForClient_answer(Resource _risorsa, Vector<DeviceClient> _clientList) throws RemoteException;
	
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
