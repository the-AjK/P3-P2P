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
//import java.util.*; 		//vector
//import java.io.*; 			//IO exceptions
//import java.net.MalformedURLException;	//malformedURLexception

//import client.IClient;

public class ClientController extends UnicastRemoteObject implements IClient, java.awt.event.ActionListener
{
	private static final String HOST = "localhost:1099";					//host per la connessione RMI
	private static final String RMITAG = "P3-P2P-JK"; 						//chiave identificativa per il registro RMI
	private static final String CONNECT_BUTTON_TEXT    = "Connetti";		//testo del pulsante di disconnessione
	private static final String DISCONNECT_BUTTON_TEXT = "Disconnetti";	
	private static final String CONNECTION_BUTTON_TEXT = "in connessione...";
	private static final String FIND_BUTTON_TEXT = "Cerca e Scarica";

	
	//riferimenti alle componenti View e Model
	private ClientView view;
	private ClientModel model;
	
	//costruttore
	public ClientController() throws Exception 
	{
		
		
	}
	
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		/*System.out.println ("Controller Client: The " + e.getActionCommand() 
			+ " button is clicked at " + new java.util.Date(e.getWhen())
			+ " with e.paramString " + e.paramString() );
			
		//qui ricevo una action event e, devo disciminarla e scegliere che operazione fare
		/*
		e.getActionCommand() = Cerca e Scarica
		new java.util.Date(e.getWhen()) = Wed Apr 30 09:07:58	CEST 2014 
		e.paramString() = ACTION_PERFORMED,cmd=Cerca e Scarica,when=1398841678102,modifiers=Button1
		*/
		
		switch(e.getActionCommand())
		{
			case FIND_BUTTON_TEXT:
				System.out.println("cerca" + view.getFindText());
				break;
				
			case CONNECT_BUTTON_TEXT:
				model.setDisconnectBtext(DISCONNECT_BUTTON_TEXT);
				break;
				
			case DISCONNECT_BUTTON_TEXT:
				model.setDisconnectBtext(CONNECT_BUTTON_TEXT);
				break;		
		}
	}
	
	//metodi per impostare i riferimenti al model ed alla view
	public void addModel(ClientModel _model)
	{
		this.model = _model;
	}

	public void addView(ClientView _view)
	{
		this.view = _view;
	}

	public void initModel(String _nomeClient, String _server2connect, int _D, String[] _R)
	{
		model.setClientName(_nomeClient);
		model.setServer2Connect(_server2connect);
		model.setDownloadCapacity(_D);
		//model.setResources(_R);		
		
		model.setLogColor(Color.RED);
		model.setDisconnectBtext(CONNECT_BUTTON_TEXT);
		model.setDisconnectBenabled(true);
		model.setFindBenabled(true);
		model.addLogText("inizializzazione completata.");
		model.addLogText("connessione automatica avviata...");
	}

}//end class ClientController()