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
import java.io.Serializable;

public class DeviceClient implements Serializable
{
	private String nomeClient;			//nome del client
	private IClient ref;				//riferimento remoto del client
	private Vector<Resource> risorse;	//lista di risorse del client
	
	/****************************************************************************************\
	|	public DeviceClient()
	|	description: costruttore senza parametri
	\****************************************************************************************/
	public DeviceClient()
	{
		this("",null,null);
	}
	
	/****************************************************************************************\
	|	public DeviceClient(String _nomeClient, IClient _ref, Vector<Resource> _risorse)
	|	description: costruttore a tre parametri
	\****************************************************************************************/
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
	
	/****************************************************************************************\
	|	public String getName()
	|	description: restituisce il nome del client
	\****************************************************************************************/
	public String getName(){return nomeClient;}
	
	/****************************************************************************************\
	|	public void setName(String _nomeClient)
	|	description: setta il nome del client
	\****************************************************************************************/
	public void setName(String _nomeClient){nomeClient = _nomeClient;}
	
	/****************************************************************************************\
	|	public void addLogDownload(DeviceClient _client, Resource _risorsa, int _parteRisorsa)
	|	description: aggiunge un client al log di download di una determinata parte di risorsa
	\****************************************************************************************/
	public void addLogDownload(DeviceClient _client, Resource _risorsa, int _parteRisorsa){
		for(int i=0; i<risorse.size(); i++)
		{
			if( risorse.get(i).getName().equals(_risorsa.getName()) &&
				risorse.get(i).getNparts() == _risorsa.getNparts() )
					risorse.get(i).addLogDownload(_client, _parteRisorsa);			
		}
	}
	
	/****************************************************************************************\
	|	public IClient getRef()
	|	description: restituisce il riferimento del client
	\****************************************************************************************/
	public IClient getRef(){return ref;}
	
	/****************************************************************************************\
	|	public void setRef(IClient _ref)
	|	description: setta il riferimento del client
	\****************************************************************************************/
	public void setRef(IClient _ref){ref = _ref;}
	
	/****************************************************************************************\
	|	public int getNresource()
	|	description: restituisce il numero di risorse del client
	\****************************************************************************************/
	public int getNresource(){return risorse.size();}
	
	/****************************************************************************************\
	|	public Resource getResource(int nrisorsa)
	|	description: restituisce la risorsa indicata
	\****************************************************************************************/
	public Resource getResource(int _nrisorsa)
	{
		if(_nrisorsa < risorse.size())
		{
			return risorse.get(_nrisorsa);
		}else{
			return null;
		}
	}
	
	/****************************************************************************************\
	|	public Vector<Resource> getResourceList()
	|	description: restituisce la lista di risorse
	\****************************************************************************************/
	public Vector<Resource> getResourceList(){return risorse;}
	
	/****************************************************************************************\
	|	public void setResourceList()
	|	description: restituisce la lista di risorse
	\****************************************************************************************/
	public void setResourceList(Vector<Resource> _r){risorse = _r;}
	
	/****************************************************************************************\
	|	public void addResource(Resource _r)
	|	description: aggiunge una risorsa alla lista di risorse
	\****************************************************************************************/
	public void addResource(Resource _r)
	{
		risorse.add(_r);
	}

} //end class DeviceClient
