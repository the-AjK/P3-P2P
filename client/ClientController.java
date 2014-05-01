/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ClientController.java
|	Description: componente controller del pattern MVC
|	Package: client
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - 
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
	
	//impostazioni NON modificabili
	private static final String RMITAG = "P3-P2P-JK"; 						//chiave identificativa per il registro RMI
	private static final String CONNECT_BUTTON_TEXT    = "Connetti";		//testo del pulsante di disconnessione
	private static final String DISCONNECT_BUTTON_TEXT = "Disconnetti";	
	private static final String CONNECTION_BUTTON_TEXT = "in connessione...";
	private static final String FIND_BUTTON_TEXT = "Cerca e Scarica";

	//riferimenti alle componenti View e Model
	private ClientView view;
	private ClientModel model;
	
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
		connectToServer();								//mi collego al server
		//threadInit();									//inizializzo i thread che girano in background
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
	|	public void actionPerformed(java.awt.event.ActionEvent _e)
	|	description: gestore degli input utenti provenienti dalla componente view
	\****************************************************************************************/
	public void actionPerformed(java.awt.event.ActionEvent _e)
	{
		switch(_e.getActionCommand())
		{
			case FIND_BUTTON_TEXT:
				System.out.println("cerca" + view.getFindText());
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
			model.addLogText("il server " + model.getServer2Connect() + " � offline!");
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
		model.setDisconnectBenabled(false);								//disabilito il pulsante
		model.setDisconnectBtext(CONNECTION_BUTTON_TEXT);				//cambio il testo
		model.addLogText("connessione al server " + model.getServer2Connect() + "...");
		
		IServer ref = serverLookup(model.getServer2Connect());			//recupero il riferimento del server
	
		if(ref != null)
		{
			try{
				if(ref.connectMEClient(model.getClientName(),model.getClientRef()))
				{
					model.setDisconnectBtext(DISCONNECT_BUTTON_TEXT);	//permetto la disconnessione
					model.setFindBenabled(true);						//abilito la ricerca
					model.setLogColor(Color.BLUE);						//testo LOG in blu
					model.addLogText("connessione al server " + model.getServer2Connect() + " completata!");
					view.setFindText("");								//resetto la barra di ricerca
					try{Thread.sleep(1000);}catch(Exception exc){}		//aspetto un secondo
				}else{
					model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
					model.addLogText("il server " + model.getServer2Connect() + " NON ha accettato la richiesta di connessione.");
				}
			}catch(Exception e){
				model.addLogText("connessione al server " + model.getServer2Connect() + " fallita!");
			}
		}else{
			model.setDisconnectBtext(CONNECT_BUTTON_TEXT);				//permetto la connessione
		}
		model.setDisconnectBenabled(true);								//abilito il pulsante
	}
	
	/****************************************************************************************\
	|	private void disconnectFromServer()
	|	description: si disconnette dal server
	\****************************************************************************************/
	private void disconnectFromServer()
	{
		model.setDisconnectBenabled(false);								//disabilito il pulsante
		model.addLogText("disconnessione dal server " + model.getServer2Connect() + "...");
		
		IServer ref = serverLookup(model.getServer2Connect());			//recupero il riferimento del server
	
		if(ref != null)
		{
			try{
				if(ref.disconnectMEClient(model.getClientName()))
				{
					model.setDisconnectBtext(CONNECT_BUTTON_TEXT);		//permetto la connessione
					model.setFindBenabled(false);						//disabilito la ricerca
					model.setLogColor(Color.RED);						//testo LOG in rosso
					model.addLogText("disconnessione al server " + model.getServer2Connect() + " completata!");
					try{Thread.sleep(1000);}catch(Exception exc){}		//aspetto un secondo
				}else{
					model.addLogText("il server " + model.getServer2Connect() + " NON ha accettato la richiesta di disconnessione.");
				}
			}catch(Exception e){
				model.addLogText("connessione al server " + model.getServer2Connect() + " fallita!");
			}
		}
		model.setDisconnectBenabled(true);								//abilito il pulsante

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
				continue;									//se non � un valore numerico non la considero
			}
			if(partiRisorsa==0){continue;}					//risorse con zero parti non le considero
			
			//se la risorsa va bene, la aggiungo 
			model.addResource(new Resource(_R[i],partiRisorsa));
		}
				
		model.setLogColor(Color.RED);
		model.setDisconnectBtext(CONNECT_BUTTON_TEXT);
		model.setDisconnectBenabled(false);
		model.setFindBenabled(false);
		model.addLogText("inizializzazione completata.");
		model.addLogText("connessione automatica avviata...");
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
		synchronized(model)
		{
			return model.getResourceList();		
		}
	}

}//end class ClientController()
