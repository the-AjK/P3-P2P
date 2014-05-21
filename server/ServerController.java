/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerController.java
|	Description: componente controller del pattern MVC
|	Package: server
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - implementata ricerca server online leggendo il registro pubblico RMI
|			 0.3 - connessione automatica dei server online
|			 0.4 - aggiunta gestione threads in background
|
\****************************************************************************************/
package server;

import java.rmi.*;
import java.rmi.server.*;
import java.awt.Color;		
import java.util.Vector; 
import java.util.Set;	
import client.IClient;
import common.DeviceServer;
import common.DeviceClient;
import common.Resource;
import java.awt.event.*;
import java.io.Serializable;

public class ServerController extends UnicastRemoteObject implements IServer, ActionListener, WindowListener, Serializable
{
	//impostazioni modificabili
	private static final String HOST = "localhost:1099";		//host per la connessione RMI
	private static final boolean VERBOSE_LOG = true;			//se true visualizza piu' messaggi di log
	private static final int AUTO_SHUTDOWN_TIMEOUT = 22;		//timeout per l'auto spegnimento in caso di problemi con connessione RMI [s]
	private static final int CHECKCONNECTIONS_TIMEOUT = 3000;	//controllo connessioni in background [ms]
	
	//impostazioni NON modificabili
	private static final String RMITAG = "P3-P2P-JK"; 								//chiave identificativa dei server per il registro RMI	
	private boolean autoShutdownActive = false;										//flag che indica se e' attivo l'autoShutdown
	private static final String CHECKCONNECTIONS_THREAD = "CheckConnectionsThread";	//nome del thread controllo connessioni
	private static final String CLIENTCONNECT_THREAD = "ClientConnectThread";
	private static final String CLIENTDISCONNECT_THREAD = "ClientDisconnectThread";	
	private static final String SERVERCONNECT_THREAD = "ServerConnectThread";
	private static final String CLIENTRESEARCH_THREAD = "ClientResearchThread";		//nome del thread ricerca risorse per il client
	private static final String SERVERRESEARCH_THREAD = "ServerResearchThread";		//nome del thread ricerca risorse per il server
	private static final String SERVERRESEARCHANSWER_THREAD = "ServerResearchAnswerThread";
	
	//riferimenti alle componenti View e Model
	private ServerView view;
	private ServerModel model;
	
	//threads del server
	CheckConnectionsThread checkConnections;		//thread che controlla la connessione con RMI ed il server
	
	/****************************************************************************************\
	|	public ServerController()
	|	description: costruttore
	\****************************************************************************************/
	public ServerController() throws Exception
	{
		checkConnections = new CheckConnectionsThread();
	}
	
	/****************************************************************************************\
	|	public void serverInit()
	|	description: inizializza il server 
	\****************************************************************************************/
	public void serverInit()
	{
		//inizio le operazioni di avvio del server...
		int logPos = model.addLogText("inizializzazione server...");		
		serverRebind(model.getServerName(),this);		//pubblico il mio nome cosi' i clients possono collegarsi
		model.addLogTextToLine(logPos," completata!");
		model.setLogColor(Color.BLUE);
		connect2server();								//controllo i server online e mi connetto ad essi
		startBackgroundThreads();						//avvio i threads che girano in background
	}
	
	/****************************************************************************************\
	|	private void startBackgroundThreads()
	|	description: avvia i thread in background del server
	\****************************************************************************************/
	private void startBackgroundThreads()
	{
		//aggiungere qui thread da avviare in background...
		checkConnections.start();
	}
	
	/****************************************************************************************\
	|	private class checkConnections
	|	description: thread che controlla la connessione con RMI ed il server
	\****************************************************************************************/
	private class CheckConnectionsThread extends Thread
	{
		public CheckConnectionsThread()
		{
			super(CHECKCONNECTIONS_THREAD);
		}
		
		public void run()
		{
			String[] animIcon = {"PSP","P2P"};
			try{
				while( !Thread.currentThread().isInterrupted() )
				{
					//animazione durante l'attesa, lampeggio veloce del titolo
					for(int i=0; i<animIcon.length; i++)
					{
						sleep(100);					
						model.setAnimIcon(animIcon[i]);							
					}
					sleep(CHECKCONNECTIONS_TIMEOUT);
										
					//Controllo la presenza dell'RMI registry
					try{
						Naming.list("//" + HOST );
					}
					catch(Exception e)
					{
						model.setLogColor(Color.RED);
						model.addLogText("[check_T] FATAL ERROR! connessione RMI non riuscita.");
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
								if(model.getServer(i).getRef().heartbeat().equals(IServer.HEARTBEAT_ANSWER)) //controllo se il server e' vivo
								{
								
								}
							}catch(Exception e){
								model.addLogText("[check_T] il server " + model.getServer(i).getName() + " e' offline.");
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
								if(model.getClient(i).getRef().heartbeat().equals(IClient.HEARTBEAT_ANSWER)) //controllo se il client e' vivo
								{
								
								}
							}catch(Exception e){
								model.addLogText("[check_T] il client " + model.getClient(i).getName() + " e' offline.");
								model.removeClient(model.getClient(i).getName());
							}		
						}					
					}//end synchronized(model)				
				
				}//end while( !Thread.currentThread().isInterrupted() )	
			}catch(InterruptedException ie){
				model.addLogText("[check_T] interrupted exception!");
			}
			finally{
				model.addLogText("[check_T] thread controllo connessioni terminato in modo imprevisto!");
			}
		}// end run()

	} //end CheckConnectionsThread
	
	/****************************************************************************************\
	|	private Thread getThread(String _threadName)
	|	description: restituisce il thread
	\****************************************************************************************/
	private Thread getThread(String _threadName)
	{
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadList = threadSet.toArray(new Thread[threadSet.size()]);
		for(int i=0; i<threadList.length; i++)
		{
			if(threadList[i].getName().startsWith(_threadName))return threadList[i];
		}
		return null;
	}
	
	/****************************************************************************************\
	|	private boolean killThread(String _threadName)
	|	description: restituisce il thread
	\****************************************************************************************/
	private boolean killThread(String _threadName)
	{
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadList = threadSet.toArray(new Thread[threadSet.size()]);
		for(int i=0; i<threadList.length; i++)
		{
			if(threadList[i].getName().startsWith(_threadName))
			{
				threadList[i].interrupt();
				return true;
			}
		}
		return false;
	}
	
	/****************************************************************************************\
	|	private void killAllDownloadThreads()
	|	description: interrompe tutti i thread che gestiscono il download
	\****************************************************************************************/
	private void killAllDownloadThreads()
	{
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadList = threadSet.toArray(new Thread[threadSet.size()]);
		for(int i=0; i<threadList.length; i++)
		{
			/*if(threadList[i].getName().startsWith(RICERCARISORSA_THREAD) ||
			   threadList[i].getName().startsWith(DOWNLOADMANAGER_THREAD) ||
			   threadList[i].getName().startsWith(DOWNLOADRESOURCEPART_THREAD) )			
				threadList[i].interrupt();
				*/
		}
	}
	
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
	|	description: estrae il nome del server che segue il TAG RMI nella stringa indicata
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
			try{Thread.sleep(1000);}catch(Exception e){}
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
		for(int i=0; i<serverNamesList.length; i++)
		{
			//controllo se contiene il TAG
			if(serverNamesList[i].contains(RMITAG + "/"))
			{
				String server2connect = rmitag2name(RMITAG, serverNamesList[i]); 		//recupero il nome del server a cui voglio connettermi
				if(server2connect.equals(model.getServerName()))continue;				//evito di connettermi a me stesso :) 
				model.addLogText("invio richiesta connessione al server " + server2connect + "...");
				try{
					ref = serverLookup(server2connect);									//recupero il riferimento a tale server
					ref.connectMEServer(model.me());									//richiedo di connettermi al server
				}catch(Exception e){
					model.addLogText("connessione al server " + server2connect + " fallita");
					e.printStackTrace();
				}
			}			
		}
		int serverTrovati = serverNamesList.length;
		if(serverTrovati > 0) serverTrovati--;				//rimuovo me stesso
		if(serverTrovati == 0)
		{
			model.addLogTextToLine(ricercaPos," completata!");
			model.addLogText("nessun altro server online.");
		}else{
			model.addLogText("ricerca server online completata!");
			//model.addLogText("trovati " + serverTrovati + " server di cui " + serverConnessi + " online.");
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
	|	public void actionPerformed(ActionEvent _e)
	|	description: implementazione dell'interfaccia ActionListener
	\****************************************************************************************/
	public void actionPerformed(ActionEvent _e)
	{
		//implementare qui le azioni in base all'ActionEvent ricevuta dalla vista
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
	|	public void connectMEServer(DeviceServer _server)
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void connectMEServer(final DeviceServer _server) throws RemoteException
	{
		//avvia un thread di connessione per server
		(new Thread(SERVERCONNECT_THREAD + "_" + _server.getName()){
		
			private boolean CONNECTED_STATUS = false;
			
			public void run()
			{
				if(VERBOSE_LOG)
					model.addLogText("[newServer_T] il server " + _server.getName() + " richiede connessione!");
					
				//controllo che non sia gia' connesso
				if(model.serverIsHere(_server.getName()))
				{
					if(VERBOSE_LOG)
						model.addLogText("[newServer_T] il server " + _server.getName() + " e' gia' connesso!");
					CONNECTED_STATUS = true;
				}else{
				
					try{
						if(_server.getRef().heartbeat().equals(HEARTBEAT_ANSWER))
						{
							model.addServer(_server);		//aggiungo un nuovo server				
							model.addLogText("[newServer_T] il server " + _server.getName() + " si e' connesso!");
							CONNECTED_STATUS = true;
						}			
					}catch(Exception e){
						if(VERBOSE_LOG)
							model.addLogText("[newServer_T] impossibile contattare il server " + _server.getName() + "!");
					}
				}
				
				try{
					_server.getRef().connectMEServer_answer(model.me(), CONNECTED_STATUS); //rispondo al server
				}catch(Exception e){
					if(VERBOSE_LOG)
							model.addLogText("[newServer_T] impossibile notificare il server " + _server.getName() + "!");
				}
				
			}//end run()
		}).start(); //avvio il thread richiesta connessione al server
	}
	
	/****************************************************************************************\
	|	public void connectMEServer_answer(DeviceServer _server2connect, boolean _youareconnected)
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void connectMEServer_answer(DeviceServer _server2connect, boolean _youareconnected) throws RemoteException
	{
		if(_youareconnected)
		{
			model.addLogText("connessione al server " + _server2connect.getName() + " completata!");
			model.addServer(_server2connect);		//aggiorno l'interfaccia grafica
		}else{
			model.addLogText("connessione al server " + _server2connect.getName() + " fallita!");
		}
	}
	
	/****************************************************************************************\
	|	public void connectMEClient(DeviceClient _client) 
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void connectMEClient(final DeviceClient _client) throws RemoteException
	{
		//avvia un thread di connessione per client
		(new Thread(CLIENTCONNECT_THREAD + "_" + _client.getName()){
		
			private boolean CONNECTED_STATUS = false;
			private boolean NEW_CLIENT = true;
			
			public void run()
			{
				//se e' gia' connesso significa che il client vuole inviarmi una nuova lista risorse
				if(model.clientIsHere(_client.getName()))
				{
					if(VERBOSE_LOG)
						model.addLogText("[client " + _client.getName() + "] ricezione nuova lista risorse...");
					NEW_CLIENT = false;
				}else{
					if(VERBOSE_LOG)
						model.addLogText("[newClient_T] il client " + _client.getName() + " richiede connessione!");
				}
				
				if(NEW_CLIENT)
				{
					model.addClient(_client);	//aggiungo un nuovo client	
					model.addLogText("[newClient_T] il client " + _client.getName() + " si e' connesso!");
				}else{
					model.addClientResourceList(_client.getName(), _client.getResourceList());	//aggiorno la lista risorse
					model.addLogText("[client " + _client.getName() + "] lista risorse aggiornata!");
				}
						
				CONNECTED_STATUS = true;
				
				try{
					_client.getRef().connectMEClient_answer(model.me(), CONNECTED_STATUS);
				}catch(Exception e){
					if(VERBOSE_LOG)
							model.addLogText("[newClient_T] impossibile notificare il client " + _client.getName() + "!");
				}
				
			}//end run()		
		}).start(); //avvio il thread di richiesta connessione per i client
	}
	
	/****************************************************************************************\
	|	public void disconnectMEClient(final DeviceClient _client) 
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void disconnectMEClient(final DeviceClient _client) throws RemoteException
	{
		//avvia un thread di disconnessione per client
		(new Thread(CLIENTDISCONNECT_THREAD + "_" + _client.getName()){
		
			private boolean DISCONNECTED_STATUS = false;
					
			public void run()
			{			
				//se il client non e' connesso, non faccio nulla
				if(!model.clientIsHere(_client.getName()))
				{
					DISCONNECTED_STATUS = false;
				}else{
					model.removeClient(_client.getName());			//rimuovo il client	
						
					//TODO -> interrompere tutti i threads (ricerca) di questo client che vuole disconnettersi
						
					model.addLogText("il client " + _client.getName() + " si e' disconnesso!"); 	
					DISCONNECTED_STATUS = true;
				}
				
				try{
					_client.getRef().disconnectMEClient_answer(model.me(), DISCONNECTED_STATUS);
				}catch(Exception e){
					if(VERBOSE_LOG)
							model.addLogText("impossibile notificare la disconnessione al client " + _client.getName() + "!");
				}			
				
			}//end run()
		}).start(); //avvio il thread di richiesta disconnessione
	}
	
	/****************************************************************************************\
	|	public void findResourceForServer_answer(final DeviceClient _client, final Resource _risorsa, final Vector<DeviceClient> _clientList)
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void findResourceForServer_answer(final DeviceClient _client, final Resource _risorsa, final Vector<DeviceClient> _clientList) throws RemoteException
	{
		(new Thread(SERVERRESEARCHANSWER_THREAD + "_" + _client.getName() + "_" + _risorsa.getName() + " " + _risorsa.getNparts()){
		
			public void run(){
				model.addResearchClientList(_client, _risorsa, _clientList);
				//ho ricevuto la risposta da un server, quindi decremento il contatore richieste
				model.decrementNumberOfRequests(_client, _risorsa);
				if(model.getNumberOfRequests(_client, _risorsa) == 0)	//se ho ricevuto tutte le risposte
				{
					//interrompo il thread di ricerca
					killThread(CLIENTRESEARCH_THREAD + "_" + _client.getName() + "_" + _risorsa.getName() + " " + _risorsa.getNparts());
				}
			}//end run()
			
		}).start();
	}
	
	/****************************************************************************************\
	|	public void findResourceForServer(final DeviceServer _server, final Resource _risorsa, final DeviceClient _client) 
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void findResourceForServer(final DeviceServer _server, final Resource _risorsa, final DeviceClient _client) throws RemoteException
	{
		(new Thread(SERVERRESEARCH_THREAD + "_" + _server.getName() + "_" + _risorsa.getName() + " " + _risorsa.getNparts()){
		
			public void run(){
				if(VERBOSE_LOG)
					model.addLogText("il server " + _server.getName() + " richiede ricerca di " + _risorsa.getName() + " " + _risorsa.getNparts());
				
				//cerco tra i miei client locali se la risorsa e' presente
				//e richiamo un metodo remoto al server che ha fatto la richiesta
				try{
					_server.getRef().findResourceForServer_answer(_client, _risorsa, model.getClientsOwnResourceList(_risorsa));
				}catch(Exception e){
					if(VERBOSE_LOG)
						model.addLogText("impossibile notificare il risultato ricerca al server " + _server.getName() + "!");
				}
			}//end run()
		}).start(); //avvio il thread ricerca server		
	}
	
	/****************************************************************************************\
	|	public void findResourceForClient(final DeviceClient _client, final Resource _risorsa) 
	|	description: implementazione del metodo remoto dell'interfaccia IServer
	\****************************************************************************************/
	public void findResourceForClient(final DeviceClient _client, final Resource _risorsa) throws RemoteException
	{
		(new Thread(CLIENTRESEARCH_THREAD + "_" + _client.getName() + "_" + _risorsa.getName() + " " + _risorsa.getNparts()){
		
			public void run(){
				
				if(VERBOSE_LOG)
					model.addLogText("il client " + _client.getName() + " richiede ricerca di " + _risorsa.getName() + " " + _risorsa.getNparts());
				
				//aggiungo la richiesta in coda
				model.addResearchRequest(_client, _risorsa);
				
				//cerco tra i miei client locali se la risorsa e' presente ed aggiorno la coda richieste
				model.addResearchClientList(_client, _risorsa, model.getClientsOwnResourceList(_risorsa));
				
				//ora inoltro la richiesta a tutti i server a cui sono collegato
				DeviceServer server;
				for(int i=0; i<model.getNservers(); i++)
				{
					server = model.getServerList().get(i); 
					try{
						//inoltro la richiesta al server ed aggiungo la risposta nella listaClient
						server.getRef().findResourceForServer(model.me(), _risorsa, _client);
						//incremento il numero delle richieste inviate ai vari server
						model.incrementNumberOfRequests(_client, _risorsa);						
					}catch(Exception e){
						model.addLogText("errore inoltro richiesta al server " + server.getName());
					}
				}	
	
				//se il numero di richieste è maggiore di zero, aspetto la risposta...
				if(model.getNumberOfRequests(_client, _risorsa) > 0)
				{
					try{
						sleep(10000);
						model.addLogText("timout ricezione risposte dai server");
					}catch(InterruptedException ie){
						model.addLogText("ricevute tutte le risposte dai server");
					}		
				}
			
				//notifico il client con la lista risorse
				try{
					_client.getRef().findResourceForClient_answer(_risorsa, model.getResearchClientList(_client,_risorsa));
				}catch(Exception e){
					model.addLogText("impossibile notificare la lista clients...");
				}

			}//end run()
		}).start(); //avvio il thread ricerca client	
	}

}//end class ServerController()
