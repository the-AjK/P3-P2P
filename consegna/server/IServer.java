/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: IServer.java
|	Package: server
|	Version: 0.1 - creazione scheletro interfaccia
|			 0.2 - aggiunti metodi remoti per la connessione dei server
|			 0.3 - aggiunti metodi remoti per la connessione dei clients 
|
\****************************************************************************************/
package server;

import java.rmi.*;
import java.util.Vector;
import client.IClient;
import common.Resource;
import common.DeviceClient;
import common.DeviceServer;

public interface IServer extends Remote
{
	//campi dati
	public static final String HEARTBEAT_ANSWER = "JK-SERVER-OK";
	
	/****************************************************************************************\
	|	public String heartbeat()
	|	description: heartbeat di controllo
	\****************************************************************************************/
	public String heartbeat() throws RemoteException;
	
	/****************************************************************************************\
	|	public void connectMEServer(final DeviceServer _server)
	|	description: utilizzato dai server per richiedere la connessione al server
	\****************************************************************************************/
	public void connectMEServer(final DeviceServer _server) throws RemoteException;
	
	/****************************************************************************************\
	|	public void connectMEServer_answer(DeviceServer _server2connect, boolean _youareconnected)
	|	description: utilizzato dal server per confermare la richiesta di connessione ai vari server
	\****************************************************************************************/
	public void connectMEServer_answer(DeviceServer _server2connect, boolean _youareconnected) throws RemoteException;
	
	/****************************************************************************************\
	|	public void connectMEClient(final DeviceClient _client) 
	|	description: utilizzato dai clients per richiedere la connessione al server e/o per 
	|				 notificare la lista di risorse
	\****************************************************************************************/
	public void connectMEClient(final DeviceClient _client) throws RemoteException;
	
	/****************************************************************************************\
	|	public void disconnectMEClient(final DeviceClient _client)
	|	description: utilizzato dai clients per richiedere la disconnessione al server
	\****************************************************************************************/
	public void disconnectMEClient(final DeviceClient _client) throws RemoteException;
	
	/****************************************************************************************\
	|	public void findResourceForServer_answer(final DeviceClient _client, final Resource _risorsa, final Vector<DeviceClient> _clientList)
	|	description: utilizzato dai server per notificare il risultato della ricerca
	\****************************************************************************************/
	public void findResourceForServer_answer(final DeviceClient _client, final Resource _risorsa, final Vector<DeviceClient> _clientList) throws RemoteException;	
	
	/****************************************************************************************\
	|	public void findResourceForServer(final DeviceServer _server, final Resource _risorsa, final DeviceClient _client) 
	|	description: utilizzato dai server per inoltrare la richiesta di ricerca risorsa
	\****************************************************************************************/
	public void findResourceForServer(final DeviceServer _server, final Resource _risorsa, final DeviceClient _client) throws RemoteException;

	/****************************************************************************************\
	|	public void findResourceForClient(final DeviceClient _client, final Resource _risorsa) 
	|	description: utilizzato dai client per chiedere la risorsa al proprio server
	\****************************************************************************************/
	public void findResourceForClient(final DeviceClient _client, final Resource _risorsa) throws RemoteException;
	
} //end interface IServer()
