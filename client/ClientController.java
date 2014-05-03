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
	Thread checkConnections;								//thread che controlla la connessione con RMI ed il server
	
	
	/****************************************************************************************\
	|	public ClientController() 
	|	description: costruttore
	\****************************************************************************************/
	public ClientController() throws Exception 
	{
		
	}
	
	/****************************************************************************************\
	|	public void clientInit()
	|	description: inizializza il client 
	\****************************************************************************************/
	public void clientInit()
	{
		//inizio le operazioni di avvio del client...
		threadInit();									//inizializzo i thread che girano in background
	}
	
	/****************************************************************************************\
	|	private void threadInit()
	|	description: inizializza i thread del server e li avvia
	\****************************************************************************************/
	private void threadInit() 
	{
		//thread con ciclo continuo che controlla le connessioni con RMI ed il server
		checkConnections = new Thread(){ 
			public void run()
			{
				boolean RMI_OK = true;		//FLAG che indica lo stato della connessione al registro RMI
				boolean SERVER_OK = true;	//FLAG status del mio server
				IServer ref;				//riferimento remoto del mio server
				String[] animIcon = {"PSP","P2P"};
				model.addLogText("[check_thread] connessione automatica avviata...");
				model.setDisconnectBtext(CONNECTION_BUTTON_TEXT);
				
				//controllo lo stato del server
				try{
					ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + model.getServer2Connect());
				}catch(NotBoundException e){
					SERVER_OK = false; //non ho trovato il server nel registro RMI
					RMI_OK = true;
				}catch(Exception e){
					SERVER_OK = false; //non ho trovato il server nel registro RMI
					RMI_OK = false;
				}
				
				while(true)
				{
					//animazione durante l'attesa, lampeggio veloce del titolo
					for(int i=0; i<animIcon.length; i++)
					{
						try{sleep(100);}catch(InterruptedException ie){}					
						model.setAnimIcon(animIcon[i]);							
					}
					try{sleep(CHECKCONNECTIONS_TIMEOUT);}catch(InterruptedException ie){}	
					
					String[] serverNamesList = {};
					try{
						serverNamesList = Naming.list("//" + HOST );
						if(!RMI_OK)
						{
							model.setLogColor(Color.BLUE);
							model.addLogText("[check_thread] connessione RMI OK.");
							connectToServer();			//mi connetto al server
							RMI_OK = true;
						}
					}
					catch(Exception e)
					{
						if(RMI_OK)
						{
							if(model.getDisconnectBtext().equals(DISCONNECT_BUTTON_TEXT))//se ero connesso
							{
								model.setLogColor(Color.RED);
								model.addLogText("[check_thread] connessione RMI non riuscita.");
								int logPos = model.addLogText("disconnessione dal server " + model.getServer2Connect() + "...");
								model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
								model.setDisconnectBenabled(true);	
								model.setFindBenabled(false);						//disabilito la ricerca
								model.addLogTextToLine(logPos," completata!");
								RMI_OK = false;
							}else{
								model.setLogColor(Color.RED);
								model.addLogText("[check_thread] connessione RMI non riuscita.");
								model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
								model.setDisconnectBenabled(true);	
								model.setFindBenabled(false);						//disabilito la ricerca
								RMI_OK = false;
							}
						}else{
							if(model.getDisconnectBtext().equals(CONNECTION_BUTTON_TEXT))//se ero in connessione all'avvio del server
							{
								model.setLogColor(Color.RED);
								model.addLogText("[check_thread] connessione RMI non riuscita.");
								model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
								model.setDisconnectBenabled(true);	
								model.setFindBenabled(false);						//disabilito la ricerca
								RMI_OK = false;
							}
						}
						
					}
							
					//controllo se il mio server è online (solo se RMI è OK)
					if(RMI_OK)
					{
						try{
							ref = (IServer) Naming.lookup("rmi://" + HOST + "/" + RMITAG + "/" + model.getServer2Connect());
							ref.heartbeat();											//controllo se il server è vivo
							if(!SERVER_OK && ref != null)
							{
								model.setLogColor(Color.BLUE);
								model.addLogText("[check_thread] il server " + model.getServer2Connect() + " è online!");
								connectToServer();		 //mi connetto al server
								SERVER_OK = true;
							}
						}catch(Exception e){
							if(SERVER_OK)
							{
								model.setLogColor(Color.RED);
								model.addLogText("[check_thread] il server " + model.getServer2Connect() + " è offline.");
								int logPos = model.addLogText("disconnessione dal server " + model.getServer2Connect() + "...");
								model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
								model.setFindBenabled(false);						//disabilito la ricerca
								model.addLogTextToLine(logPos," completata!");
								SERVER_OK = false;
							}
						}	
					}			
					
				}//end while(1)
			}// end run()
		};
		checkConnections.start();	//avvio il thread
		
	} //end threadInit()
	
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
	|	public void actionPerformed(java.awt.event.ActionEvent _e)
	|	description: gestore degli input utenti provenienti dalla componente view
	\****************************************************************************************/
	public void actionPerformed(java.awt.event.ActionEvent _e)
	{
		switch(_e.getActionCommand())
		{
			case FIND_BUTTON_TEXT:
				System.out.println("cerca" + view.getFindText());
				synchronized(model)
				{
					model.addResourceToDownloadQueue(new Resource("J",8));
				}
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
	|	private void connectToServer()
	|	description: si connette al server
	\****************************************************************************************/
	private void connectToServer()
	{
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
	}
	
	/****************************************************************************************\
	|	private void disconnectFromServer()
	|	description: si disconnette dal server
	\****************************************************************************************/
	private void disconnectFromServer()
	{
		if(model.getDisconnectBtext().equals(CONNECT_BUTTON_TEXT))return;	//evito di disconnettermi se non sono connesso
		
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
						model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
						model.setFindBenabled(false);						//disabilito la ricerca
						model.setLogColor(Color.RED);						//testo LOG in rosso
						model.addLogTextToLine(logPos," completata!");
					}else{
						model.addLogText(model.getServer2Connect() + " NON ha accettato la richiesta di disconnessione.");
					}
				}catch(Exception e){
					model.addLogTextToLine(logPos," fallita!");
				}
			}
			try{Thread.sleep(100);}catch(Exception exc){}					//antibounce 100ms
			model.setDisconnectBenabled(true);								//abilito il pulsante
		}// end synchronized(model)

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
				partiRisorsa=Integer.parseInt(_R[i+1]);		//provo a convertire il numero di parti della risorsa
			}catch(NumberFormatException nfe)
			{
				continue;									//se non è un valore numerico non la considero
			}
			if(partiRisorsa==0){continue;}					//risorse con zero parti non le considero
			
			//se la risorsa va bene, la aggiungo 
			model.addResource(new Resource(_R[i],partiRisorsa));
		}
				
		model.setLogColor(Color.RED);
		model.setDisconnectBtext(CONNECT_BUTTON_TEXT);
		model.setDisconnectBenabled(false);
		model.setFindBenabled(false);
		model.addLogText("[Client " + _nomeClient + "] Log di sistema:");
		model.addLogText("inizializzazione completata.");
		
	}
	
	/****************************************************************************************\
	|	public String heartbeat() 
	|	description: implementazione del metodo remoto dell'interfaccia IClient
	\****************************************************************************************/
	public String heartbeat() throws RemoteException
	{
		return "JK-CLIENT-OK";	//restituisco la stringa JK per confermare che sono online
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
