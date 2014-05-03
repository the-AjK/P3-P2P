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
	private static final String HOST = "localhost:1099";		//host per la connessione RMI
	private static final boolean VERBOSE_LOG = true;			//se true visualizza più messaggi di log
	private static final int AUTO_SHUTDOWN_TIMEOUT = 22;		//timeout per l'auto spegnimento in caso di problemi con connessione RMI [s]
	private static final int CHECKCONNECTIONS_TIMEOUT = 3000;	//controllo connessioni in background [ms]
	
	//impostazioni NON modificabili
	private static final String RMITAG = "P3-P2P-JK"; 			//chiave identificativa dei server per il registro RMI
	
	private boolean autoShutdownActive = false;					//flag che indica se è attivo l'autoShutdown
			
	//riferimenti alle componenti View e Model
	private ServerView view;
	private ServerModel model;
	
	//threads del server
	Thread checkConnections;									//thread che controlla i devices connessi
	
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
		int logPos = model.addLogText("inizializzazione server...");		
		serverRebind(model.getServerName(),this);		//pubblico il mio nome così i clients possono collegarsi
		model.addLogTextToLine(logPos," completata!");
		model.setLogColor(Color.BLUE);
		connect2server();								//controllo i server online e mi connetto ad essi
		threadInit();									//avvio i thread che girano in background
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
				String[] animIcon = {"PSP","P2P"};
				while(true)
				{
					//animazione durante l'attesa, lampeggio veloce del titolo
					for(int i=0; i<animIcon.length; i++)
					{
						try{sleep(100);}catch(InterruptedException ie){}					
						model.setAnimIcon(animIcon[i]);							
					}
					try{sleep(CHECKCONNECTIONS_TIMEOUT);}catch(InterruptedException ie){}	
										
					//Controllo la presenza dell'RMI registry
					try{
						Naming.list("//" + HOST );
					}
					catch(Exception e)
					{
						model.setLogColor(Color.RED);
						model.addLogText("[check_thread] FATAL ERROR! connessione RMI non riuscita.");
						autoShutdown(AUTO_SHUTDOWN_TIMEOUT);
					}
					
					//controllo i server connessi alla ricerca di server morti :)
					synchronized(model)	//prendo il lock sui dati del model
					{
						//ora controllo che tutta la mia lista server a cui sono connesso 
						//siano ancora online
						for(int i=0; i<model.getNservers(); i++)
						{
							try{
								if(model.getServer(i).getRef().heartbeat().equals(IServer.HEARTBEAT_ANSWER)) //controllo se il server è vivo
								{
								
								}
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
								if(model.getClient(i).getRef().heartbeat().equals(IClient.HEARTBEAT_ANSWER)) //controllo se il client è vivo
								{
								
								}
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
			autoShutdown(AUTO_SHUTDOWN_TIMEOUT);					
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
			autoShutdown(AUTO_SHUTDOWN_TIMEOUT);					
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
			autoShutdown(AUTO_SHUTDOWN_TIMEOUT);					
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
	|	private void autoShutdown(int _sec)
	|	description: spegnimento automatico dopo _sec secondi
	\****************************************************************************************/
	private void autoShutdown(int _sec)
	{
		autoShutdownActive = true;		//attivo il flag
		int logPos = model.addLogText("server auto-shutdown tra " + _sec + "sec...");
		for(int i=_sec; i>=0; i--)
		{
			model.setLogText(logPos,"server auto-shutdown tra " + i + "sec...");	
			try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
		}
		System.exit(-1);
	}
	
	/****************************************************************************************\
	|	private void connect2server()
	|	description: cerca di recuperare la lista server nel registro RMI e si connette ad essi
	\****************************************************************************************/
	private void connect2server()
	{
		String[] serverNamesList = {};	
			
		int ricercaPos = model.addLogText("ricerca server online...");
		
		//recupero la lista dei server iscritti al registro RMI
		try{
			serverNamesList = Naming.list("//" + HOST );
		}
		catch(Exception e)
		{
			model.setLogColor(Color.RED);
			model.addLogText("FATAL ERROR! connessione RMI non riuscita.");
			autoShutdown(AUTO_SHUTDOWN_TIMEOUT);
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
					int logPos = model.addLogText("connessione al server " + server2connect + "...");
					try{
						ref = serverLookup(server2connect);							//recupero il riferimento a tale server
						if(ref.connectMEServer(model.getServerName())) 				//richiedo di connettermi al server
						{
							model.addServer(server2connect,ref);					//aggiorno l'interfaccia grafica
							model.addLogTextToLine(logPos," completata!");
							serverConnessi++;;
						}
					}catch(Exception e){
						model.addLogTextToLine(logPos," fallita!");
					}
				}
			}//synchronized(model)
		}
		int serverTrovati = serverNamesList.length;
		if(serverTrovati > 0) serverTrovati--;				//rimuovo me stesso
		if(serverTrovati == 0)
		{
			model.addLogTextToLine(ricercaPos," completata!");
			model.addLogText("nessun altro server online.");
		}else{
			model.addLogText("ricerca server online completata!");
			model.addLogText("trovati " + serverTrovati + " server di cui " + serverConnessi + " online.");
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
		//eseguo l'unbind dal registro RMI se ero connesso correttamente
		if(!autoShutdownActive)
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
	|	public void initModel(String _nomeServer)
	|	description: inizializza la componente model all'avvio del server
	\****************************************************************************************/
	public void initModel(String _nomeServer)
	{
		model.setServerName(_nomeServer);
		model.setServerRef(this);
		model.setLogColor(Color.RED);
		model.addLogText("[Server " + _nomeServer + "] Log di sistema:");
	}
	
	/****************************************************************************************\
	|	public String heartbeat()
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public String heartbeat() throws RemoteException
	{
		return HEARTBEAT_ANSWER;
	}
	
	/****************************************************************************************\
	|	public boolean connectMEServer(String _serverName)
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public boolean connectMEServer(String _serverName) throws RemoteException
	{
		if(VERBOSE_LOG)
			model.addLogText("[new server] il server " + _serverName + " richiede connessione!");
			
		//controllo che non sia già connesso
		if(model.serverIsHere(_serverName))
		{
			if(VERBOSE_LOG)
				model.addLogText("[new server] il server " + _serverName + " è già connesso!");
			return false;
		}
		
		IServer ref;
		try{
			ref = serverLookup(_serverName);
		}catch(Exception e){
			if(VERBOSE_LOG)
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
		boolean NEW_CLIENT = true;
		int logPos = 0;
		
		//se è già connesso significa che il client vuole inviarmi una nuova lista risorse
		if(model.clientIsHere(_clientName))
		{
			if(VERBOSE_LOG)
				logPos = model.addLogText("[client " + _clientName + "] nuova lista risorse...");
			NEW_CLIENT = false;
		}else{
			if(VERBOSE_LOG)
				model.addLogText("[new client] il client " + _clientName + " richiede connessione!");
		}
		
		if(VERBOSE_LOG && NEW_CLIENT)
			model.addLogText("[new client] recupero lista risorse del client " + _clientName + "...");
		Vector<Resource> listaRisorse;
		try{ 
			listaRisorse = _clientRef.getResourceList();
		}catch(Exception e){
			if(VERBOSE_LOG)
			{
				if(NEW_CLIENT)
					model.addLogText("[new client] impossibile recuperare la lista risorse!");
				else
					model.addLogTextToLine(logPos," fail!");
			}
			listaRisorse = null;
		}
		
		if(listaRisorse != null) //se ho recuperato la lista risorse del client
		{
			synchronized(model)	//prendo il lock sui dati del model
			{
				if(NEW_CLIENT)
				{
					model.addClient(_clientName,_clientRef,listaRisorse);	//aggiungo un nuovo client	
					model.addLogText("[new client] il client " + _clientName + " si è connesso!");
				}else{
					model.addClientResourceList(_clientName,listaRisorse);		//aggiorno la lista risorse
					model.addLogTextToLine(logPos," ok!");
				}
			} 	
			return true;
		}else{
			if(NEW_CLIENT)
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
		//se il client non è connesso, non faccio nulla
		if(!model.clientIsHere(_clientName))return false;
		
		synchronized(model)	//prendo il lock sui dati del model
		{
			model.removeClient(_clientName);			//rimuovo il client			
			model.addLogText("il client " + _clientName + " si è disconnesso!");
		} 	
		return true;
	}

}//end class ServerController()
