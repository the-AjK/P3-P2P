taskkill /im rmiregistry.exe /F
taskkill /im java.exe /F
sleep 5
start cmd.exe /C rmiregistry
sleep 2
start cmd.exe /C java server.ServerStarter Razorback1
start cmd.exe /C java server.ServerStarter Razorback2
sleep 2
start cmd.exe /C java client.ClientStarter C1 Razorback1 3 A 1 B 4 C 6 E 2
start cmd.exe /C java client.ClientStarter C2 Razorback2 3 A 1 D 2
start cmd.exe /C java client.ClientStarter C3 Razorback2 3 E 2
exit
