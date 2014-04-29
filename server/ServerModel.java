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

import java.util.*; 		//vector

public class ServerModel extends java.util.Observable 
{
	//dati interfaccia grafica
	private String serverName;		//nome del server
	private String clients;			//clients connessi
	private String servers;			//server connessi
	private Vector<String> log;		//log di sistema
	
	//dati del server
	
	
	//costruttore
	public ServerModel()
	{	
		//inizializzo i campi dati
		serverName = "";
		clients = "";
		servers = "";
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
	
	public String getClientsText(){return clients;}	
	public String getServersText(){return servers;}
	
	public String getLogText()
	{
		String logText = "";
		for(int i=0; i<log.size(); i++)
		{
			logText = logText + log.get(i) + "\n";
		}
		return logText;
	}
	
	public void addLogText(String logLine)
	{
		log.add(new String(logLine));
		setChanged();
		notifyObservers();//model pull
	}

	

}//end class ServerModel
