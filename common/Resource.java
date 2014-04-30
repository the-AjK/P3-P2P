/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: Resource.java
|	Description: componente risorsa
|	Package: common
|	Version: 1.0 - creazione classe  
|
\****************************************************************************************/
package common;

import java.util.Vector;

public class Resource
{
	private class ResourcePart 
	{
		public String part;
		public ResourcePart(String _s)
		{
			part = _s;
		}
	}
	
	private String nomeRisorsa;						//nome della risorsa
	private Vector<ResourcePart> partiRisorsa;		//parti della risorsa 
	
	public Resource(String _nomeRisorsa, int _nparti)
	{
		nomeRisorsa = _nomeRisorsa;
		partiRisorsa = new Vector<ResourcePart>();
		for(int i=0; i<_nparti; i++)
		{
			partiRisorsa.add(new ResourcePart("PART_CONTENT"));
		}
	}
	
	public String getName(){return nomeRisorsa;}
	
	public int getNparts(){return partiRisorsa.size();}
	
	public String getPart(int _n)
	{
		if(_n < partiRisorsa.size())
		{
			return partiRisorsa.get(_n).part;
		}else{
			return null;
		}	
	}
	
	public void setPart(int _n, String _value)
	{
		if(_n < partiRisorsa.size())
		{
			partiRisorsa.get(_n).part = _value;
		}
	}	

} //end class Resource
