/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ServerStarter.java
|	Package: server
|	Version: 1.0 - avvio main con parametri e creazione pattern MVC
|
\****************************************************************************************/
package server;

/****************************************************************************************\
|	public class ServerStarter
|	description: avvia il server
\****************************************************************************************/
public class ServerStarter
{ 
	public ServerStarter(String _nomeServer) throws Exception 
	{ 
		//creo le varie componenti del pattern MVC
		ServerModel sModel = new ServerModel();
		ServerView sView = new ServerView();
		ServerController sController = new ServerController();

		//connetto la view come osservatore del model
		sModel.addObserver(sView);
		
		//connetto model e view al controller
		sController.addModel(sModel);
		sController.addView(sView);
		
		//connetto il controller ed il model alla view
		sView.addController(sController);	
		sView.addModel(sModel);
		
		//inizializzo il model con il nome del server ed i vari dati
		sController.initModel(_nomeServer);
	}
	
	/****************************************************************************************\
	|	public static void main(String[])
	|	description: :)
	\****************************************************************************************/
	public static void main(String[] args) throws Exception
	{ 
		try
		{ 
			if(args.length == 0 || args[0] == "--help" || args[0] == "-h" || args[0] == "--h" || args[0] == "-help")
			{
				System.out.println("\nServerStarter necessita di essere invocato con i seguenti parametri:\n");
				System.out.println("server name - string\n\n");
				System.out.println("Usage: \n");
				System.out.println("     ServerStarter [server name] (start a new server)\n");
				System.out.println("     ServerStarter --help (display this help)\n");
				System.exit(0);				
			}	
			ServerStarter s = new ServerStarter(	args[0]	//nome server
												);
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}
		
	}//end main
	
}//end class ServerStarter()
