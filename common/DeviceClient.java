/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: DeviceClient.java
|	Description: componente client
|	Package: common
|	Version: 1.0 - creazione classe  
|
\****************************************************************************************/
package common;

import java.util.Vector;
import client.IClient;

public class DeviceClient
{
	private String nomeClient;			//nome del client
	private IClient ref;				//riferimento remoto del client
	private Vector<Resource> risorse;	//lista di risorse del client
	
	public DeviceClient(String _nomeClient, IClient _ref, Vector<Resource> _risorse)
	{
		nomeClient = _nomeClient;
		ref = _ref;	
		if(_risorse == null)
		{
			risorse = new Vector<Resource>();
		}else{
			risorse = _risorse;
		}
	}
	
	public String getName(){return nomeClient;}
	public IClient getRef(){return ref;}
	public Resource getResource(int nrisorsa)
	{
		if(nrisorsa < risorse.size())
		{
			return risorse.get(nrisorsa);
		}else{
			return null;
		}
	}
	public Vector<Resource> getResourceList(){return risorse;}
	public void addResource(Resource r)
	{
		risorse.add(r);
	}
	

} //end class DeviceClient
