/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: DeviceServer.java
|	Description: componente server
|	Package: common
|	Version: 1.0 - creazione classe  
|
\****************************************************************************************/
package common;

import server.IServer;
import java.io.Serializable;

public class DeviceServer implements Serializable
{
	//campi dati
	private String nomeServer;	//nome del server
	private IServer ref;		//riferimento del server
	
	/****************************************************************************************\
	|	public DeviceServer()
	|	description: costruttore senza parametri
	\****************************************************************************************/
	public DeviceServer()
	{
		this("",null);
	}
	
	/****************************************************************************************\
	|	public DeviceServer(String _nomeServer, IServer _ref)
	|	description: costruttore a due parametri
	\****************************************************************************************/
	public DeviceServer(String _nomeServer, IServer _ref)
	{
		nomeServer = _nomeServer;
		ref = _ref;	
	}
	
	/****************************************************************************************\
	|	public String getName()
	|	description: restituisce il nome del server
	\****************************************************************************************/
	public String getName(){return nomeServer;}
	
	/****************************************************************************************\
	|	public void setName(String _nomeServer)
	|	description: setta il nome del server
	\****************************************************************************************/
	public void setName(String _nomeServer){nomeServer = _nomeServer;}
	
	/****************************************************************************************\
	|	public IServer getRef()
	|	description: restituisce il riferimento del server
	\****************************************************************************************/
	public IServer getRef(){return ref;}

	/****************************************************************************************\
	|	public void setRef(IServer _ref)
	|	description: restituisce il riferimento del server
	\****************************************************************************************/
	public void setRef(IServer _ref){ref = _ref;}

} //end class DeviceServer
