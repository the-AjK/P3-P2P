/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerView.java
|	Description: componente view del pattern MVC
|	Package: server
|	Version: 1.0 - creazione GUI
|
\****************************************************************************************/
package server;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.Observable; 

public class ServerView implements java.util.Observer
{ 
	//riferimento al model
	private ServerModel model;	
	
	//componenti grafici
	private JFrame frame;		//frame principale
	private JTextArea clients; 	//lista clients connessi
	private JTextArea servers; 	//lista server connessi
	private JTextArea log;		//log di sistema
	
	/****************************************************************************************\
	|	public ServerView()
	|	description: costruttore
	\****************************************************************************************/
	public ServerView() throws Exception 
	{ 
		//layout principale
		frame = new JFrame("Server in avvio...");
		frame.setLayout(new GridLayout(2,1,10,5));
		
		//creo pannello principale
		JPanel connectionsP = new JPanel();
		connectionsP.setLayout(new GridLayout(1,2,5,5));	
		
		//ora le varie aree di testo
		clients = new JTextArea();
		JScrollPane clientsList = new JScrollPane(clients);
		clientsList.setBorder(BorderFactory.createTitledBorder("Client connessi - risorse"));
		connectionsP.add(clientsList);		
		
		servers = new JTextArea();
		JScrollPane serversList = new JScrollPane(servers);
		serversList.setBorder(BorderFactory.createTitledBorder("Server connessi"));
		connectionsP.add(serversList);
		
		//preparo il log
		log = new JTextArea();
		log.setFont(new Font("Monospaced", Font.PLAIN, 11));
		JScrollPane logList = new JScrollPane(log);
		DefaultCaret caret = (DefaultCaret) log.getCaret(); //scrolling continuo verso il basso
		caret.setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);
		logList.setBorder(BorderFactory.createTitledBorder("Log"));
		log.setForeground(Color.RED);
		
		//aggiungo le varie componenti al JFrame principale
		frame.add(connectionsP);
		frame.add(logList);
		
		//non permetto di editare il testo
		clients.setEditable(false);
		servers.setEditable(false);
		log.setEditable(false);
				
		//impostazioni generali della finestra
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	
	} //ServerView()
	
	/****************************************************************************************\
	|	public void update(Observable _obs, Object _obj)
	|	description: implementazione dell'observer, metodo richiamato per eseguire il refresh
	|				 della GUI in modalità "model-pull", la view usa il riferimento al model
	|				 per andare a leggere e recuperare i campi dati da visualizzare.
	\****************************************************************************************/
	public void update(Observable _obs, Object _obj)
	{
		frame.setTitle("P3-" + model.getAnimIcon() + " Server " + model.getServerName());
		clients.setText(model.getClientsText());
		servers.setText(model.getServersText());
		log.setText(model.getLogText());
		log.setForeground(model.getLogColor());
	} 
	
	/****************************************************************************************\
	|	public void addController(ActionListener _controller)
	|	description: predisposizione per aggiungere un controller come ActionListener alla view
	|				 (attualmente non utilizzato visto che l'interfaccia server non presenta
	|				 input utente di nessun tipo).
	\****************************************************************************************/
	public void addActionController(ActionListener _controller)
	{
		//button.addActionListener(_controller);
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
	|	public void addModel(ServerModel _model)
	|	description: setta il riferimento al model
	\****************************************************************************************/
	public void addModel(ServerModel _model){model = _model;}
		
}//end class ServerView()
