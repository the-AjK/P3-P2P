/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ClientModel.java
|	Description: componente model del pattern MVC
|	Package: client
|	Version: 0.1 - creazione struttura scheletro
|			 1.0 - aggiunti campi dati e relativi metodi set/get
|
\****************************************************************************************/
package client;

import java.util.Vector; 		
import java.awt.Color;
import common.DeviceClient;
import common.Resource;

public class ClientModel extends java.util.Observable 
{
	//campi dati
	private DeviceClient me;				//contiene nomeClient, riferimento e lista risorse
	private String server2connect;			//nome del server a cui connettersi
	private int downloadCapacity;			//capacità di download
	private Vector<String> log;				//log di sistema
	private Color coloreLog;				//colore testo della casella log
	private boolean findBenabled;			//flag che indica se il pulsante di ricerca è abilitato oppure no
	private boolean disconnectBenabled;		//flag che indica se il pulsante di disconnessione è abilitato oppure no
	private String disconnectBtext;			//testo del pulsante di disconnessione
	private Vector<Resource> downloadQueue;	//lista di risorse in fase di download
	
	/****************************************************************************************\
	|	public ClientModel() 
	|	description: costruttore
	\****************************************************************************************/
	public ClientModel()
	{	
		//inizializzo i campi dati
		me = new DeviceClient(); //creo me stesso
		server2connect = "";
		downloadCapacity = 0;
		downloadQueue = new Vector<Resource>();
		log = new Vector<String>();
		log.add("P3-P2P Client (C) JK");
	}
	
	/****************************************************************************************\
	|	public void viewRefresh()
	|	description: notifica la parte view in modalità "model-pull"
	\****************************************************************************************/
	private void viewRefresh()
	{
		setChanged();
		notifyObservers();	//model pull
	}

	/****************************************************************************************\
	|	public IClient getClientRef()
	|	description: restituisce il riferimento del client
	\****************************************************************************************/
	public IClient getClientRef(){return me.getRef();}
	
	/****************************************************************************************\
	|	public void setClientRef(IClient _ref)
	|	description: setta il riferimento del client
	\****************************************************************************************/
	public void setClientRef(IClient _ref){me.setRef(_ref);}
	
	/****************************************************************************************\
	|	public String getClientName()
	|	description: restituisce il nome del client
	\****************************************************************************************/
	public String getClientName(){return me.getName();}
		
	/****************************************************************************************\
	|	public void setClientName(String _nome)
	|	description: setta il nome del client
	\****************************************************************************************/
	public void setClientName(String _nome)
	{
		me.setName(_nome);
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public String getDownloadQueueText()
	|	description: restituisce la lista di risorse in download in formato stringa per la GUI
	\****************************************************************************************/
	public String getDownloadQueueText()
	{
		String res = "";
		for(int i=0; i<downloadQueue.size(); i++)
		{
			res = res + downloadQueue.get(i) + "\n";
		}
		return res;
	}
	
	
	/****************************************************************************************\
	|	public String getResourceListText()
	|	description: restituisce la lista di risorse del client in formato stringa per la GUI
	\****************************************************************************************/
	public String getResourceListText()
	{	
		String res = "";
		Integer nparti;
		for(int i=0; i<me.getNresource(); i++)
		{
			nparti = me.getResource(i).getNparts();
			res = res + me.getResource(i).getName() + " " + nparti.toString() + "\n";
		}
		return res;
	}	
	
	/****************************************************************************************\
	|	public Vector<Resource> getResourceList()
	|	description: restituisce la lista di risorse del client
	\****************************************************************************************/
	public Vector<Resource> getResourceList(){return me.getResourceList();}
	
	/****************************************************************************************\
	|	public void addResource(Resource _risorsa)
	|	description: aggiunge una risorsa alla lista di risorse del client
	\****************************************************************************************/
	public void addResource(Resource _risorsa)
	{
		me.addResource(_risorsa);
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public String getServer2Connect()
	|	description: restituisce il nome del server a cui il client deve connettersi
	\****************************************************************************************/
	public String getServer2Connect(){return server2connect;}
	
	/****************************************************************************************\
	|	public void setServer2Connect(String _server2connect)
	|	description: setta il nome del server a cui il client deve connettersi
	\****************************************************************************************/
	public void setServer2Connect(String _server2connect)
	{
		server2connect = _server2connect;
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public int getDownloadCapacity(){return downloadCapacity;}
	|	description: restituisce il limite di download del client
	\****************************************************************************************/
	public int getDownloadCapacity(){return downloadCapacity;}
	
	/****************************************************************************************\
	|	public void setDownloadCapacity(int _downloadCapacity)
	|	description: setta il limite di download del client
	\****************************************************************************************/
	public void setDownloadCapacity(int _downloadCapacity)
	{
		downloadCapacity = _downloadCapacity;
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public String getLogText()
	|	description: restituisce la lista di log sottoforma di unica stringa
	\****************************************************************************************/
	public String getLogText()
	{
		String logText = "";
		for(int i=0; i<log.size(); i++)
		{
			logText = logText + log.get(i) + "\n";
		}
		return logText;
	}
	
	/****************************************************************************************\
	|	public void addLogText(String _logLine)
	|	description: aggiunge una riga di testo ai log
	\****************************************************************************************/
	public void addLogText(String _logLine)
	{
		log.add(_logLine);
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public Color getLogColor()
	|	description: restituisce il colore dei log
	\****************************************************************************************/
	public Color getLogColor(){return coloreLog;}
	
	/****************************************************************************************\
	|	public void setLogColor(Color _color)
	|	description: setta il colore dei log
	\****************************************************************************************/
	public void setLogColor(Color _color)
	{
		coloreLog = _color;
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public boolean getFindBenabled()
	|	description: restituisce true se il pulsante di ricerca è abilitato, false altrimenti
	\****************************************************************************************/
	public boolean getFindBenabled(){return findBenabled;}
	
	/****************************************************************************************\
	|	public void setFindBenabled(boolean _en)
	|	description: abilita o disabilita il pulsante di ricerca
	\****************************************************************************************/
	public void setFindBenabled(boolean _en)
	{
		findBenabled = _en;
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public boolean getDisconnectBenabled()
	|	description: restituisce true se il pulsante di disconnessione è abilitato, false 
	|				 altrimenti
	\****************************************************************************************/
	public boolean getDisconnectBenabled(){return disconnectBenabled;}
	
	/****************************************************************************************\
	|	public void setDisconnectBenabled(boolean _en)
	|	description: abilita o disabilita il pulsante di disconnessione
	\****************************************************************************************/
	public void setDisconnectBenabled(boolean _en)
	{
		disconnectBenabled = _en;
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public String getDisconnectBtext()
	|	description: restituisce il testo del pulsante di disconnessione
	\****************************************************************************************/
	public String getDisconnectBtext(){return disconnectBtext;}
	
	/****************************************************************************************\
	|	public void  setDisconnectBtext(String _testoB)
	|	description: setta il testo del pulsante di disconnessione
	\****************************************************************************************/
	public void  setDisconnectBtext(String _testoB)
	{
		disconnectBtext = _testoB;
		viewRefresh();
	}	

}//end class ClientModel()
