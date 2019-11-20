import java.net.*;
import java.util.LinkedList;

class Client implements Runnable {
	
	DatagramSocket cSocket;
	int clientPort = 29000;
	InetAddress cIpAddress;
	LinkedList<User> usersClient = Auxiliar.buildUsers();
	long timeSleeping = 4000;

	@Override
	public void run(){
		while(true){
		try{
			cSocket = new DatagramSocket();
				for (int i=0;i<usersClient.size();i++ ) {
					cIpAddress = InetAddress.getByName(usersClient.get(i).getIP());
					String myMessage = "PTA;";
					byte[] data = myMessage.getBytes();
					DatagramPacket clientSendPacket = new DatagramPacket(data,data.length, cIpAddress, clientPort);
					System.out.println("\nClient/PTA - Sending PTA requisition to: " +cIpAddress.getHostAddress());
					cSocket.send(clientSendPacket);
				}
				Thread.sleep(timeSleeping);	
			
		}catch(InterruptedException e){
			System.out.println("Client/Wake up");
		}catch(Exception e){
			System.out.println("Client/PTA ERROR"+e.toString());
		}
		}
	}
	
}