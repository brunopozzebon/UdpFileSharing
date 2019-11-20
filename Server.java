import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.LinkedList;
import java.lang.Runtime;

class Server implements Runnable {
	int door = 29000;		
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	
	byte[] receiveData = new byte[1024];
	String message= "";

	InetAddress ipAddress;
	int clientPort;

	LinkedList<User> users = Auxiliar.buildUsers();

	Thread client;
	int limitWaiting = 2000;
	int recountFactor = 1000;

	int n = users.size();
	int myRequisitions=1;
	int otherRequisitions=1;

	long countingTime;
	

	public Server(Thread client){
		this.client = client;
	}

	@Override
	public void run(){
		//If my program is closed, this hook save the data
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Programa sendo finalizado");
				Auxiliar.saveStructure(users);
			}
		});

		new java.util.Timer().schedule(
		new java.util.TimerTask(){
			@Override
			public void run(){
				float count = (myRequisitions*n)/(myRequisitions+otherRequisitions);
				if(count<1){
					System.out.println("\nServer/ To many requisitions"+myRequisitions+"/"+otherRequisitions);
					client.interrupt();
				}
			}
		},0,recountFactor
		);

		System.out.println("\n---------- Array ------------");
		Auxiliar.printArray(users);
		System.out.println("-----------------------------\n");

		try{
			receivePacket = new DatagramPacket(receiveData,receiveData.length);
			serverSocket = new DatagramSocket(door);
			
			while (true) {
				try{
					clearBuffer();
					System.out.println("Server/Information - Waiting for requisitions");
				    serverSocket.setSoTimeout(limitWaiting);
					serverSocket.receive(receivePacket);
					message = new String(receivePacket.getData(),0,receivePacket.getLength());
					countingTime = System.currentTimeMillis();
					
					if(message.indexOf(';')==-1){
						System.out.println("Server/ERROR: Invalid requisition sintax");
						continue;
					}

					String method = message.substring(0,message.indexOf(';')).trim().toUpperCase();
					ipAddress = receivePacket.getAddress();
					clientPort = receivePacket.getPort();
					System.out.println("");
					switch(method){
						case "PTA":
						otherRequisitions++;
					//If Someone, need the list of my files PROTOCOL("PTA");
						pta();
						break;
						case "PAE":
						otherRequisitions++;
					//If someone need a especific file PROTOCOL("PAE;nameFile.txt")
						pae();
						break;
						case "EAE":
						myRequisitions++;
					//If someone send me a file that I request PROTOCOL("EAE;sizeOfFile;nameOfFile;fileData")
						eae();
						break;
						case "ETA":
						myRequisitions++;
					//If someone send me the list of files that i request PROTOCOL("ETA;file1.txt,file2.txt,file3.txt")
						eta();
						break;
						default:
						System.out.println("Server/Warning - Método não reconhecido");
					}

					clearBuffer();
				}catch(SocketTimeoutException e){
					System.out.println("\nServer/Information - Waiting to much, wake up client");
					client.interrupt();
				}
			}
		}catch(Exception e){
			System.out.println("Server/ERROR Datagram Error\n"+e.toString());
		}
	}

	public void pta(){ 
		//Assembling the Application Protocol ("ETA;file1.txt,file2.txt")
		try{
			System.out.println("Server/PTA - Received: " + clientPort+" IP: " +ipAddress.getHostAddress());
			String myMessage = "ETA;"+Auxiliar.getMyFilesSeparedByComma();			
			byte[] sendData = myMessage.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, ipAddress, door);
			serverSocket.send(sendPacket);
			Auxiliar.calculateTime("PTA", countingTime);
		}catch(IOException e){}
	}

	public void pae(){
		System.out.println("Server/PAE - Received: " + clientPort+" IP: " +ipAddress.getHostAddress());
		if(message.length()<4){
			return;
		}

		String requestedFile = message.substring(message.indexOf(';')+1);
		requestedFile = requestedFile.trim();
		try{
			String file = Auxiliar.readFile(requestedFile);
			//Assembling the Application Protocol ("EAE;sizeOfFile;nameOfFile;fileData")
			String myMessage = "EAE;"+file.length()+";"+requestedFile+";"+file;
			byte[] sendData = myMessage.getBytes();

			System.out.println(myMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, ipAddress, door);
			serverSocket.send(sendPacket);
			Auxiliar.calculateTime("PAE", countingTime);
		}catch(IOException e){
			System.out.println("Server/PAE ERROR File "+requestedFile+" requested by "+ipAddress.getHostAddress()+" doesn`t exists");
		}
	}

	public void eae(){	
		if(trustIp()){
			
			if(!message.matches("^\\w{3};\\d+;[^;]+;[\\S\\s]*$")){
				System.out.println("Server/EAE: Invalid Sintax ");
				return;
			}
		
			String[] eaeRequisition = message.split(";");
			int size = Integer.valueOf(eaeRequisition[1]);//useLEss

			String nameFile = ipAddress.getHostAddress()+"_"+eaeRequisition[2];
			System.out.println("Server/EAE - Received EAE with file "+nameFile);
			String text ="";
			for (int i=3;i<eaeRequisition.length;i++) {
				text+=eaeRequisition[i];
			}

			if(size<text.length()){
				text = text.substring(0,size);
			}
			
			Auxiliar.saveFile(text,nameFile);
			users.get(getUserIndexByIp()).addFile(nameFile);
			Auxiliar.calculateTime("EAE", countingTime);
			}else{
			System.out.println("Server/EAE WARNING - Receive a requisition of a unknowed PC");
		}
	}

	public void eta(){ 
		if(trustIp()){
			if(!message.matches("^\\w{3};((([^;]*,)*[^;]+))?$")){
				System.out.println("Server/ETA: Invalid Sintax ");
				return;
			}

			message = message.trim();

			String[] etaRequisition = message.split(";");
			
			String[] userFiles =null;
			if(etaRequisition.length>1){
				userFiles = etaRequisition[1].split(",");
			}
			
			try{
				if(userFiles!=null){
					System.out.println("Server/ETA - Received ETA with file "+userFiles.toString());
				}
				User user = users.get(getUserIndexByIp());
				LinkedList<String> currentFiles = new LinkedList<String>();
				for (int i=0;userFiles!=null && i<userFiles.length;i++) {
					if(user.contains(ipAddress.getHostAddress()+"_"+userFiles[i])){
						System.out.println("Server/ETA - File "+userFiles[i]+" already included");
						currentFiles.add(ipAddress.getHostAddress()+"_"+userFiles[i]);
					}else{
						String file = userFiles[i];
						String myMessage = "PAE;"+file;
						byte[] sendData = myMessage.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, ipAddress, door);
						System.out.println("Server/ETA - Sending PAE requisition to: " +ipAddress.getHostAddress());
						serverSocket.send(sendPacket);
					}
					
				}
				removeOldFiles(currentFiles,user);
				Auxiliar.calculateTime("ETA", countingTime);
			}catch(IOException e){}
		}else{
			System.out.println("Server/ETA WARNING - Receive a requisition of a unknowed PC");
		}
	}

	public void removeOldFiles(LinkedList<String> currentFiles,User user){
		LinkedList<String> removedFiles = compareFiles(currentFiles, user.getFiles());
		user.setFiles(currentFiles);
		for (int i=0;i<removedFiles.size() ;i++ ) {
			Auxiliar.removeFile(removedFiles.get(i));
		}
	}

	public LinkedList<String> compareFiles(LinkedList<String> currentFiles,LinkedList<String> oldFiles){
		LinkedList<String> removed = new LinkedList<String>();
		for (int i=oldFiles.size()-1;i>=0;i-- ) {
			if(!currentFiles.contains(oldFiles.get(i))){
				String removedFile = oldFiles.remove(i);
				removed.add(removedFile);
			}
		}
		return removed;
	}

	public boolean trustIp(){
		String ip =  ipAddress.getHostAddress();
		System.out.println(ip);
		for (int i=0; i<users.size();i++ ) {
			if(users.get(i).getIP().equals(ip)){
				return true;
			}
		}
		return false;
	}

	public int getUserIndexByIp(){
		String ip =  ipAddress.getHostAddress();
		for (int i=0; i<users.size();i++ ) {
			if(users.get(i).getIP().equals(ip)){
				return i;
			}
		}
		return -1;
	}

	private void clearBuffer(){
		receiveData = new byte[2048];
		message="";
	}

}
