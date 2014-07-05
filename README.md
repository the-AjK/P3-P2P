P3-P2P
======

Simulazione di un sistema P2P di scambio risorse

Progetto Programmazione Concorrente e Distribuita A.S. 2013/2014

Copyright (c) 2014, Alberto Garbui (aka JK) www.ajk.altervista.org

All rights reserved.

Compile:

	$ javac server.ServerStarter
	$ javac client.ClientStarter
	
Usage RMI:

	rmiregistry							start RMI.	

Usage server:

	java server.ServerStarter <server_name>

	Commands:

	server_name							start a new server with the specified name.

	Examples:

	$ java server.ServerStarter Razorback1
	$ java server.ServerStarter Razorback2
	
Usage client:

	java client.ClientStarter <client_name> <server_to_connect> <download_limit> [resource_name resource_parts]

	Commands:

	client_name							specify the client's name.
	server_to_connect					specify the server to connect.
	download_limit						specify the download limit.

	Options:
	
	[resource_name resource_parts]		start the client with a preset resource named "resource_name" with "resource_parts" parts.
	
	Examples:

	$ java client.ClientStarter C1 Razorback1 3 A 1 B 4 C 6 E 2
	$ java client.ClientStarter C2 Razorback2 3 A 1 D 2 C 6


Description:

Il progetto consiste nella realizzazione di un sistema P2P per la condivisione e lo scambio di un
certo numero di risorse.
Il sistema è costituito da un certo numero di Server e da un certo numero di Client che si
connettono e si disconnettono dal sistema. Ogni risorsa RK è caratterizzata da un nome, R,
e da un numero K di parti in cui è scomposta. Ogni client ha un certo numero di risorse ed
ha una certa capacità di download D che gli permette di scaricare fino a D parti di risorse
contemporaneamente.
Un client C che si connette al sistema mette a disposizione l’insieme di risorse che possiede.
Un client C connesso può richiedere al sistema una risorsa RK di cui ha bisogno; il server S a cui
C è connesso dovrà indicare a C l’elenco dei client che sono attualmente connessi al sistema e in
possesso di una copia di RK. C quindi comunicherà direttamente con un certo numero di client
di questo elenco, scaricando concorrentemente più parti di RK da diversi clienti, in modo da
massimizzare l’utilizzo della propria capacità di download. Se durante la fase di scaricamento
di una parte di R, un cliente contattato si disconnette dal sistema, C deve poter reindirizzare
la richiesta di quella parte ad un altro dei client che la possiedono. Si suppone che un client
abbia infinita capacità di upload, cioè può servire le richieste di infinite parti di risorsa purché
ogni richiesta arrivi da un client diverso. Perciò, dato un client A che richiede una risorsa divisa
in 3 parti e un client B che è l’unico a possedere quella risorsa, non è possibile che A apra 3
connessioni con B per scaricare contemporaneamente le 3 parti della risorsa; A dovrà scaricare
da B la risorsa connettendosi con B per 3 volte in modo sequenziale.
Al fine di rendere la simulazione verificabile ogni scaricamento di una parte deve essere
simulato con un tempo di attesa costante per tutti i client ma configurabile in modo agevole a livello
di codice sorgente.
Il programma deve gestire richieste e scambi concorrenti di diverse risorse. L’applicazione
inoltre deve essere distribuita, cioè i vari client e server devono essere rappresentati da programmi
distinti che possono risiedere su JVM distinte.
