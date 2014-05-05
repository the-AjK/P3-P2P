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
	private static final boolean PART_HEADER_FULL = true;	
	private static final boolean PART_HEADER_EMPTY = false;	
	private static final int PART_BODY_FULL = 22;	
	private static final int PART_BODY_EMPTY = 0;	
	
	private class ResourcePart implements Serializable
	{												//contenuto informativo della parte di risorsa
		public boolean file_header;					//l'header viene scritto ad inizio download
		public int file_body;						//per poi finire con la scrittura del body che indica il download completo
		
		public Vector<DeviceClient> logDownload;	//ogni parte di risorsa ha un log download che indica
													//quanti e quali clients l'hanno scaricata
													
		public ResourcePart(boolean _file_header, int _file_body)			
		{
			file_header = _file_header;
			file_body = _file_body;
			logDownload = new Vector<DeviceClient>();
		}
	}
	
	//campi dati
	private String nomeRisorsa;						//nome della risorsa
	private Vector<ResourcePart> partiRisorsa;		//parti della risorsa 		
	
	/****************************************************************************************\
	|	public Resource(String _nomeRisorsa, int _nparti, boolean _initValue)
	|	description: costruttore
	\****************************************************************************************/
	public Resource(String _nomeRisorsa, int _nparti, boolean _fullResource)
	{
		nomeRisorsa = _nomeRisorsa;
		partiRisorsa = new Vector<ResourcePart>();
		for(int i=0; i<_nparti; i++)
		{
			if(_fullResource)
			{
				partiRisorsa.add(new ResourcePart(PART_HEADER_FULL, PART_BODY_FULL));
			}else{
				partiRisorsa.add(new ResourcePart(PART_HEADER_EMPTY, PART_BODY_EMPTY));
			}
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
	|	description: restituisce true se la risorsa e' completa in tutte le sue parti
	\****************************************************************************************/
	public boolean isFull()
	{
		int trueCounter = 0;
		for(int i=0; i<partiRisorsa.size(); i++)
		{
			if(partiRisorsa.get(i).file_header == PART_HEADER_FULL)trueCounter++;
			if(partiRisorsa.get(i).file_body == PART_BODY_FULL)trueCounter++;
		}
	
		return trueCounter == (partiRisorsa.size() * 2);
	}	
	
	/****************************************************************************************\
	|	public boolean isEmpty()
	|	description: restituisce true se la risorsa ha tutte le sue parti incomplete
	\****************************************************************************************/
	public boolean isEmpty()
	{
		int trueCounter = 0;
		for(int i=0; i<partiRisorsa.size(); i++)
		{
			if(partiRisorsa.get(i).file_header == PART_HEADER_EMPTY)trueCounter++;
			if(partiRisorsa.get(i).file_body == PART_BODY_EMPTY)trueCounter++;
		}
	
		return trueCounter == (partiRisorsa.size() * 2);
	}
	
	/****************************************************************************************\
	|	public boolean isPartEmpty(int _part)
	|	description: restituisce true se la parte di risorsa indicata e' incompleta
	\****************************************************************************************/
	public boolean isPartEmpty(int _part)
	{
		return (partiRisorsa.get(_part).file_header == PART_HEADER_EMPTY) &&
			   (partiRisorsa.get(_part).file_body == PART_BODY_EMPTY);
	}
	
	/****************************************************************************************\
	|	public boolean isPartFull(int _part)
	|	description: restituisce true se la parte di risorsa indicata e' completa
	\****************************************************************************************/
	public boolean isPartFull(int _part)
	{
		return (partiRisorsa.get(_part).file_header == PART_HEADER_FULL) &&
			   (partiRisorsa.get(_part).file_body == PART_BODY_FULL);
	}
	
	/****************************************************************************************\
	|	public boolean isPartInDownload(int _part)
	|	description: restituisce true se la parte di risorsa indicata e' in download
	\****************************************************************************************/
	public boolean isPartInDownload(int _part)
	{
		return ((partiRisorsa.get(_part).file_header == PART_HEADER_EMPTY) &&
			   (partiRisorsa.get(_part).file_body == PART_BODY_FULL)) ||
			   ((partiRisorsa.get(_part).file_header == PART_HEADER_FULL) &&
			   (partiRisorsa.get(_part).file_body == PART_BODY_EMPTY));
	}
	
	/****************************************************************************************\
	|	public boolean getPart(int _n)
	|	description: restituisce l'header della parte della risorsa indicata
	\****************************************************************************************/
	public boolean getPartHeader(int _n)
	{
		return partiRisorsa.get(_n).file_header;
	}
	
	/****************************************************************************************\
	|	public int getPart(int _n)
	|	description: restituisce il body della parte della risorsa indicata
	\****************************************************************************************/
	public int getPartBody(int _n)
	{
		return partiRisorsa.get(_n).file_body;
	}
	
	/****************************************************************************************\
	|	public void setPartHeader(int _n, boolean _value)
	|	description: setta l'header della parte di risorsa indicata con un determinato valore (_value)
	\****************************************************************************************/
	public void setPartHeader(int _n, boolean _value)
	{
		partiRisorsa.get(_n).file_header = _value;
	}
	
	/****************************************************************************************\
	|	public void setPartBody(int _n, int _value)
	|	description: setta il body della parte di risorsa indicata con un determinato valore (_value)
	\****************************************************************************************/
	public void setPartBody(int _n, int _value)
	{
		partiRisorsa.get(_n).file_body = _value;
	}
	
	/****************************************************************************************\
	|	public void setPartInDownload(int _n)
	|	description: setta la parte di risorsa indicata come in download
	\****************************************************************************************/
	public void setPartInDownload(int _n)
	{
		partiRisorsa.get(_n).file_header = PART_HEADER_FULL;
		partiRisorsa.get(_n).file_body = PART_BODY_EMPTY;
	}
	
	/****************************************************************************************\
	|	public void setPartFull(int _n)
	|	description: setta la parte di risorsa indicata come piena
	\****************************************************************************************/
	public void setPartFull(int _n)
	{
		partiRisorsa.get(_n).file_header = PART_HEADER_FULL;
		partiRisorsa.get(_n).file_body = PART_BODY_FULL;
	}
	
	/****************************************************************************************\
	|	public void setPartEmpty(int _n)
	|	description: setta la parte di risorsa indicata come vuota
	\****************************************************************************************/
	public void setPartEmpty(int _n)
	{
		partiRisorsa.get(_n).file_header = PART_HEADER_EMPTY;
		partiRisorsa.get(_n).file_body = PART_BODY_EMPTY;
	}

} //end class Resource
