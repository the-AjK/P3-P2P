/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ClientController.java
|	Description: componente controller del pattern MVC
|	Package: client
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - gestione disconnessione server e registro RMI
|			 0.3 - aggiunta gestione threads in background
|
\****************************************************************************************/
package client;

import java.rmi.*;
import java.rmi.server.*;
import java.awt.event.*;
import java.util.Vector; 
import java.util.Set;
import java.awt.Color;
import common.Resource;
import common.DeviceClient;
import server.IServer;
import java.io.Serializable;
import java.util.Random;

public class ClientController extends UnicastRemoteObject implements IClient, ActionListener, WindowListener, Serializable
{
	//impostazioni modificabili
	private static final String HOST = "localhost:1099";					//host per la connessione RMI
	private static final int DOWNLOAD_TIMEOUT = 7000;						//tempo per simulare il download di una parte di risorsa
	private static final boolean VERBOSE_LOG = true;						//se true visualizza piu' messaggi di log
	
	
	//impostazioni NON modificabili
	private static final int CHECKCONNECTIONS_TIMEOUT = 3000;				//timeout per controllo connessione in background
	private static final int CHECKDOWNLOADQUEUE_TIMEOUT = 2000;				//timeout per controllo coda download
	private static final boolean RESOURCE_EMPTY = false;					//risorsa vuota
	private static final boolean RESOURCE_FULL = true;						//risorsa completa
	private static final String RMITAG = "P3-P2P-JK"; 						//chiave identificativa per il registro RMI
	private static final String CONNECT_BUTTON_TEXT    = "Connetti";		//testo del pulsante di disconnessione
	private static final String DISCONNECT_BUTTON_TEXT = "Disconnetti";	
	private static final String CONNECTION_BUTTON_TEXT = "in connessione...";
	private static final String FIND_BUTTON_TEXT = "Cerca e Scarica";
	private static final String CHECKCONNECTIONS_THREAD = "CheckConnectionsThread";	//nome del thread controllo connessioni
	private static final String RICERCARISORSA_THREAD = "RicercaRisorsaThread";	
	private static final String DOWNLOADMANAGER_THREAD = "DownloadManagerThread";
	private static final String DOWNLOADRESOURCEPART_THREAD = "DownloadResourcePartThread";
	private static final String UPLOADRESOURCEPART_THREAD = "UploadResourcePartThread";
	
	//gestione numero random per il thread download
	private Random rand;

	//riferimenti alle componenti View e Model
	private ClientView view;
	private ClientModel model;
	
	//threads del client
	CheckConnectionsThread checkConnections;		//thread che controlla la connessione con RMI ed il server
	DownloadManagerThread downloadManager;			//thread che gestisce i download, svuota la coda di download	
	
	/****************************************************************************************\
	|	public ClientController() 
	|	description: costruttore
	\****************************************************************************************/
	public ClientController() throws Exception 
	{
		checkConnections = new CheckConnectionsThread();
		rand = new Random();
	}
	
	/****************************************************************************************\
	|	public void clientInit()
	|	description: inizializza il client 
	\****************************************************************************************/
	public void clientInit()
	{
		int logPos = model.addLogText("inizializzazione...");
		//aggiungere qui operazioni di inizializzazione client...
		model.addLogTextToLine(logPos," completata!");
		startBackgroundThreads();				//avvio i threads che girano in background
	}
	
	/****************************************************************************************\
	|	private void startBackgroundThreads()
	|	description: avvia i threads in background del client
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
			boolean FIRST_TIME = true;	//FLAG attivo solamente al primo avvio del client
			boolean RMI_OK = false;		//FLAG che indica lo stato della connessione al registro RMI
			boolean SERVER_OK = false;	//FLAG status del mio server
			IServer ref;				//riferimento remoto del mio server
			String[] animIcon = {"PSP","P2P"};
			model.addLogText("[check_T] connessione automatica avviata...");
			model.setDisconnectBtext(CONNECTION_BUTTON_TEXT);				
				
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
					
					//controllo lo stato del server e di conseguenza anche del registro RMI
					try{
						ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + model.getServer2Connect());
						if(!RMI_OK || FIRST_TIME) 
						{
							model.addLogText("[check_T] connessione RMI disponibile.");
							model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
							model.setDisconnectBenabled(true);					
							RMI_OK = true;
						}
						
						//RMI ok ed anche il server e' iscritto, ma controlliamo se risponde all'heartbeat!
						try{
							if(ref.heartbeat().equals(IServer.HEARTBEAT_ANSWER))
							{
								if(!SERVER_OK || FIRST_TIME)
								{
									model.addLogText("[check_T] il server " + model.getServer2Connect() + " e' online!");
									SERVER_OK = connectToServer();				//mi connetto al server in automatico
								}
							}
						}catch(Exception ee){
							if(SERVER_OK || FIRST_TIME)
							{
								model.setLogColor(Color.RED); 
								model.addLogText("[check_T] il server " + model.getServer2Connect() + " e' offline.");
								if(model.getDisconnectBtext().equals(DISCONNECT_BUTTON_TEXT) ||  //se ero connesso
								   model.getDisconnectBtext().equals(CONNECTION_BUTTON_TEXT) )	 //oppure in fase di connessione
									model.addLogText("[check_T] connessione al server " + model.getServer2Connect() + " terminata!");
								model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
								model.setDisconnectBenabled(true);
								model.setFindBenabled(false);						//disabilito la ricerca
								SERVER_OK = false; 
							}
						}
						
					}catch(NotBoundException e){ 								//non ho trovato il server nel registro RMI
						model.setLogColor(Color.RED); 							//segnalo l'errore con caratteri rossi
						if(!RMI_OK || FIRST_TIME) 
						{
							model.addLogText("[check_T] connessione RMI OK.");
							model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
							model.setDisconnectBenabled(true);
							RMI_OK = true;
						}
						if(SERVER_OK || FIRST_TIME)
						{
							model.addLogText("[check_T] il server " + model.getServer2Connect() + " e' offline.");
							if(model.getDisconnectBtext().equals(DISCONNECT_BUTTON_TEXT) ||  //se ero connesso
							   model.getDisconnectBtext().equals(CONNECTION_BUTTON_TEXT) )	 //oppure in fase di connessione
								model.addLogText("[check_T] connessione al server " + model.getServer2Connect() + " terminata!");
							model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
							model.setDisconnectBenabled(true);
							model.setFindBenabled(false);						//disabilito la ricerca
							SERVER_OK = false; 
						}
					}catch(Exception e){ 										//registro RMI irreperibile
						model.setLogColor(Color.RED); 							//segnalo l'errore con caratteri rossi
						if(RMI_OK || FIRST_TIME)
						{
							model.addLogText("[check_T] connessione RMI non riuscita.");
							if(model.getDisconnectBtext().equals(DISCONNECT_BUTTON_TEXT) ||  //se ero connesso
							   model.getDisconnectBtext().equals(CONNECTION_BUTTON_TEXT) )	 //oppure in fase di connessione
								model.addLogText("[check_T] connessione al server " + model.getServer2Connect() + " terminata!");
							model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
							model.setDisconnectBenabled(true);
							model.setFindBenabled(false);						//disabilito la ricerca
							SERVER_OK = false; 
							RMI_OK = false;							
						}
					}	

					if(FIRST_TIME)FIRST_TIME = false; //non e' piu' la prima volta :(
										
				}//end while( !Thread.currentThread().isInterrupted() )	
			}catch(InterruptedException ie){
				model.addLogText("[check_T] interrupted exception!");
			}
			finally{
				//non dovrebbe mai terminare, quindi se succede lo segnalo
				model.addLogText("[check_T] thread controllo connessioni terminato in modo imprevisto!");
			}			
		}//end run()
		
	}//class CheckConnectionsThread
	
	/****************************************************************************************\
	|	private class RicercaRisorsa
	|	description: thread che gestisce la ricerca di una risorsa
	\****************************************************************************************/
	private class RicercaRisorsaThread extends Thread
	{
		//campi dati
		private Resource risorsaDaCercare;
		private Vector<DeviceClient> listaClient;
		
		public RicercaRisorsaThread(String _nomeRisorsa, int _partiRisorsa)
		{
			super(RICERCARISORSA_THREAD + "_" + _nomeRisorsa + "_" + _partiRisorsa);
			risorsaDaCercare = new Resource(_nomeRisorsa, _partiRisorsa, RESOURCE_EMPTY);
			listaClient = new Vector<DeviceClient>(); //lista di clients che hanno la risorsa 
		}
		
		public void run()
		{
			model.addLogText("[ricerca_T] ricerca risorsa " + risorsaDaCercare.getName() + " " + risorsaDaCercare.getNparts() + " in corso...");	
			
			try{
				IServer ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + model.getServer2Connect());
				listaClient = ref.findResourceForClient(model.getClientName(),risorsaDaCercare);	//richiamo il metodo remoto per inoltrare la richiesta al server
				
				if(VERBOSE_LOG)	//stampo la lista di clients che hanno la risorsa cercata
					for(int i=0; i<listaClient.size(); i++)
						model.addLogText("[ricerca_T] il client " + listaClient.get(i).getName() + " ha la risorsa " + risorsaDaCercare.getName() + " " + risorsaDaCercare.getNparts());	
				
				if(listaClient.size() > 0) //se esiste almeno un client che possiede la risorsa aggiungo in coda download
				{
					//synchronized(model)
					{ 
						model.addResourceToDownloadQueue(risorsaDaCercare, listaClient);
					}				
					model.addLogText("[ricerca_T] risorsa " + risorsaDaCercare.getName() + " " + risorsaDaCercare.getNparts() + " aggiunta in coda download!");	
					view.setFindText(""); //ora che la risorsa e' stata aggiunta svuoto il campo di testo
				}else{
					model.addLogText("[ricerca_T] risorsa " + risorsaDaCercare.getName() + " " + risorsaDaCercare.getNparts() + " non trovata!");
				}
			}catch(Exception e){
				model.addLogText("[ricerca_T] impossibile comunicare con il server!");
			}							
		}//end run()
		
	}//class RicercaRisorsaThread
	
	/****************************************************************************************\
	|	private class DownloadManager
	|	description: thread che gestisce i download, svuota la coda di download	
	\****************************************************************************************/
	private class DownloadManagerThread extends Thread
	{
		private Object lock;
		private int activeDownloads;				//numero di download attivi
		private Vector<DeviceClient> activeClients;	//lista di clients attivi (in upload)
		
		public DownloadManagerThread()
		{
			super(DOWNLOADMANAGER_THREAD);
			lock = new Object();
			activeDownloads = 0;
			activeClients = new Vector<DeviceClient>();
		}
		
		public void run()
		{
			Resource risorsa;
			Vector<DeviceClient> listaClient;
			DeviceClient client;
			
			try{
				while( !Thread.currentThread().isInterrupted() )
				{
				
					sleep(CHECKDOWNLOADQUEUE_TIMEOUT);
					
					//synchronized(model)
					//{ 
					//scorro la lista delle risorse in download per vedere se ci sono parti da scaricare
					for(int i=0; i<model.getNdownloadQueue(); i++)
					{
						//if(Thread.currentThread().isInterrupted())break;
						
						//synchronized(lock)
						{
						risorsa = model.getResourceInDownload(i); 				//prendo la risorsa
						if(risorsa.isFull())
						{
							model.addLogText("[download_T] download risorsa " + risorsa.getName() + " " + risorsa.getNparts() + " terminato!");
							//sposto la nuova risorsa completa sulla lista delle mie risorse
							//aspetto un po' prima di spostare la risorsa
							sleep(DOWNLOAD_TIMEOUT);
							synchronized(model)
							{
								model.addResource(risorsa);
								model.removeResourceInDownload(i);	//rimuovo la risorsa dalla coda download
							}							
							if(VERBOSE_LOG)
								model.addLogText("[download_T] comunico nuova lista risorse al server " + model.getServer2Connect());
							try{
								IServer ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + model.getServer2Connect());
								ref.connectMEClient(model.getClientName(),model.getClientRef());
							}catch(Exception e){
								if(VERBOSE_LOG)
									model.addLogText("[download_T] comunicazione nuova lista fallita!");
							}						
						}else{
							//se la risorsa ha ancora parti da scaricare...
							//prendo la lista di clients che possiedono tale risorsa
							listaClient = model.getDownloadClientListForResource(i);
							int parte;
							int clientPos;
							synchronized(lock)
						    {
							if(activeDownloads < model.getDownloadCapacity() &&
								listaClient.size() > activeClients.size()	)//vedo se posso scaricare
							{
								do{
									clientPos = randInt(0, listaClient.size() - 1);
									client = listaClient.get(clientPos);					//prendo un client random
								}while(activeClients.contains(client));						//che non sia attivo 								
								do{
									parte = randInt(0, risorsa.getNparts() - 1);			//prendo una parte random
								}while(risorsa.isPartFull(parte) || 
									   risorsa.isPartInDownload(parte) );					//esco se la parte e' vuota
								risorsa.setPartInDownload(parte);							//setto come in download
								activeDownloads++;
								activeClients.add(client);
								//avvio un thread di download per la parte di risorsa
								(new DownloadResourcePartThread(risorsa, parte, listaClient, clientPos)).start();						
							}	
							}
							}
						}	//synch(lock)				
					}//end for
					//}//end synchronized(model)
				}//end while( !Thread.currentThread().isInterrupted() )	
			}catch(InterruptedException ie){
				model.addLogText("[download_T] interrupted exception!");
			}
			finally{
				//non dovrebbe mai terminare, quindi se succede lo segnalo
				model.addLogText("[download_T] thread gestione download terminato!");
			}				
		}//end run()
		
		/****************************************************************************************\
		|	private class DownloadResourcePartThread
		|	description: thread che gestisce il download di una parte di risorsa
		\****************************************************************************************/
		private class DownloadResourcePartThread extends Thread
		{
			//campi dati
			private Resource risorsa;
			private int parte;
			private DeviceClient client;
			private Vector<DeviceClient> listaClient;
			private int clientPos;
			
			public DownloadResourcePartThread(Resource _risorsa, int _parte, Vector<DeviceClient> _listaClient, int _clientPos)
			{
				super(DOWNLOADRESOURCEPART_THREAD + "_" + _risorsa.getName() + "." + _parte);
				risorsa = _risorsa;
				parte = _parte;
				listaClient = _listaClient;
				clientPos = _clientPos;
				client = listaClient.get(clientPos);
			}
			
			public void run()
			{
				try{
					
					
					model.addLogText("[downloadP_T] avvio download " + risorsa.getName() + "." + parte + " dal client " + client.getName());
											
					try{
						if(client.getRef().downloadStart(risorsa, parte, model.getMe()))	//richiedo il download
						{
							sleep(DOWNLOAD_TIMEOUT * 3);		//timeout download, spero di essere interrotto prima di finire il tempo					
							model.addLogText("[downloadP_T] timeout! download " + risorsa.getName() + "." + parte + " fallito!");
						}else{
							//il client non ha accettato la richiesta di download
							model.addLogText("[downloadP_T] download " + risorsa.getName() + "." + parte + " fallito!");
						}
								
					}catch(RemoteException re){
						model.addLogText("[downloadP_T] client " + client.getName() + " offline, download " + risorsa.getName() + "." + parte + " interrotto!");
						//il client non e' raggiungibile, quindi lo elimino dalla lista client
						synchronized(lock)
						{
						risorsa.setPartEmpty(parte);	//reimposto la risorsa come vuota
						activeClients.remove(client);
						listaClient.remove(clientPos);
						}
					}		
					
				}catch(InterruptedException ie){
					//model.addLogText("[downloadP_T] interrupted exception!");
					
					//TODO discriminare se l'interruzione è avvenuta a causa di downloadStop oppure a causa di una disconnessione manuale
					model.addLogText("[downloadP_T] download " + risorsa.getName() + "." + parte + " completato!");
					risorsa.setPartFull(parte);				//setto la parte come completata
					activeClients.remove(client);
					activeDownloads--;
				}
				catch(Exception e){
					model.addLogText("[downloadP_T] interrupted exception durante attesa lock!");
				}
				finally{
				
				}
			}//end run()
		}
		
	}//class DownloadManagerThread
	
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
			if(threadList[i].getName().startsWith(RICERCARISORSA_THREAD) ||
			   threadList[i].getName().startsWith(DOWNLOADMANAGER_THREAD) ||
			   threadList[i].getName().startsWith(DOWNLOADRESOURCEPART_THREAD) ||
			   threadList[i].getName().startsWith(UPLOADRESOURCEPART_THREAD) )			
				threadList[i].interrupt();
		}
	}
		
	/****************************************************************************************\
	|	private int randInt(int _min, int _max)
	|	description: restituisce un numero intero random compreso nel range indicato
	\****************************************************************************************/
	private int randInt(int _min, int _max)
	{
		return rand.nextInt((_max - _min) + 1) + _min;
	}
		
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
		//eseguo la disconnessione dal server
		disconnectFromServer();
		System.exit(0); 
	}
	
	/****************************************************************************************\
	|	public void actionPerformed(ActionEvent _e)
	|	description: gestore degli input utenti provenienti dalla componente view
	\****************************************************************************************/
	public void actionPerformed(ActionEvent _e)
	{
		if(_e.getActionCommand().equals(FIND_BUTTON_TEXT))
		{
			String textArea = view.getFindText().toLowerCase() + " "; //recupero la stringa cercata dalla view con sentinella finale
				
			int firstChar = 0;
			while(firstChar < textArea.length()-1 && textArea.charAt(firstChar) == ' ')	//elimino gli spazi vuoti iniziali
				firstChar++;	
			textArea = textArea.substring(firstChar);				
			
			int firstWhiteChar = textArea.indexOf(" ");
			int secondWhiteChar = textArea.indexOf(" ",firstWhiteChar + 1);
			if(firstWhiteChar<=0)
			{
				model.addLogText("[input_err] inserisci nome risorsa!");		//manca il nome della risorsa da cercare
				view.setFindText("");
				return;
			} 	
			
			String nomeRisorsa = textArea.substring(0,firstWhiteChar);		//recupero il nome della risorsa
			
			if(secondWhiteChar<=0)
			{
				model.addLogText("[input_err] nome risorsa incompleto o errato!");	//manca il numero di parti della risorsa da cercare
				view.setFindText(nomeRisorsa);
				return;
			} 

			while(firstWhiteChar < textArea.length()-1 && textArea.charAt(firstWhiteChar) == ' ')	//elimino gli spazi vuoti intermedi
				firstWhiteChar++;	
			secondWhiteChar = textArea.indexOf(" ",firstWhiteChar);
			String partiRisorsaText = textArea.substring(firstWhiteChar, secondWhiteChar); 			//recupero il numero di parti
	
			int partiRisorsa = 0;
			try
			{
				partiRisorsa = Integer.parseInt(partiRisorsaText);	//provo a convertire il numero di parti della risorsa
			}catch(NumberFormatException nfe)
			{
				model.addLogText("[input_err] formato numerico parti risorsa errato!");
				return;
			}
			if(partiRisorsa <= 0)
			{
				model.addLogText("[input_err] impossibile cercare risorse vuote!");
				return;
			}					
			
			view.setFindText(nomeRisorsa + " " + partiRisorsa);	
			
			//ora cerco se ho gia' la risorsa che l'utente sta cercando
			if(model.resourceIsHere(nomeRisorsa,partiRisorsa))
			{
				model.addLogText("[input_err] risorsa gia' presente!");
				return;
			}				
			if(model.resourceIsDownloading(nomeRisorsa,partiRisorsa))
			{
				model.addLogText("[input_err] risorsa gia' in coda download!");
				return;
			}				
					
			//ora che tutto torna creo il thread di ricerca, lo avvio ed esco!
			(new RicercaRisorsaThread(nomeRisorsa,partiRisorsa)).start();
		
		}
		else if(_e.getActionCommand().equals(CONNECT_BUTTON_TEXT))
		{
			connectToServer();
		}
		else if(_e.getActionCommand().equals(DISCONNECT_BUTTON_TEXT))
		{			
			disconnectFromServer();	
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
		}catch(NotBoundException e){
			model.addLogText("il server " + model.getServer2Connect() + " e' offline!");
		}catch(Exception e){
			model.addLogText("problemi con la connessione RMI!");
		}
		return ref;	
	}
	
	/****************************************************************************************\
	|	private boolean connectToServer()
	|	description: si connette al server, restituisce true se la connessione va a buon fine
	\****************************************************************************************/
	private boolean connectToServer()
	{
		boolean CONNECT_OK = false;
				
		synchronized(model.getDisconnectBtext()) //prendo il lock sui dati per modificare i pulsanti
		{
			model.setDisconnectBenabled(false);								//disabilito il pulsante
			model.setDisconnectBtext(CONNECTION_BUTTON_TEXT);				//cambio il testo
			int logPos = model.addLogText("connessione al server " + model.getServer2Connect() + "...");
			
			IServer ref = serverLookup(model.getServer2Connect());			//recupero il riferimento del server
				
			if(ref != null)
			{
				try{
					if(ref.connectMEClient(model.getClientName(),model.getClientRef()))
					{
						model.setDisconnectBtext(DISCONNECT_BUTTON_TEXT);	//permetto la disconnessione
						model.setFindBenabled(true);						//abilito la ricerca
						model.setLogColor(Color.BLUE);						//testo LOG in blu
						model.addLogTextToLine(logPos," completata!");
						view.setFindText("");								//resetto la barra di ricerca
						downloadManager = new DownloadManagerThread();		//creo ed avvio un nuovo thread di download
						downloadManager.start();						
						CONNECT_OK = true;
					}else{
						model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
						model.addLogText(model.getServer2Connect() + " NON ha accettato la richiesta di connessione.");
					}
				}catch(Exception e){
					model.addLogTextToLine(logPos," fallita!");
					model.setDisconnectBtext(CONNECT_BUTTON_TEXT);			//permetto la connessione
				}
			}else{
				model.setDisconnectBtext(CONNECT_BUTTON_TEXT);				//permetto la connessione
			}
			try{Thread.sleep(100);}catch(Exception exc){}					//antibounce 100ms
			model.setDisconnectBenabled(true);								//abilito il pulsante
		}//end synchronized(model)
		return CONNECT_OK;
	}
	
	/****************************************************************************************\
	|	private boolean disconnectFromServer()
	|	description: si disconnette dal server, restituisce true se la disconnessione non ha 
	|				 problemi.
	\****************************************************************************************/
	private boolean disconnectFromServer()
	{
		if(model.getDisconnectBtext().equals(CONNECT_BUTTON_TEXT))return true;	//evito di disconnettermi se non sono connesso
		 
		boolean DISCONNECT_OK = false;
		
		//TERMINO I THREAD DI DOWNLOAD ed EVENTUALE RICERCA
		model.addLogText("terminazione threads download..."); 
		killAllDownloadThreads();
		model.addLogText("richiesta terminazione threads downloads inviata.");
								
		synchronized(model.getDisconnectBtext()) //prendo il lock sui dati per modificare i pulsanti
		{
		
			model.setDisconnectBenabled(false);									//disabilito il pulsante
			int logPos = model.addLogText("disconnessione dal server " + model.getServer2Connect() + "...");
			
			IServer ref = serverLookup(model.getServer2Connect());			//recupero il riferimento del server
		
			if(ref != null)
			{
				try{
					if(ref.disconnectMEClient(model.getClientName()))
					{
						model.addLogTextToLine(logPos," completata!");
						DISCONNECT_OK = true;
					}else{
						model.addLogText(model.getServer2Connect() + " NON ha accettato la richiesta di disconnessione.");
					}
				}catch(Exception e){
					model.addLogTextToLine(logPos," fallita!");				//non mi sono disconnesso, quindi 
				}
			}
			
			try{Thread.sleep(100);}catch(Exception exc){}					//antibounce 100ms
			
			//sia che la disconnessione vada a buon fine che non, permetto la connessione per ripartire...
			model.setLogColor(Color.RED);									//testo LOG in rosso
			model.setDisconnectBtext(CONNECT_BUTTON_TEXT);					//permetto la connessione
			model.setDisconnectBenabled(true);								//abilito il pulsante
			model.setFindBenabled(false);									//disabilito la ricerca
		}// end synchronized(model)
		
		return DISCONNECT_OK;
	}
	
	/****************************************************************************************\
	|	public void addModel(ClientModel _model)
	|	description: setta il riferimento alla componente model
	\****************************************************************************************/
	public void addModel(ClientModel _model){model = _model;}

	/****************************************************************************************\
	|	public void addView(ClientView _view)
	|	description: setta il riferimento alla componente view
	\****************************************************************************************/
	public void addView(ClientView _view){view = _view;}

	/****************************************************************************************\
	|	public void initModel(String _nomeClient, String _server2connect, int _D, String[] _R)
	|	description: inizializza la componente model all'avvio del client
	\****************************************************************************************/
	public void initModel(String _nomeClient, String _server2connect, int _D, String[] _R)
	{
		model.setClientName(_nomeClient);
		model.setClientRef(this);
		model.setServer2Connect(_server2connect);
		model.setDownloadCapacity(_D);
		model.setLogColor(Color.RED);
		model.setDisconnectBtext(CONNECT_BUTTON_TEXT);
		model.setDisconnectBenabled(false);
		model.setFindBenabled(false);
		model.addLogText("[Client " + _nomeClient + "] Log di sistema:");

		//carico le risorse
		model.addLogText("controllo risorse locali...");		
		int partiRisorsa;
		Resource nuovaRisorsa;
		for(int i=0; i<_R.length; i+=2)
		{
			try
			{
				partiRisorsa = Integer.parseInt(_R[i+1]);	//provo a convertire il numero di parti della risorsa
			}catch(NumberFormatException nfe)
			{
				continue;									//se non e' un valore numerico non la considero				
			}
			if(partiRisorsa==0){continue;}					//risorse con zero parti non le considero
			
			//se la risorsa va bene e non e' gia' presente, la aggiungo come piena
			nuovaRisorsa = new Resource(_R[i].toLowerCase(), partiRisorsa, RESOURCE_FULL);
			
			if(!model.resourceIsHere(_R[i].toLowerCase(),partiRisorsa))
			{
				model.addResource(nuovaRisorsa);
			}else{
				model.addLogText("impossibile inserire risorse uguali!");
				model.addLogText("inserita una sola occorrenza di " + nuovaRisorsa.getName() + " " + nuovaRisorsa.getNparts() + ".");
			}
		}
		
	}
	
	/****************************************************************************************\
	|	public String heartbeat() 
	|	description: implementazione del metodo remoto dell'interfaccia IClient
	\****************************************************************************************/
	public String heartbeat() throws RemoteException
	{
		return HEARTBEAT_ANSWER;	//rispondo
	}
	
	/****************************************************************************************\
	|	public Vector<Resource> getResourceList()
	|	description: implementazione del metodo remoto dell'interfaccia IClient
	\****************************************************************************************/
	public Vector<Resource> getResourceList() throws RemoteException
	{
		synchronized(model.getResourceList())
		{
			return model.getResourceList();		
		}
	}
	
	/****************************************************************************************\
	|	public boolean downloadStart(final Resource _risorsa, final int _parte, final DeviceClient _client)
	|	description: implementazione del metodo remoto dell'interfaccia IClient
	\****************************************************************************************/
	public boolean downloadStart(final Resource _risorsa,final int _parte, final DeviceClient _client) throws RemoteException
	{
		(new Thread(UPLOADRESOURCEPART_THREAD + "_" + _risorsa + "." + _parte){
			public void run()
			{
				try
				{
					model.addLogText("[upload_T] il client " + _client.getName() + " richiede risorsa " + _risorsa.getName() + "." + _parte + "...");
					sleep(DOWNLOAD_TIMEOUT);									//simulo l'upload
					if(_client.getRef().downloadStop(_risorsa,_parte,_client))
					{
						//aggiorno la mia lista risorse riempiendo il log download di questa
						//determinata parte che ho scaricato
						model.addLogDownload(_client, _risorsa, _parte);
						model.addLogText("[upload_T] upload " + _risorsa.getName() + "." + _parte + " al client " + _client.getName() + " completato!");
					}else{
						model.addLogText("[upload_T] impossibile completare l'upload " + _risorsa.getName() + "." + _parte + " al client " + _client.getName() + "!");
					}
				}catch(InterruptedException ie){
					model.addLogText("[upload_T] upload " + _risorsa.getName() + "." + _parte + " al client " + _client.getName() + " interrotto!");
				}catch(RemoteException re){
					model.addLogText("[upload_T] upload interrotto, impossibile contattare il client " + _client.getName() + "!");
				}
				finally{
					
				}			
			}// end run()
		}).start();
		return true;
	}
	
	/****************************************************************************************\
	|	public boolean downloadStop(final Resource _risorsa, final int _parte, final DeviceClient _client) 
	|	description: implementazione del metodo remoto dell'interfaccia IClient
	\****************************************************************************************/
	public boolean downloadStop(final Resource _risorsa,final int _parte, final DeviceClient _client) throws RemoteException
	{
		model.addLogText("[downloadP_T] download in terminazione...");
		killThread(DOWNLOADRESOURCEPART_THREAD + "_" + _risorsa.getName() + "." + _parte);
		return true;
	}	

}//end class ClientController()
