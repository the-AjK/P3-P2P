taskkill /im rmiregistry.exe /F
taskkill /im java.exe /F
cd server
del *.class
cd ..
cd client
del *.class
cd ..
cd common
del *.class
cd ..
javac server/ServerStarter.java
javac client/ClientStarter.java
timeout 30