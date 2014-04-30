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

//import java.rmi.*;
//import java.io.*;
//import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.Observable; //per update();

/****************************************************************************************\
|	public class ServerView
|	description: gestisce la creazione della GUI.
\****************************************************************************************/
public class ServerView implements java.util.Observer
{ 
	//riferimento al model
	private ServerModel model;	
	
	//componenti grafici
	private JFrame frame;		//frame principale
	private JTextArea clients; 	//lista clients connessi
	private JTextArea servers; 	//lista server connessi
	private JTextArea log;		//log di sistema
	
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
		clientsList.setBorder(BorderFactory.createTitledBorder("Client connessi"));
		connectionsP.add(clientsList);		
		
		servers = new JTextArea();
		JScrollPane serversList = new JScrollPane(servers);
		serversList.setBorder(BorderFactory.createTitledBorder("Server connessi"));
		connectionsP.add(serversList);
		
		//preparo il log
		log = new JTextArea();
		JScrollPane logList = new JScrollPane(log);
		DefaultCaret caret = (DefaultCaret) log.getCaret(); //scrolling continuo verso il basso
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
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
		frame.addWindowListener(new WindowAdapter()
						 {
							public void windowClosing(WindowEvent e)
							{
								//log.append("chiusura server in corso...\n");
								//JOptionPane.showMessageDialog(null, "Verranno notificati gli altri server. Attendere...", "Chiusura server", JOptionPane.ERROR_MESSAGE);
								//log.append("Bye bye :)\n");
								System.exit(-1);
							}
						});
		
		
		
	} //ServerView()
	
	//il model notifica la view richiamando questo metodo implementato
	public void update(Observable obs, Object obj)
	{
		//model Pull, quindi richiedo i dati al model ed aggiorno la vista:
		frame.setTitle("Server " + model.getServerName());
		clients.setText(model.getClientsText());
		servers.setText(model.getServersText());
		log.setText(model.getLogText());
		log.setForeground(model.getLogColor());
	} 
	
	public void addController(ActionListener controller)
	{
		//button.addActionListener(controller);	
	}
	
	public void addModel(ServerModel _model)
	{
		this.model = _model;
	}
	
	public static class CloseListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			System.exit(0);
		} 
	} 	
		
}//end class ServerView()