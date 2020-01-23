#Udp File Sharing
This application allow computers to share .txt files with the UDP protocol, and a private application protocol, that control the syncronization of the files. You need to type the IPs addresses of all computer connected in the network.

## :file_folder: Requirements
1. Java
2. Javac

## :rocket: How it work
Each instance of the this program will make continuous requisitions to all computers IP typed in the config.txt, that will send you a properly response.
FORMAT OF THE PROTOCOL
| Message | What it mean |
|"PTA" | One computer requ | 

## :rocket: How to run it
```bash
# Clone this repository
git clone https://github.com/brunopozzebon/udp-file-sharing.git

# Go into the repository
cd udp-file-sharing

# Create your network
Put all IP´s of the computer that you need to connect in config.txt.
Which IP need to be in own line, ending with ";". Like the example below:
127.0.0.1;
127.0.0.2;
These computers need to have the same application running, to make the service work well.

#Compile the files
If you´re in a unix like OS, you can run the app.sh
(It´ll compile the java files and executed the program)
If you aren´t, you need to compile the files manually, and the, run ./Test in shellscript

```

