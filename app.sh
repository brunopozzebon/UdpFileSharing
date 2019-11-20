#!/bin/bash
java -version
javac DebugServer.java
javac Server.java
javac Client.java
javac Test.java
javac Auxiliar.java
javac User.java

java Test
#Caso nao tenha instalado o jdk
#sudo apt update
#sudo apt-get install default-jdk
