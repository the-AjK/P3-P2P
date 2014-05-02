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
	//campi dati
	private String nomeRisorsa;						//nome della risorsa
	//private Vector<String> partiRisorsa;			//parti della risorsa 
	
	//private static final long serialVersionUID = 7526472295622776147L;
	
	/****************************************************************************************\
	|	public Resource(String _nomeRisorsa, int _nparti)
	|	description: costruttore
	\****************************************************************************************/
	public Resource(String _nomeRisorsa, Integer _nparti)
	{
		nomeRisorsa = _nomeRisorsa;
		//partiRisorsa = new Vector<String>();
		for(Integer i=0; i<_nparti; i++)
		{
			//partiRisorsa.add(new String("PART_CONTENT"));
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
	//public Integer getNparts(){return partiRisorsa.size();}
	
	/****************************************************************************************\
	|	public String getPart(int _n)
	|	description: restituisce la parte della risorsa indicata
	\****************************************************************************************/
	/*public String getPart(Integer _n)
	{
		if(_n < partiRisorsa.size())
		{
			return partiRisorsa.get(_n);
		}else{
			return null;
		}	
	}*/
	
	/****************************************************************************************\
	|	public void setPart(int _n, String _value)
	|	description: setta la parte di risorsa indicata con un determinato valore (_value)
	\****************************************************************************************/
	/*public void setPart(int _n, String _value)
	{
		if(_n < partiRisorsa.size())
		{
			partiRisorsa.get(_n) = _value;
		}
	}	*/

	/*
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		//out.writeObject(nome);
		//out.writeVector(componentiRisorsa);
	}
	
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		in.defaultReadObject();
		//nome = (String) in.readObject();
		//componentiRisorsa = (Vector<Integer>) in.readVector();
	
	}*/

} //end class Resource
