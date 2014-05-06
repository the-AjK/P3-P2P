taskkill /im rmiregistry.exe /F
taskkill /im java.exe /F
sleep 5
start cmd.exe /C rmiregistry
sleep 5
start cmd.exe /C java server.ServerStarter Razorback1
start cmd.exe /C java client.ClientStarter C1 Razorback1 3 A 1 B 4 C 6
start cmd.exe /C java client.ClientStarter C2 Razorback1 4 A 3 C 3 C 6
start cmd.exe /C java client.ClientStarter C3 Razorback1 2 A 2 C 6
start cmd.exe /C java client.ClientStarter C4 Razorback1 2 A 2 

exit
