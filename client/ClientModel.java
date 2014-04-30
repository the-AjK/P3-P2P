/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ClientModel.java
|	Description: componente model del pattern MVC
|	Package: client
|	Version: 0.1 - creazione struttura scheletro
|			 0.2 - 
|
\****************************************************************************************/
package client;

import java.util.*; 		//vector
import java.awt.Color;			//color

public class ClientModel extends java.util.Observable 
{
	//dati interfaccia grafica
	private String clientName;			//nome del client
	private String server2connect;		//nome del server a cui connettersi
	private int downloadCapacity;		//capacità di download
	private Vector<String> log;			//log di sistema
	private Color coloreLog;			//colore testo della casella log
		
	//dati del client
	
	
	//costruttore
	public ClientModel()
	{	
		//inizializzo i campi dati
		clientName = "";
		server2connect = "";
		downloadCapacity = 0;
		log = new Vector<String>();
		log.add("P3-P2P Client (C) JK");
	}

	//metodi usati dalla view e controller per recuperare e/o modificare i dati del model	
	public String getClientName(){return clientName;}
	
	public void setClientName(String _nome)
	{
		clientName = _nome;
		setChanged();
		notifyObservers();//model pull
	}
	
	public String getServer2Connect(){return server2connect;}
	
	public void setServer2Connect(String _server2connect)
	{
		server2connect = _server2connect;
		setChanged();
		notifyObservers();//model pull
	}
	
	public int getDownloadCapacity(){return downloadCapacity;}
	
	public void setDownloadCapacity(int _downloadCapacity)
	{
		downloadCapacity = _downloadCapacity;
		setChanged();
		notifyObservers();//model pull
	}
	
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
	
	public Color getLogColor(){return coloreLog;}
	public void setLogColor(Color _color)
	{
		coloreLog = _color;
		setChanged();
		notifyObservers();//model pull
	}
	

}//end class ClientModel()
