/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerModel.java
|	Description: componente model del pattern MVC
|	Package: server
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - 
|
\****************************************************************************************/
package server;

import java.util.Vector; 		
import java.awt.Color;			
import common.DeviceClient;
import common.DeviceServer;

public class ServerModel extends java.util.Observable 
{
	//dati interfaccia grafica
	private String serverName;				//nome del server
	private Vector<DeviceClient> clients;	//clients connessi
	private Vector<DeviceServer> servers;	//server connessi
	private Vector<String> log;				//log di sistema
	private Color coloreLog;				//colore testo della casella log
	
	//dati del server	
	
	//costruttore
	public ServerModel()
	{	
		//inizializzo i campi dati
		serverName = "JK :)";
		clients = new Vector<DeviceClient>();
		servers = new Vector<DeviceServer>();
		log = new Vector<String>();
		log.add("P3-P2P Server (C) JK");
	}

	//metodi usati dalla view e controller per recuperare e/o modificare i dati del model	
	public String getServerName(){return serverName;}
	
	public void setServerName(String _nome)
	{
		serverName = _nome;
		setChanged();
		notifyObservers();//model pull
	}
	
	//converte un vector di stringhe in un'unica stringa
	private String vector2String(Vector<String> v)
	{
		String res = "";
		for(int i=0; i<v.size(); i++)
		{
			res = res + v.get(i) + "\n";
		}
		return res;
	}
	
	public String getClientsText()
	{
		String res = "";
		for(int i=0; i<clients.size(); i++)
		{
			res = res + clients.get(i).getName() + "\n";
		}
		return res;
	}	
	
	public String getServersText()
	{
		String res = "";
		for(int i=0; i<servers.size(); i++)
		{
			res = res + servers.get(i).getName() + "\n";
		}
		return res;
	}
	
	public String getLogText(){return vector2String(log);}
	
	public void addServer(String _nomeServer, IServer _ref)
	{
		servers.add(new DeviceServer(_nomeServer,_ref)); //aggiungo un server alla lista di server
		setChanged();
		notifyObservers();//model pull
	}
	
	public void addLogText(String logLine)
	{
		log.add(logLine);
		setChanged();
		notifyObservers();//model pull
	}
	
	public Color getLogColor(){return coloreLog;}
	public void setLogColor(Color _color)
	{
		coloreLog = _color;
		setChanged();
		notifyObservers();//model pull
	}
	
	

}//end class ServerModel()
