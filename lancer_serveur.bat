@echo off
if not exist bin mkdir bin
echo Compilation du projet...
javac -encoding UTF-8 -cp "lib/*" -d bin src/model/*.java src/protocol/*.java src/dao/*.java src/util/*.java src/server/*.java src/client/*.java src/ui/*.java
echo Demarrage du serveur...
java -cp "bin;lib/*" server.ReservationServer
pause