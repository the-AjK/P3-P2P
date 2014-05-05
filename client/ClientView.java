/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ClientView.java
|	Description: componente view del pattern MVC
|	Package: client
|	Version: 1.0 - creazione GUI
|
\****************************************************************************************/
package client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.Observable; 

public class ClientView implements java.util.Observer
{ 
	//riferimento al model
	private ClientModel model;	
	
	//componenti grafici
	private JFrame frame;				//frame principale
	private JTextArea files;			//lista di risorse del client
	private JTextArea downloadQueue;	//coda di download
	private JTextArea log;				//log di sistema
	private JTextField toFind;			//barra testuale di ricerca risorse
	private JButton disconnectB;		//pulsante di disconnessione
	private JButton findB;				//pulsante di ricerca
	
	/****************************************************************************************\
	|	public ClientView() 
	|	description: costruttore
	\****************************************************************************************/
	public ClientView() throws Exception 
	{ 
		//imposto il layout principale
		frame = new JFrame("Client in avvio...");
		frame.setLayout(new FlowLayout());
		
		JPanel info = new JPanel();
		info.setLayout(new GridLayout(2,1));
		info.setPreferredSize(new Dimension(400,295));
		
		//preparo il primo pannello per i comandi utente
		JPanel userActionsP = new JPanel();
		userActionsP.setLayout(new FlowLayout());
				
		//creo il pannello di ricerca e lo aggiungo
		JPanel findP = new JPanel();
		findP.setLayout(new FlowLayout(5,5,5));
		findP.setBorder(BorderFactory.createTitledBorder("Cerca risorsa e Scarica"));
		toFind = new JTextField(10);
			 
		findB = new JButton("Cerca e Scarica");
		findB.setPreferredSize(new Dimension(findB.getPreferredSize().width,  
											 toFind.getPreferredSize().height)); 
		findP.add(toFind); 	//aggiungo la casella di testo
		findP.add(findB);	//aggiungo il pulsante cerca
		findB.setEnabled(false);
				
		//creo il pulsante Disconnetti
		disconnectB = new JButton("    avvio client...    ");
		disconnectB.setPreferredSize(new Dimension(disconnectB.getPreferredSize().width,  
													findP.getPreferredSize().height-9)); 
		disconnectB.setEnabled(false);
		
		//aggiungo barra di ricerca e pulsante disconnetti al pannello userActionsP
		userActionsP.add(findP);
		userActionsP.add(disconnectB);
		
		//creo pannello fileStatus -> files completi e coda download
		JPanel fileStatusP = new JPanel();
		fileStatusP.setLayout(new GridLayout(1,2,5,5));
		
		//ora le varie aree di testo
		files = new JTextArea();
		JScrollPane filesList = new JScrollPane(files);
		filesList.setBorder(BorderFactory.createTitledBorder("File completi"));
		fileStatusP.add(filesList);		
		
		downloadQueue = new JTextArea();
		JScrollPane downloadQueueList = new JScrollPane(downloadQueue);
		downloadQueueList.setBorder(BorderFactory.createTitledBorder("Coda download"));
		fileStatusP.add(downloadQueueList);
		
		//preparo il log sottostante
		log = new JTextArea();
		log.setFont(new Font("Monospaced", Font.PLAIN, 11));
		JScrollPane logList = new JScrollPane(log);
		DefaultCaret caret = (DefaultCaret) log.getCaret(); //scrolling continuo verso il basso
		caret.setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);
		logList.setBorder(BorderFactory.createTitledBorder("Log"));
		log.setForeground(Color.RED);
				
		//aggiungo i vari pannelli al JFrame principale
		info.add(fileStatusP);
		info.add(logList);
		frame.add(userActionsP);
		frame.add(info);
		
		//non permetto di editare il testo
		files.setEditable(false);
		downloadQueue.setEditable(false);
		log.setEditable(false);
				
		//impostazioni generali della finestra
		frame.setSize(412, 402);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
	} //ClientView
	
	/****************************************************************************************\
	|	public String getFindText()
	|	description: restituisce il campo di ricerca al controller
	\****************************************************************************************/
	public String getFindText(){return toFind.getText();}
	
	/****************************************************************************************\
	|	public void setFindText(String _s)
	|	description: restituisce il campo di ricerca al controller
	\****************************************************************************************/
	public void setFindText(String _s){toFind.setText(_s);}
	
	/****************************************************************************************\
	|	public void update(Observable _obs, Object _obj)
	|	description: implementazione dell'observer, metodo richiamato per eseguire il refresh
	|				 della GUI in modalita' "model-pull", la view usa il riferimento al model
	|				 per andare a leggere e recuperare i campi dati da visualizzare.
	\****************************************************************************************/
	public void update(Observable _obs, Object _obj)
	{
		frame.setTitle("P3-" + model.getAnimIcon() + " Client " + model.getClientName() + " per " + model.getServer2Connect());
		disconnectB.setText(model.getDisconnectBtext());
		disconnectB.setEnabled(model.getDisconnectBenabled());
		findB.setEnabled(model.getFindBenabled());
		files.setText(model.getResourceListText());
		downloadQueue.setText(model.getDownloadQueueText());
		log.setText(model.getLogText());
		log.setForeground(model.getLogColor());
	} 
		
	/****************************************************************************************\
	|	public void addController(ActionListener _controller)
	|	description: permette di aggiungere un controller come ActionListener alla view
	\****************************************************************************************/
	public void addActionController(ActionListener _controller)
	{
		findB.addActionListener(_controller); 
		disconnectB.addActionListener(_controller);
	}
	
	/****************************************************************************************\
	|	public void addWindowController(WindowListener _controller)
	|	description: permette di aggiungere un controller come WindowListener alla view
	\****************************************************************************************/
	public void addWindowController(WindowListener _controller)
	{
		frame.addWindowListener(_controller);
	}
	
	/****************************************************************************************\
	|	public void addModel(ClientModel _model)
	|	description: setta il riferimento al model
	\****************************************************************************************/
	public void addModel(ClientModel _model){model = _model;}
	
	/****************************************************************************************\
	|	public static class CloseListener
	|	description: classe interna per gestire la chiusura della GUI
	\****************************************************************************************/
	public static class CloseListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			System.exit(0);
		} 
	} 	
		
}//end class ClientView()
