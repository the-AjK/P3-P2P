/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerController.java
|	Description: componente controller del pattern MVC
|	Package: server
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - 
|
\****************************************************************************************/
package server;

import java.rmi.*;
import java.rmi.server.*;
import java.awt.Color;			//color
//import java.util.*; 			//vector
//import java.io.*; 			//IO exceptions
//import java.net.MalformedURLException;	//malformedURLexception

//import client.IClient;

import common.DeviceServer;
import common.DeviceClient;
import common.Resource;

public class ServerController extends UnicastRemoteObject implements IServer, java.awt.event.ActionListener
{
	private static final String HOST = "localhost:1099";	//host per la connessione RMI
	private static final String RMITAG = "P3-P2P-JK"; 		//chiave identificativa per il registro RMI
			
	//riferimenti alle componenti View e Model
	private ServerView view;
	private ServerModel model;
	
	//nome del server
	//private String nomeServer;
	
	//lista di clients connessi e relative risorse
	//private Vector<ClientsResourceList> clientList;
	
	//lista di server connessi
	//private Vector<String> serverList;
	
	//metodo che ritorna il riferimento della lista di risorse un determinato client
	/*private ClientsResourceList getClientRef(String nomeClient)
	{
		for(int i=0; i<clientList.size(); i++)
		{
			if(clientList.get(i).getNomeClient().equals(nomeClient))
				return clientList.get(i);
		}
		return null;		
	}
	*/
	
	//costruttore
	public ServerController() throws Exception
	{
	
	}
	
	public void serverInit() throws Exception
	{
		//inizio le operazioni di avvio del server...
		serverRebind(model.getServerName(),this);		//pubblico il mio nome così i clients possono collegarsi
		model.addLogText("server " + model.getServerName() + " pronto!");
		model.setLogColor(Color.BLUE);
		connect2server();								//controllo i server online e mi connetto ad essi
	}
	
	/****************************************************************************************\
	|	method: private void serverRebind(String, ServerController)
	|	description: pubblica il proprio nome e riferimento sul registro RMI
	\****************************************************************************************/
	private void serverRebind(String server2rebind, ServerController ref) throws Exception
	{
		String rmiObjName = "rmi://" + HOST + "/" + RMITAG + "/" + server2rebind;
		Naming.rebind(rmiObjName, ref);
	}
	
	/****************************************************************************************\
	|	method: private IServer serverLookup(String)
	|	description: esegue il lookup del nome server nel registro RMI e ritorna il riferimento
	\****************************************************************************************/
	private IServer serverLookup(String nome) throws Exception
	{
		IServer ref = null;
		ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + nome);
		return ref;	
	}
	
	/*private IClient lookupClient(String nome)
	{
		IClient ref = null;
		for(int i=0; i<clientList.size(); i++)
		{
			if(clientList.get(i).getNomeClient().compareTo(nome)==0)
			{
				ref = clientList.get(i).getRefC(); //prendo il riferimento all'oggetto remoto
				break;
			}
		}
		return ref;		
	}
	*/
	
	private String RMITAG2ServerName(String _rmiString)
	{
		return _rmiString.substring(_rmiString.indexOf(RMITAG + "/") + RMITAG.length() + 1, _rmiString.length());
	}
	
	/****************************************************************************************\
	|	function: private void connect2server()
	|	description: cerca di recuperare la lista server nel registro RMI e si connette ad essi
	\****************************************************************************************/
	private void connect2server() throws Exception
	{
		String[] serverNamesList = {};	
			
		model.addLogText("ricerca server online...");
		
		//recupero la lista dei server iscritti al registro RMI
		try{
			serverNamesList = Naming.list("//" + HOST );
		}
		catch(ConnectException e)
		{
			model.addLogText("impossibile recuperare la lista server!");
			model.addLogText("errore di connessione al registro RMI!");	
			model.addLogText("server auto-shutdown tra 30sec...");	
			Thread.sleep(30000);
			System.exit(-1);
		}
		
		IServer ref = null;
		
		for(int i=0; i<serverNamesList.length; i++)
		{
			//controllo se contiene il TAG
			if(serverNamesList[i].contains(RMITAG + "/"))
			{
				String server2connect = RMITAG2ServerName(serverNamesList[i]); 	//recupero il nome del server a cui voglio connettermi
				if(server2connect.equals(model.getServerName()))continue;		//evito di connettermi a me stesso :) 
				try{
					ref = serverLookup(server2connect);							//recupero il riferimento a tale server
					ref.connectMEServer(model.getServerName()); 				//richiedo di connettermi al server
				}catch(Exception e){
					model.addLogText("impossibile contattare il server " + server2connect + "!");
					ref = null;
				}
				if(ref != null)													//se mi sono collegato al server
				{
					model.addServer(server2connect,ref);						//aggiorno l'interfaccia grafica
					model.addLogText("connesso a " + server2connect + "!");
				}
			}
		}
		
	} //connect2server()
	
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		System.out.println ("Controller: The " + e.getActionCommand() 
			+ " button is clicked at " + new java.util.Date(e.getWhen())
			+ " with e.paramString " + e.paramString() );
	}
	
	//metodi per impostare i riferimenti al model ed alla view
	public void addModel(ServerModel _model)
	{
		this.model = _model;
	}

	public void addView(ServerView _view)
	{
		this.view = _view;
	}

	public void initModel(String _nome)
	{
		model.setServerName(_nome);
		model.setLogColor(Color.RED);
		model.addLogText("inizializzazione server avviata...");		
	}
	
	//implemento i metodi dell'interfaccia IServer
	public void connectMEServer(String _serverName) throws RemoteException
	{
		IServer ref;
		try{
			ref = serverLookup(_serverName);
		}catch(Exception e){
			model.addLogText("impossibile contattare il server " + _serverName + "!");
			ref = null;
		}
		
		if(ref != null)
		{
			model.addServer(_serverName,ref);					
			model.addLogText("connesso a " + _serverName + "!");
		}
	}

}//end class ServerController()