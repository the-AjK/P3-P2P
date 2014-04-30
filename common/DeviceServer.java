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

public class DeviceServer
{
	private String nomeServer;	//nome del server
	private IServer ref;		//riferimento remoto del server
	
	public DeviceServer(String _nomeServer, IServer _ref)
	{
		nomeServer = _nomeServer;
		ref = _ref;	
	}
	
	public String getName(){return nomeServer;}
	public IServer getRef(){return ref;}	

} //end class DeviceServer
