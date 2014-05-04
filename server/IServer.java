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
	|	public boolean connectMEServer(String _serverName)
	|	description: utilizzato dai server per richiedere la connessione al server
	\****************************************************************************************/
	public boolean connectMEServer(String _serverName, IServer _serverRef) throws RemoteException;
	
	/****************************************************************************************\
	|	public boolean connectMEClient(String _clientName, IClient _clientRef)
	|	description: utilizzato dai clients per richiedere la connessione al server e/o per 
	|				 notificare la lista di risorse
	\****************************************************************************************/
	public boolean connectMEClient(String _clientName, IClient _clientRef) throws RemoteException;
	
	/****************************************************************************************\
	|	public boolean disconnectMEClient(String _clientName)
	|	description: utilizzato dai clients per richiedere la disconnessione al server
	\****************************************************************************************/
	public boolean disconnectMEClient(String _clientName) throws RemoteException;
	
	/****************************************************************************************\
	|	public Vector<DeviceClient> findResourceForServer(String _serverName, Resource _risorsa) 
	|	description: utilizzato dai server per inoltrare la richiesta di ricerca risorsa
	\****************************************************************************************/
	public Vector<DeviceClient> findResourceForServer(String _serverName, Resource _risorsa) throws RemoteException;

	/****************************************************************************************\
	|	public Vector<DeviceClient> findResourceForClient(String _clientName, Resource _risorsa) 
	|	description: utilizzato dai client per chiedere la risorsa al proprio server
	\****************************************************************************************/
	public Vector<DeviceClient> findResourceForClient(String _clientName, Resource _risorsa) throws RemoteException;
	
} //end interface IServer()
