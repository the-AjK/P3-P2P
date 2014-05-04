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
|
\****************************************************************************************/
package client;

import java.rmi.*;
import java.rmi.server.*;
import java.awt.event.*;
import java.util.Vector; 
import java.awt.Color;
import common.Resource;
import server.IServer;

public class ClientController extends UnicastRemoteObject implements IClient, ActionListener, WindowListener
{
	//impostazioni modificabili
	private static final String HOST = "localhost:1099";					//host per la connessione RMI
	private static final int DOWNLOAD_TIMEOUT = 3000;						//tempo per simulare il download di una parte di risorsa
	private static final boolean VERBOSE_LOG = true;						//se true visualizza più messaggi di log
	
	
	//impostazioni NON modificabili
	private static final int CHECKCONNECTIONS_TIMEOUT = 3000;				//timeout per controllo connessione in background
	private static final String RMITAG = "P3-P2P-JK"; 						//chiave identificativa per il registro RMI
	private static final String CONNECT_BUTTON_TEXT    = "Connetti";		//testo del pulsante di disconnessione
	private static final String DISCONNECT_BUTTON_TEXT = "Disconnetti";	
	private static final String CONNECTION_BUTTON_TEXT = "in connessione...";
	private static final String FIND_BUTTON_TEXT = "Cerca e Scarica";

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
		downloadManager = new DownloadManagerThread();
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
		downloadManager.start();
	}
	
	/****************************************************************************************\
	|	private class checkConnections
	|	description: thread che controlla la connessione con RMI ed il server
	\****************************************************************************************/
	private class CheckConnectionsThread extends Thread
	{
		public void run()
		{
			boolean FIRST_TIME = true;	//FLAG attivo solamente al primo avvio del client
			boolean RMI_OK = false;		//FLAG che indica lo stato della connessione al registro RMI
			boolean SERVER_OK = false;	//FLAG status del mio server
			IServer ref;				//riferimento remoto del mio server
			String[] animIcon = {"PSP","P2P"};
			model.addLogText("[check_T] connessione automatica avviata...");
			model.setDisconnectBtext(CONNECTION_BUTTON_TEXT);				
							
			while(true)
			{
				//animazione durante l'attesa, lampeggio veloce del titolo
				for(int i=0; i<animIcon.length; i++)
				{
					try{sleep(100);}catch(InterruptedException ie){}					
					model.setAnimIcon(animIcon[i]);							
				}
				try{sleep(CHECKCONNECTIONS_TIMEOUT);}catch(InterruptedException ie){}	
				
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
					
					//RMI ok ed anche il server è iscritto, ma controlliamo se risponde all'heartbeat!
					try{
						if(ref.heartbeat().equals(IServer.HEARTBEAT_ANSWER))
						{
							if(!SERVER_OK || FIRST_TIME)
							{
								model.addLogText("[check_T] il server " + model.getServer2Connect() + " è online!");
								SERVER_OK = connectToServer();				//mi connetto al server in automatico
							}
						}
					}catch(Exception ee){
						if(SERVER_OK || FIRST_TIME)
						{
							model.setLogColor(Color.RED); 
							model.addLogText("[check_T] il server " + model.getServer2Connect() + " è offline.");
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
						model.addLogText("[check_T] il server " + model.getServer2Connect() + " è offline.");
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

				if(FIRST_TIME)FIRST_TIME = false; //non è più la prima volta :(
				
			}//end while(1)			
		}//end run()
	}//class CheckConnectionsThread
	
	/****************************************************************************************\
	|	private class RicercaRisorse
	|	description: thread che gestisce la ricerca delle risorse
	\****************************************************************************************/
	private class RicercaRisorseThread extends Thread
	{
		public void run()
		{
			
			
			
		}//end run()
		
	}//class RicercaRisorseThread
	
	/****************************************************************************************\
	|	private class DownloadManager
	|	description: thread che gestisce i download, svuota la coda di download	
	\****************************************************************************************/
	private class DownloadManagerThread extends Thread
	{
		public void run()
		{
			
			
			
		}//end run()
		
	}//class DownloadManagerThread
		
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
		switch(_e.getActionCommand())
		{
			case FIND_BUTTON_TEXT:
				String textArea = view.getFindText().toLowerCase() + " "; //recupero la stringa cercata dalla view con sentinella finale
				
				int firstChar = 0;
				while(firstChar < textArea.length()-1 && textArea.charAt(firstChar) == ' ')	//elimino gli spazi vuoti iniziali
					firstChar++;	
				textArea = textArea.substring(firstChar);				
				
				int firstWhiteChar = textArea.indexOf(" ");
				int secondWhiteChar = textArea.indexOf(" ",firstWhiteChar + 1);
				if(firstWhiteChar<=0)
				{
					model.addLogText("[ricerca] inserisci nome risorsa!");		//manca il nome della risorsa da cercare
					view.setFindText("");
					break;
				} 	
				
				String nomeRisorsa = textArea.substring(0,firstWhiteChar);		//recupero il nome della risorsa
				
				if(secondWhiteChar<=0)
				{
					model.addLogText("[ricerca] nome risorsa incompleto o errato!");	//manca il numero di parti della risorsa da cercare
					view.setFindText(nomeRisorsa);
					break;
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
					model.addLogText("[ricerca] formato numerico parti risorsa errato!");
					break;
				}
				if(partiRisorsa <= 0)
				{
					model.addLogText("[ricerca] impossibile cercare risorse vuote!");
					break;
				}					
				
				view.setFindText(nomeRisorsa + " " + partiRisorsa);	
				
				//ora cerco se ho già la risorsa che l'utente sta cercando
				if(model.resourceIsHere(nomeRisorsa,partiRisorsa))
				{
					model.addLogText("[ricerca] risorsa già presente!");
					break;
				}				
				if(model.resourceIsDownloading(nomeRisorsa,partiRisorsa))
				{
					model.addLogText("[ricerca] risorsa già in coda download!");
					break;
				}				
						
				model.addLogText("[ricerca] ricerca risorsa " + nomeRisorsa + " " + partiRisorsa + " in corso...");	
				
				//TODO lancio il thread ricerca che aggiungera la risorsa nella lista di download in caso di risposta positiva.
				
				(new RicercaRisorseThread()).start();
				
				synchronized(model)
				{
					model.addResourceToDownloadQueue(new Resource(nomeRisorsa,partiRisorsa,false));
				}
				
				view.setFindText(""); //ora che la risorsa è stata aggiunta svuoto il campo di testo
				break;
				
			case CONNECT_BUTTON_TEXT:
				connectToServer();
				break;
				
			case DISCONNECT_BUTTON_TEXT:
				disconnectFromServer();
				break;		
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
			model.addLogText("il server " + model.getServer2Connect() + " è offline!");
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

		//carico le risorse
		int partiRisorsa;
		for(int i=0; i<_R.length; i+=2)
		{
			try
			{
				partiRisorsa = Integer.parseInt(_R[i+1]);	//provo a convertire il numero di parti della risorsa
			}catch(NumberFormatException nfe)
			{
				continue;									//se non è un valore numerico non la considero				
			}
			if(partiRisorsa==0){continue;}					//risorse con zero parti non le considero
			
			//se la risorsa va bene, la aggiungo come piena (true)
			model.addResource(new Resource(_R[i].toLowerCase(),partiRisorsa,true));
		}
				
		model.setLogColor(Color.RED);
		model.setDisconnectBtext(CONNECT_BUTTON_TEXT);
		model.setDisconnectBenabled(false);
		model.setFindBenabled(false);
		model.addLogText("[Client " + _nomeClient + "] Log di sistema:");
		
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

}//end class ClientController()
