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
import java.io.Serializable;
import java.io.*;

public class Resource implements Serializable
{
	private class ResourcePart implements Serializable
	{
		public boolean part;
		public ResourcePart(boolean _part){part = _part;}
	}
	
	//campi dati
	private String nomeRisorsa;						//nome della risorsa
	private Vector<ResourcePart> partiRisorsa;		//parti della risorsa 
		
	
	/****************************************************************************************\
	|	public Resource(String _nomeRisorsa, int _nparti, boolean _initValue)
	|	description: costruttore
	\****************************************************************************************/
	public Resource(String _nomeRisorsa, int _nparti, boolean _initValue)
	{
		nomeRisorsa = _nomeRisorsa;
		partiRisorsa = new Vector<ResourcePart>();
		for(int i=0; i<_nparti; i++)
		{
			partiRisorsa.add(new ResourcePart(_initValue));
		}
	}
	
	/****************************************************************************************\
	|	public String getName()
	|	description: restituisce il nome della risorsa
	\****************************************************************************************/
	public String getName(){return nomeRisorsa;}
	
	/****************************************************************************************\
	|	public int getNparts()
	|	description: restituisce il numero di parti che compongono la risorsa
	\****************************************************************************************/
	public int getNparts(){return partiRisorsa.size();}
	
	/****************************************************************************************\
	|	public boolean isFull()
	|	description: restituisce true se la risorsa è completa in tutte le sue parti
	\****************************************************************************************/
	public boolean isFull()
	{
		int trueCounter = 0;
		for(int i=0; i<partiRisorsa.size(); i++)
			if(partiRisorsa.get(i).part)trueCounter++;
	
		return trueCounter == partiRisorsa.size();
	}	
	
	/****************************************************************************************\
	|	public boolean isEmpty()
	|	description: restituisce true se la risorsa ha tutte le sue parti incomplete
	\****************************************************************************************/
	public boolean isEmpty()
	{
		int falseCounter = 0;
		for(int i=0; i<partiRisorsa.size(); i++)
			if(partiRisorsa.get(i).part == false)falseCounter++;
	
		return falseCounter == partiRisorsa.size();
	}
	
	/****************************************************************************************\
	|	public boolean isPartEmpty(int _part)
	|	description: restituisce true se la parte di risorsa indicata è incompleta
	\****************************************************************************************/
	public boolean isPartEmpty(int _part)
	{
		if(_part < partiRisorsa.size())
		{
			return partiRisorsa.get(_part).part == false;
		}
		return false;
	}
	
	/****************************************************************************************\
	|	public boolean isPartFull(int _part)
	|	description: restituisce true se la parte di risorsa indicata è completa
	\****************************************************************************************/
	public boolean isPartFull(int _part)
	{
		if(_part < partiRisorsa.size())
		{
			return partiRisorsa.get(_part).part == true;
		}
		return false;
	}
	
	/****************************************************************************************\
	|	public boolean getPart(int _n)
	|	description: restituisce la parte della risorsa indicata
	\****************************************************************************************/
	public boolean getPart(int _n)
	{
		if(_n < partiRisorsa.size())
		{
			return partiRisorsa.get(_n).part;
		}else{
			return false;
		}	
	}
	
	/****************************************************************************************\
	|	public void setPart(int _n, boolean _value)
	|	description: setta la parte di risorsa indicata con un determinato valore (_value)
	\****************************************************************************************/
	public void setPart(int _n, boolean _value)
	{
		if(_n < partiRisorsa.size())
		{
			partiRisorsa.get(_n).part = _value;
		}
	}

} //end class Resource
