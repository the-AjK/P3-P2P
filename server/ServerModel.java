/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerModel.java
|	Description: componente model del pattern MVC
|	Package: server
|	Version: 0.1 - creazione struttura scheletro
|			 1.0 - aggiunti campi dati e relativi metodi set/get
|			 1.1 - aggiunto logo iniziale
|
\****************************************************************************************/
package server;

import java.util.Vector; 		
import java.awt.Color;	
import client.IClient;		
import common.DeviceClient;
import common.DeviceServer;
import common.Resource;

public class ServerModel extends java.util.Observable 
{
	//campi dati
	private DeviceServer me;				//contiene nome del server e riferimento
	private Vector<DeviceClient> clients;	//clients connessi
	private Vector<DeviceServer> servers;	//server connessi
	private Vector<String> log;				//log di sistema
	private Color coloreLog;				//colore testo della casella log
	private String animationIcon;			//piccola icona di animazione per visualizzare lo stato del client
	
	/****************************************************************************************\
	|	public ServerModel()
	|	description: costruttore del model
	\****************************************************************************************/
	public ServerModel()
	{	
		//inizializzo i campi dati
		me = new DeviceServer("",null); 	//creo me stesso :)
		clients = new Vector<DeviceClient>();
		servers = new Vector<DeviceServer>();
		log = new Vector<String>();
		log.add("  ______  ______          ______  ______  ______ ");  
		log.add(" |   __ \\|__    | ______ |   __ \\|__    ||   __ \\"); 
		log.add(" |    __/|__    ||______||    __/|    __||    __/"); 
		log.add(" |___|   |______|        |___|   |______||___|	  "); 
		log.add("                          P3-P2P Server (C) JK   ");   
		log.add("--------------------------------------------------");
		animationIcon = "P2P";
	}
	
	/****************************************************************************************\
	|	public void viewRefresh()
	|	description: notifica la parte view in modalit� "model-pull"
	\****************************************************************************************/
	private void viewRefresh()
	{
		setChanged();
		notifyObservers();
	}
	
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
	|	public String getServerName()
	|	description: restituisce il nome del server
	\****************************************************************************************/	
	public String getServerName(){return me.getName();}
	
	/****************************************************************************************\
	|	public IServer getServerRef()
	|	description: restituisce il riferimento del server
	\****************************************************************************************/	
	public IServer getServerRef(){return me.getRef();}
	
	/****************************************************************************************\
	|	public void setServerRef(IServer _ref)
	|	description: setta il riferimento del server
	\****************************************************************************************/	
	public void setServerRef(IServer _ref){me.setRef(_ref);}
	
	/****************************************************************************************\
	|	public int getNclients()
	|	description: restituisce il numero di client connessi al server
	\****************************************************************************************/
	public int getNclients(){return clients.size();}
	
	/****************************************************************************************\
	|	public int getNservers()
	|	description: restituisce il numero di server connessi
	\****************************************************************************************/
	public int getNservers(){return servers.size();}
	
	/****************************************************************************************\
	|	public Vector<DeviceServer> getServerList()
	|	description: restituisce la lista dei server connessi
	\****************************************************************************************/
	public Vector<DeviceServer> getServerList(){return servers;}
	
	/****************************************************************************************\
	|	public Vector<DeviceClient> getClientList()
	|	description: restituisce la lista dei clients connessi al server
	\****************************************************************************************/
	public Vector<DeviceClient> getClientList(){return clients;}
	
	/****************************************************************************************\
	|	public void removeServer(String _server2remove)
	|	description: rimuove il server specificato dalla lista di servers
	\****************************************************************************************/	
	public void removeServer(String _server2remove)
	{
		for(int i=0; i<servers.size(); i++)
		{
			if(servers.get(i).getName().equals(_server2remove))
			{
				servers.remove(i);
				break;
			}		
		}	
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public void removeClient(String _client2remove)
	|	description: rimuove il client specificato dalla lista di clients 
	\****************************************************************************************/
	public void removeClient(String _client2remove)
	{
		for(int i=0; i<clients.size(); i++)
		{
			if(clients.get(i).getName().equals(_client2remove))
			{
				clients.remove(i);
				break;
			}		
		}	
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public Vector<DeviceClient> getClientsOwnResourceList(Resource _risorsa)
	|	description: restituisce una lista di client che possiedono una determinata risorsa
	\****************************************************************************************/
	public Vector<DeviceClient> getClientsOwnResourceList(Resource _risorsa)
	{
		Vector<DeviceClient> res = new Vector<DeviceClient>();
		Resource risorsa;
		DeviceClient client;
		
		for(int i=0; i<clients.size(); i++)		//scorro la lista di clients
		{
			client = clients.get(i);
			for(int j=0; j<client.getNresource(); j++) //scorro la lista delle risorse
			{
				risorsa = client.getResource(j);
				if(risorsa.getName().equals(_risorsa.getName()) &&
					risorsa.getNparts() == _risorsa.getNparts() )
				{
					res.add(client);	//aggiungo il client che possiede la risorsa
					break;
				}
			}
		}
		return res;
	}
	
	/****************************************************************************************\
	|	public DeviceServer getServer(int _n)
	|	description: restituisce il DeviceServer specificato 
	\****************************************************************************************/
	public DeviceServer getServer(int _n)
	{
		if(_n < servers.size())
		{
			return servers.get(_n);
		}else{
			return null;
		}	
	}
	
	/****************************************************************************************\
	|	public boolean serverIsHere(String _serverName)
	|	description: restituisce true se il server specificato � nella lista servers
	\****************************************************************************************/
	public boolean serverIsHere(String _serverName)
	{
		for(int i=0; i<servers.size(); i++)
		{
			if(servers.get(i).getName().equals(_serverName))return true;
		}
		return false;
	}
	
	/****************************************************************************************\
	|	public DeviceClient getClient(int _n)
	|	description: restituisce il DeviceClient specificato 
	\****************************************************************************************/
	public DeviceClient getClient(int _n)
	{
		if(_n < clients.size())
		{
			return clients.get(_n);
		}else{
			return null;
		}	
	}	
	
	/****************************************************************************************\
	|	public boolean clientIsHere(String _clientName)
	|	description: restituisce true se il client specificato � nella lista clients
	\****************************************************************************************/
	public boolean clientIsHere(String _clientName)
	{
		for(int i=0; i<clients.size(); i++)
		{
			if(clients.get(i).getName().equals(_clientName))return true;
		}
		return false;
	}
	
	/****************************************************************************************\
	|	public void setServerName(String _nome)
	|	description: setta il nome del server
	\****************************************************************************************/
	public void setServerName(String _nome)
	{
		me.setName(_nome);
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	private String vector2String(Vector<String> v)
	|	description: converte un vector di stringhe in un'unica stringa
	\****************************************************************************************/
	private String vector2String(Vector<String> v)
	{
		String res = "";
		for(int i=0; i<v.size(); i++)
		{
			res = res + v.get(i) + "\n";
		}
		return res;
	}
	
	/****************************************************************************************\
	|	public String getClientsText()
	|	description: restituisce la lista di clients sottoforma di stringa per la GUI
	\****************************************************************************************/
	public String getClientsText()
	{
		String res = "";
		
		for(int i=0; i<clients.size(); i++)
		{
			res = res + clients.get(i).getName() + " - [ R: {";
			for(int j=0; j<clients.get(i).getNresource(); j++)
			{
				if(j!=0)res = res + ",";
				res = res + clients.get(i).getResource(j).getName() + clients.get(i).getResource(j).getNparts();
			}
			res = res + "} ]\n";
		}
		return res;
	}	
	
	/****************************************************************************************\
	|	public String getServersText()
	|	description: restituisce la lista di servers sottoforma di stringa per la GUI
	\****************************************************************************************/
	public String getServersText()
	{
		String res = "";
		for(int i=0; i<servers.size(); i++)
		{
			res = res + servers.get(i).getName() + "\n";
		}
		return res;
	}
	
	/****************************************************************************************\
	|	public String getLogText()
	|	description: restituisce la lista dei log sottoforma di stringa per la GUI
	\****************************************************************************************/
	public String getLogText(){return vector2String(log);}
	
	/****************************************************************************************\
	|	public void addServer(String _nomeServer, IServer _ref)
	|	description: aggiunge un server alla lista di servers
	\****************************************************************************************/
	public void addServer(String _nomeServer, IServer _ref)
	{
		servers.add(new DeviceServer(_nomeServer,_ref)); 
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public void addClient(String _nomeClient, IClient _ref, Vector<Resource> _listaRisorse)
	|	description: aggiunge un client alla lista dei clients
	\****************************************************************************************/
	public void addClient(String _nomeClient, IClient _ref, Vector<Resource> _listaRisorse)
	{
		clients.add(new DeviceClient(_nomeClient,_ref,_listaRisorse));
		viewRefresh();
	}
	
	/****************************************************************************************\
	|	public void addClientResourceList(String _nomeClient, Vector<Resource> _listaRisorse)
	|	description: aggiorna la lista risorse di un client
	\****************************************************************************************/
	public void addClientResourceList(String _nomeClient, Vector<Resource> _listaRisorse)
	{
		boolean trovato = false;
		int i;
		for(i=0; i<clients.size(); i++)
		{
			if(clients.get(i).getName().equals(_nomeClient))
			{
				trovato = true;
				break;
			}
		}
		if(trovato)
		{
			clients.get(i).setResourceList(_listaRisorse);
			viewRefresh();
		}
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
	|	description: restituisce il colore del testo dei log
	\****************************************************************************************/
	public Color getLogColor(){return coloreLog;}
	
	/****************************************************************************************\
	|	public Color setLogColor()
	|	description: setta il colore del testo dei log
	\****************************************************************************************/
	public void setLogColor(Color _color)
	{
		coloreLog = _color;
		viewRefresh();
	}	

}//end class ServerModel()
