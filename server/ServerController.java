/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerController.java
|	Description: componente controller del pattern MVC
|	Package: server
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - implementata ricerca server online leggendo il registro RMI
|			 0.3 - connessione automatica dei server online
|			 0.4 - creazione del thread checkConnections per eliminare i server offline
|
\****************************************************************************************/
package server;

import java.rmi.*;
import java.rmi.server.*;
import java.awt.Color;		
import java.util.Vector; 	
import client.IClient;
import common.DeviceServer;
import common.DeviceClient;
import common.Resource;
import java.awt.event.*;

public class ServerController extends UnicastRemoteObject implements IServer, ActionListener, WindowListener
{
	//impostazioni modificabili
	private static final String HOST = "localhost:1099";	//host per la connessione RMI
	
	//impostazioni NON modificabili
	private static final int CHECKCONNECTIONS_TIMEOUT = 3000;	//controllo connessione in background ogni 3secondi
	private static final String RMITAG = "P3-P2P-JK"; 			//chiave identificativa dei server per il registro RMI
			
	//riferimenti alle componenti View e Model
	private ServerView view;
	private ServerModel model;
	
	//threads del server
	Thread checkConnections;								//thread che controlla i devices connessi
	
	/****************************************************************************************\
	|	public ServerController()
	|	description: costruttore
	\****************************************************************************************/
	public ServerController() throws Exception
	{
	
	}
	
	/****************************************************************************************\
	|	public void serverInit()
	|	description: inizializza il server 
	\****************************************************************************************/
	public void serverInit()
	{
		//inizio le operazioni di avvio del server...
		int logPos = model.addLogText("inizializzazione server " + model.getServerName() + "...");		
		serverRebind(model.getServerName(),this);		//pubblico il mio nome così i clients possono collegarsi
		model.addLogTextToLine(logPos," OK!");
		model.setLogColor(Color.BLUE);
		connect2server();								//controllo i server online e mi connetto ad essi
		threadInit();									//inizializzo i thread che girano in background
	}
	
	/****************************************************************************************\
	|	private void threadInit()
	|	description: inizializza i thread del server e li avvia
	\****************************************************************************************/
	private void threadInit() 
	{
		//thread con ciclo continuo che controlla le connessioni con RMI, Server e Clients
		checkConnections = new Thread(){ 
			public void run()
			{
				while(true)
				{
					//aspetto tre secondi prima di controllare
					try{sleep(CHECKCONNECTIONS_TIMEOUT);}catch(InterruptedException ie){}
					
					//Controllo la presenza dell'RMI registry
					try{
						Naming.list("//" + HOST );
					}
					catch(Exception e)
					{
						model.setLogColor(Color.RED);
						model.addLogText("[check_thread] FATAL ERROR! connessione RMI non riuscita.");
						model.addLogText("[check_thread] server auto-shutdown tra 30sec...");	
						try{sleep(30000);}catch(InterruptedException ie){}
						System.exit(-1);
					}
					
					//controllo i server connessi alla ricerca di server morti :)
					synchronized(model)	//prendo il lock sui dati del model
					{
						//ora controllo che tutta la mia lista server a cui sono connesso 
						//siano ancora online
						for(int i=0; i<model.getNservers(); i++)
						{
							try{
								model.getServer(i).getRef().heartbeat();		//controllo se il server è vivo
							}catch(Exception e){
								model.addLogText("[check_thread] il server " + model.getServer(i).getName() + " è offline.");
								model.removeServer(model.getServer(i).getName());
							}		
						}					
					}//end synchronized(model)
					
					//controllo i clients connessi alla ricerca di clients morti :)
					synchronized(model)	//prendo il lock sui dati del model
					{
						for(int i=0; i<model.getNclients(); i++)
						{
							try{
								model.getClient(i).getRef().heartbeat();		//controllo se il client è vivo
							}catch(Exception e){
								model.addLogText("[check_thread] il client " + model.getClient(i).getName() + " è offline.");
								model.removeClient(model.getClient(i).getName());
							}		
						}					
					}//end synchronized(model)				
				
				}//end while(1)
			}// end run()
		};
		checkConnections.start();	//avvio il thread
		
	} //end threadInit()
	
	/****************************************************************************************\
	|	private void serverRebind(String, ServerController)
	|	description: pubblica il proprio nome e riferimento sul registro RMI
	\****************************************************************************************/
	private void serverRebind(String _server2rebind, ServerController _ref)
	{
		String rmiObjName = "rmi://" + HOST + "/" + RMITAG + "/" + _server2rebind;
		try{
			Naming.rebind(rmiObjName, _ref);
		}catch(Exception e){
			model.setLogColor(Color.RED);
			model.addLogText("FATAL ERROR! connessione RMI non riuscita.");
			model.addLogText("server auto-shutdown tra 30sec...");	
			try{Thread.sleep(30000);}catch(InterruptedException ie){}
			System.exit(-1);					
		}
	}
	
	/****************************************************************************************\
	|	private void serverUnbind(String, ServerController)
	|	description: rimuove il proprio nome e riferimento sul registro RMI
	\****************************************************************************************/
	private void serverUnbind(String _server2unbind)
	{
		String rmiObjName = "rmi://" + HOST + "/" + RMITAG + "/" + _server2unbind;
		try{
			Naming.unbind(rmiObjName);
		}catch(Exception e){
			model.setLogColor(Color.RED);
			model.addLogText("FATAL ERROR! connessione RMI non riuscita.");
			model.addLogText("server auto-shutdown tra 30sec...");	
			try{Thread.sleep(30000);}catch(InterruptedException ie){}
			System.exit(-1);					
		}
	}
		
	/****************************************************************************************\
	|	private IServer serverLookup(String)
	|	description: esegue il lookup del nome server nel registro RMI e ritorna il riferimento
	\****************************************************************************************/
	private IServer serverLookup(String _nome)
	{
		IServer ref = null;
		try{
			ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + _nome);
		}catch(Exception e){
			model.setLogColor(Color.RED);
			model.addLogText("FATAL ERROR! connessione RMI non riuscita.");
			model.addLogText("server auto-shutdown tra 30sec...");	
			try{Thread.sleep(30000);}catch(InterruptedException ie){}
			System.exit(-1);					
		}
		return ref;	
	}
	
	/****************************************************************************************\
	|	private String rmitag2name(String _rmitag, String _rmiString)
	|	description: estra il nome del server che segue il TAG RMI nella stringa indicata
	\****************************************************************************************/
	private String rmitag2name(String _rmitag, String _rmiString)
	{
		return _rmiString.substring(_rmiString.indexOf(_rmitag + "/") + _rmitag.length() + 1, _rmiString.length());
	}
	
	/****************************************************************************************\
	|	private void connect2server()
	|	description: cerca di recuperare la lista server nel registro RMI e si connette ad essi
	\****************************************************************************************/
	private void connect2server()
	{
		String[] serverNamesList = {};	
			
		int logPos = model.addLogText("ricerca server online...");
		
		//recupero la lista dei server iscritti al registro RMI
		try{
			serverNamesList = Naming.list("//" + HOST );
		}
		catch(Exception e)
		{
			model.setLogColor(Color.RED);
			model.addLogText("FATAL ERROR! connessione RMI non riuscita.");
			model.addLogText("server auto-shutdown tra 30sec...");	
			try{Thread.sleep(30000);}catch(Exception exc){}
			System.exit(-1);
		}
		
		IServer ref = null;
		int serverConnessi = 0;
		for(int i=0; i<serverNamesList.length; i++)
		{
			synchronized(model)		//prendo il lock sui dati del model
			{
				//controllo se contiene il TAG
				if(serverNamesList[i].contains(RMITAG + "/"))
				{
					String server2connect = rmitag2name(RMITAG, serverNamesList[i]); 	//recupero il nome del server a cui voglio connettermi
					if(server2connect.equals(model.getServerName()))continue;		//evito di connettermi a me stesso :) 
					model.addLogText("connessione al server " + server2connect + "...");
					try{
						ref = serverLookup(server2connect);							//recupero il riferimento a tale server
						if(ref.connectMEServer(model.getServerName())) 				//richiedo di connettermi al server
						{
							model.addServer(server2connect,ref);					//aggiorno l'interfaccia grafica
							model.addLogText("connesso a " + server2connect + "!");
							serverConnessi++;;
						}
					}catch(Exception e){
						model.addLogText("impossibile contattare il server " + server2connect + ", connessione fallita!");
					}
				}
			}//synchronized(model)
		}
		int serverTrovati = serverNamesList.length;
		if(serverTrovati > 0) serverTrovati--;				//rimuovo me stesso
		if(serverTrovati == 0)
		{
			model.addLogTextToLine(logPos," completata! Nessun server online.");
		}else{
			model.addLogText("ricerca server online completata! trovati " + serverTrovati + " server di cui " + serverConnessi + " online.");
		}
		
	} //connect2server()
	
	/****************************************************************************************\
	|	public void windowDeiconified(WindowEvent _e)
	|	public void windowIconified(WindowEvent _e)
	|	public void windowClosed(WindowEvent _e)
	|	public void windowOpened(WindowEvent _e)
	|	public void windowDeactivated(WindowEvent _e)
	|	public void windowDeactivated(WindowEvent _e)
	|	public void windowActivated(WindowEvent _e)
	|	description: implementazione dell'interfaccia WindowListener
	\****************************************************************************************/
	public void windowDeiconified(WindowEvent _e){}
	public void windowIconified(WindowEvent _e){}
	public void windowClosed(WindowEvent _e){}
	public void windowOpened(WindowEvent _e){}
	public void windowDeactivated(WindowEvent _e){} //lost focus
	public void windowActivated(WindowEvent _e){} //get focus
	
	/****************************************************************************************\
	|	public void windowClosing(WindowEvent _e)
	|	description: implementazione dell'interfaccia WindowListener in chiusura del server
	\****************************************************************************************/
	public void windowClosing(WindowEvent _e)
	{
		//eseguo l'unbind dal registro RMI
		serverUnbind(model.getServerName());
		System.exit(0); 
	}
	
	/****************************************************************************************\
	|	public void actionPerformed(java.awt.event.ActionEvent _e)
	|	description: implementazione dell'interfaccia ActionListener
	\****************************************************************************************/
	public void actionPerformed(ActionEvent _e)
	{
		System.out.println ("Controller: The " + _e.getActionCommand() 
			+ " button is clicked at " + new java.util.Date(_e.getWhen())
			+ " with e.paramString " + _e.paramString() );
	}
	
	/****************************************************************************************\
	|	public void addModel(ServerModel _model)
	|	description: setta il riferimento alla componente model
	\****************************************************************************************/
	public void addModel(ServerModel _model){this.model = _model;}
		
	/****************************************************************************************\
	|	public void addView(ServerView _view)
	|	description: setta il riferimento alla componente view
	\****************************************************************************************/
	public void addView(ServerView _view){this.view = _view;}

	/****************************************************************************************\
	|	public void initModel(String _nome)
	|	description: inizializza la componente model all'avvio del server
	\****************************************************************************************/
	public void initModel(String _nome)
	{
		model.setServerName(_nome);
		model.setServerRef(this);
		model.setLogColor(Color.RED);
	}
	
	/****************************************************************************************\
	|	public String heartbeat()
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public String heartbeat() throws RemoteException
	{
		return RMITAG;	//restituisco l'RMITAG per confermare che sono online
	}
	
	/****************************************************************************************\
	|	public boolean connectMEServer(String _serverName)
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public boolean connectMEServer(String _serverName) throws RemoteException
	{
		IServer ref;
		try{
			ref = serverLookup(_serverName);
		}catch(Exception e){
			model.addLogText("[new server] impossibile contattare il server " + _serverName + "!");
			ref = null;
		}
		
		if(ref != null)
		{
			synchronized(model)	//prendo il lock sui dati del model
			{
				model.addServer(_serverName,ref);		//aggiungo un nuovo server				
				model.addLogText("[new server] il server " + _serverName + " si è connesso!");
			}
			return true;
		}else{
			return false;
		}
	}
	
	/****************************************************************************************\
	|	public boolean connectMEClient(String _clientName, IClient _clientRef) 
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public boolean connectMEClient(String _clientName, IClient _clientRef) throws RemoteException
	{
		model.addLogText("[new client] il client " + _clientName + " richiede connessione!");
			
		model.addLogText("[new client] recupero lista risorse del client " + _clientName + "...");
		Vector<Resource> listaRisorse;
		try{
			listaRisorse = _clientRef.getResourceList();
		}catch(Exception e){
			e.printStackTrace();
			model.addLogText("[new client] impossibile recuperare la lista risorse!");
			listaRisorse = null;
		}
		
		if(listaRisorse != null) //se ho recuperato la lista risorse del client
		{
			synchronized(model)	//prendo il lock sui dati del model
			{
				model.addClient(_clientName,_clientRef,listaRisorse);	//aggiungo un nuovo client			
				model.addLogText("[new client] il client " + _clientName + " si è connesso!");
			} 	
			return true;
		}else{
			model.addLogText("[new client] connessione con il client " + _clientName + " fallita!");
			return false;
		}
	}
	
	/****************************************************************************************\
	|	public boolean disconnectMEClient(String _clientName) 
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public boolean disconnectMEClient(String _clientName) throws RemoteException
	{
		synchronized(model)	//prendo il lock sui dati del model
		{
			model.removeClient(_clientName);			//rimuovo il client			
			model.addLogText("il client " + _clientName + " si è disconnesso!");
		} 	
		return true;
	}

}//end class ServerController()
