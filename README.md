# Udp File Sharing
This application allow computers to share .txt files with the UDP protocol, and a private application protocol, that control the syncronization of the files. You need to type the IPs addresses of all computer connected in the network.

## :file_folder: Requirements
1. Java
2. Javac

## :wrench: How it work
First we need a network of computers running this program (or something with the same protocol). Each computer will have a list of IP´s, registered manually in config.txt. This program will make continuous requests to each computer in the list, and wait for the response, as described in the protocol.

## :rocket: How to run it

Clone this repository
```bash
git clone https://github.com/brunopozzebon/udp-file-sharing.git
```
Go into the repository
```bash
cd udp-file-sharing
```
Create your network
Put all IP´s of the computer that you need to connect in config.txt.
Which IP need to be in own line, ending with ";". Like the example below:
127.0.0.1;
127.0.0.2;
These computers need to have the same application running, to make the service work well.

Compile the files
If you´re in a unix like OS, you can run the app.sh
(It´ll compile the java files and executed the program)
If you aren´t, you need to compile the files manually, and the, run ./Test in shellscript


