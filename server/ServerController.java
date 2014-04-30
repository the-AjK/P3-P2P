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
//import client.IClientImpl;
//import client.ClientResource;
//import client.Client;

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
	//public ServerController(String _nome, JTextArea _clients, JTextArea _servers, JTextArea _log) throws Exception 
	public ServerController() throws Exception 
	{
		
				
		//inizio le operazioni di avvio del server...
		//connect2server(); 					//controllo i server online e mi connetto ad essi
		//serverRebind(nomeServer,this);		//pubblico il mio nome così i clients possono collegarsi
		//log.setForeground(Color.BLUE);			
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
	
	/****************************************************************************************\
	|	function: private void connect2server()
	|	description: cerca di recuperare la lista server nel registro RMI e si connette ad essi
	\****************************************************************************************/
	private void connect2server() throws Exception
	{
		//recupero la lista dei server iscritti al registro RMI
		String[] serverNamesList = Naming.list("//" + HOST );
		
		//mi connetto...
		for(int i=0; i<serverNamesList.length; i++)
		{
			//controllo se contiene il TAG
			if(serverNamesList[i].contains(RMITAG + "/"))
			{
				//String server2connect = ; 								//recupero il nome del server a cui voglio connettermi
				//IServer ref = serverLookup(server2connect);				//recupero il riferimento a tale server
				//ref.connectMEServer(nomeServer); 						//richiedo di connettermi al server
				//servers.append(serverList.get(i) + "\n");				//aggiorno l'interfaccia grafica: server
				//log.append("connesso a " + serverList.get(i) + "!\n");	//ed i log
				//log.append("contiene " + i + ": " + serverNamesList[i] + "\n");
			}else{
				//log.append("nolista " + i + ": " + serverNamesList[i] + "\n");
			}
		}
		//se la lista non è vuota, significa che ci sono altri server online
		/*if(ref!=null)
		{	
			
			//quindi mi connetto a tutti i server
			//log.append("mi connetto ai server...\n");
			for(int i=0; i<serverList.size(); i++)
			{
				ref = RMIlookup(serverList.get(i)); 					//prendo il riferimento al server
				ref.connectMEServer(nomeServer); 						//richiedo di connettermi al server
				servers.append(serverList.get(i) + "\n");				//aggiorno l'interfaccia grafica: server
				log.append("connesso a " + serverList.get(i) + "!\n");	//ed i log
			}
			serverList.add(nomeServer);									//mi aggiungo anch'io alla lista
		}
		*/
		
	}	
	
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
		model.addLogText("inizializzazione completata.");
		model.addLogText("ricerca server online...");
		
		//log.append("inizializzazione del server " + nome + " completata!\n");
		model.setLogColor(Color.BLUE);
	}

}//end class ServerController()