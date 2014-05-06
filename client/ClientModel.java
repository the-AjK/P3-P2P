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
|			 1.1 - aggiunto logo iniziale
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
	private DeviceClient me;								//contiene nomeClient, riferimento e lista risorse
	private String server2connect;							//nome del server a cui connettersi
	private int downloadCapacity;							//capacita' di download
	private Vector<String> log;								//log di sistema
	private Color coloreLog;								//colore testo della casella log
	private boolean findBenabled;							//flag che indica se il pulsante di ricerca e' abilitato oppure no
	private boolean disconnectBenabled;						//flag che indica se il pulsante di disconnessione e' abilitato oppure no
	private String disconnectBtext;							//testo del pulsante di disconnessione
	private Vector<ResourceDownloadQueue> downloadQueue;	//lista di risorse in fase di download
	private String animationIcon;							//piccola icona di animazione per visualizzare lo stato del client
	
	private class ResourceDownloadQueue
	{
		public Resource risorsa;							//risorsa in download
		public Vector<DeviceClient> listaClient;			//lista di client che possiedono la risorsa
		public ResourceDownloadQueue(Resource _risorsa, Vector<DeviceClient> _listaClient)
		{
			risorsa = _risorsa;
			listaClient = _listaClient;
		}	
		
		/****************************************************************************************\
		|	public void removeClient(String _nomeClient)
		|	description: rimuove un client dalla listaClient che possiedono una risorsa
		\****************************************************************************************/
		public void removeClient(String _nomeClient)
		{
		
		}
	}
	
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
		downloadQueue = new Vector<ResourceDownloadQueue>();
		log = new Vector<String>();
		log.add("  ______  ______          ______  ______  ______ ");  
		log.add(" |   __ \\|__    | ______ |   __ \\|__    ||   __ \\"); 
		log.add(" |    __/|__    ||______||    __/|    __||    __/"); 
		log.add(" |___|   |______|        |___|   |______||___|	  "); 
		log.add("                          P3-P2P Client (C) JK   ");   
		log.add("--------------------------------------------------");
		animationIcon = "P2P";
	}
	
	/****************************************************************************************\
	|	public void viewRefresh()
	|	description: notifica la parte view in modalita' "model-pull"
	\****************************************************************************************/
	private void viewRefresh()
	{
		setChanged();
		notifyObservers();	//model pull
	}
	
	/****************************************************************************************\
	|	public DeviceClient getMe()
	|	description: restituisce me stesso
	\****************************************************************************************/
	public DeviceClient getMe(){return me;}
	
	/****************************************************************************************\
	|	public String getAnimIcon()
	|	description: restituisce l'icona
	\****************************************************************************************/
	public String getAnimIcon(){return animationIcon;}
	
	/****************************************************************************************\
	|	public void setAnimIcon(String _s)
	|	description: setta l'icona
	\****************************************************************************************/
	public void setAnimIcon(String _s)
	{
		animationIcon = _s;
		viewRefresh();
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
	|	public void addResourceToDownloadQueue(Resource _res, Vector<DeviceClient> _listaClient)
	|	description: aggiunge una risorsa in coda download
	\****************************************************************************************/
	public void addResourceToDownloadQueue(Resource _res, Vector<DeviceClient> _listaClient)
	{
		downloadQueue.add(new ResourceDownloadQueue(_res,_listaClient));
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public int getNdownloadQueue()
	|	description: restituisce il numero di risorse in download
	\****************************************************************************************/
	public int getNdownloadQueue(){return downloadQueue.size();}
	
	
	/****************************************************************************************\
	|	public void removeResourceInDownload(int _n)
	|	description: rimuove una risorsa in download
	\****************************************************************************************/
	public void removeResourceInDownload(int _n)
	{
		downloadQueue.remove(_n);
	}
	
	/****************************************************************************************\
	|	public int getResourceInDownload()
	|	description: restituisce la risorsa in download
	\****************************************************************************************/
	public Resource getResourceInDownload(int _n)
	{   
		if(_n < downloadQueue.size())
		{
			return downloadQueue.get(_n).risorsa;
		}
		return null;
	}
	
	/****************************************************************************************\
	|	public Vector<DeviceClient> getDownloadClientListForResource
	|	description: restituisce la lista di clients per la risorsa in download
	\****************************************************************************************/
	public Vector<DeviceClient> getDownloadClientListForResource(int _n)
	{
		if(_n < downloadQueue.size())
		{
			return downloadQueue.get(_n).listaClient;
		}
		return null;
	}
	
	/****************************************************************************************\
	|	public String getDownloadQueueText()
	|	description: restituisce la lista di risorse in download in formato stringa per la GUI
	\****************************************************************************************/
	public String getDownloadQueueText()
	{
		String res = "[ limite parti in download = " + downloadCapacity + " ]\n";
		Integer nparti=0;
		for(int i=0; i<downloadQueue.size(); i++)
		{
			nparti = downloadQueue.get(i).risorsa.getNparts();
			res = res + downloadQueue.get(i).risorsa.getName() + " [ ";
			for(int j=0; j<nparti; j++)
			{
				if(downloadQueue.get(i).risorsa.isPartEmpty(j)){
					res = res + "_ ";
				}else if(downloadQueue.get(i).risorsa.isPartInDownload(j)){
					res = res + "[ ] ";
				}else if(downloadQueue.get(i).risorsa.isPartFull(j)){
					res = res + "[*] ";
				}else{
					res = res + "--- ";  //unknow status ! :(
				}
			}
			res = res + "]\n";
		}
		return res;
	}
	
	/****************************************************************************************\
	|	public boolean resourceIsDownloading(String _nomeRisorsa, int _partiRisorsa)
	|	description: restituisce true se la risorsa e' nella coda download
	\****************************************************************************************/
	public boolean resourceIsDownloading(String _nomeRisorsa, int _partiRisorsa)
	{
		for(int i=0; i<downloadQueue.size(); i++)
		{
			if(downloadQueue.get(i).risorsa.getName().equals(_nomeRisorsa) &&
				downloadQueue.get(i).risorsa.getNparts() == _partiRisorsa
			)return true;
		}
		return false;
	}	
	
	/****************************************************************************************\
	|	public boolean resourceIsHere(String _nomeRisorsa, int _partiRisorsa)
	|	description: restituisce true se la risorsa e' presente nella lista risorse
	\****************************************************************************************/
	public boolean resourceIsHere(String _nomeRisorsa, int _partiRisorsa)
	{
		for(int i=0; i<me.getNresource(); i++)
		{
			if(me.getResource(i).getName().equals(_nomeRisorsa) &&
				me.getResource(i).getNparts() == _partiRisorsa
			)return true;
		}
		return false;
	}	
	
	/****************************************************************************************\
	|	public String getResourceListText()
	|	description: restituisce la lista di risorse del client in formato stringa per la GUI
	\****************************************************************************************/
	public String getResourceListText()
	{	
		String res = "";
		int nparti=0;
		for(int i=0; i<me.getNresource(); i++)
		{
			nparti = me.getResource(i).getNparts();
			res = res + me.getResource(i).getName() + " " + nparti + "\n";
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
	|	public int addLogText(String _logLine)
	|	description: aggiunge una riga di testo ai log e ritorna la posizione
	\****************************************************************************************/
	public int addLogText(String _logLine)
	{
		log.add(_logLine);
		viewRefresh();
		return log.size() - 1;
	}
	
	/****************************************************************************************\
	|	public void addLogTextToLine(int pos, String _logText)
	|	description: aggiunge del testo nella riga dei log indicata
	\****************************************************************************************/
	public void addLogTextToLine(int pos, String _logText)
	{
		if(pos < log.size())
		{
			log.setElementAt(log.get(pos) + _logText, pos);
			viewRefresh();
		}
	}
	
	/****************************************************************************************\
	|	public void setLogText(int pos, String _logLine)
	|	description: sovrascrive un log precedente nella posizione indicata
	\****************************************************************************************/
	public void setLogText(int pos, String _logLine)
	{
		if(pos < log.size())
		{
			log.setElementAt(_logLine, pos);
			viewRefresh();
		}
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
	|	description: restituisce true se il pulsante di ricerca e' abilitato, false altrimenti
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
	|	description: restituisce true se il pulsante di disconnessione e' abilitato, false 
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
