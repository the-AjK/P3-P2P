/****************************************************************************************\
|	
|	Project: P3-P2P
|	Author: Alberto Garbui - Mat.: 561226
|
|	File: ClientStarter.java
|	Package: client
|	Version: 1.0 - avvio main con parametri e creazione pattern MVC
|
\****************************************************************************************/
package client;

public class ClientStarter
{ 
	/****************************************************************************************\
	|	public ClientStarter(String _nomeClient, String _server2connect, int _D, String[] _R)
	|	description: costruttore
	\****************************************************************************************/
	public ClientStarter(	String _nomeClient,		//nome client
							String _server2connect,	//nome server
							int _D, 				//disponibilità download
							String[] _R				//elenco risorse (nome risorse + n_parti)
						) throws Exception 
	{ 
		//creo le varie componenti del pattern MVC
		ClientModel cModel = new ClientModel();
		ClientView cView = new ClientView();
		ClientController cController = new ClientController();

		//connetto la view come osservatore del model
		cModel.addObserver(cView);
		
		//connetto model e view al controller
		cController.addModel(cModel);
		cController.addView(cView);
		
		//connetto il controller ed il model alla view
		cView.addActionController(cController);		//controller input utente dai pulsanti
		cView.addWindowController(cController);		//controller input utente dalla finestra
		cView.addModel(cModel);
		
		//inizializzo il model con il nome del server ed i vari dati
		cController.initModel(_nomeClient,_server2connect,_D,_R);
	}
	
	/****************************************************************************************\
	|	public static void main(String[])
	|	description: :)
	\****************************************************************************************/
	public static void main(String[] args) throws Exception
	{ 
		try
		{ 
			if(args.length < 3 || args[0] == "--help" || args[0] == "-h" || args[0] == "--h" || args[0] == "-help")
			{
				System.out.println("\nClientStarter necessita di essere invocato con i seguenti parametri:\n");
				System.out.println("nome client - string\n");
				System.out.println("nome server a cui connettersi - string\n");
				System.out.println("capacita di download - int\n");
				System.out.println("elenco risorse coppie [nome risorsa - parti risorsa]\n\n");
				System.out.println("Usage: \n");
				System.out.println("     ClientStarter [client name] [server to connect] [download capacity] [[resource name] [resource parts]]* (start a new client)\n");
				System.out.println("     ClientStarter --help (display this help)\n");
				System.exit(0);
			}			

			String[] risorse = new String[args.length-3];
			System.arraycopy(args,3,risorse,0,args.length-3);
		
			ClientStarter s=new ClientStarter(	args[0],					//nome client
												args[1],					//nome server
												Integer.parseInt(args[2]),	//capacità di download
												risorse			//stringhe (nome risorsa + n_parti)
											  );
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}		
	}//end main
	
}//end class ClientStarter()
