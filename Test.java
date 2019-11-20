import java.util.LinkedList;
import java.lang.Runtime;
public class Test{
public static void main(String[] args){
	Thread client = new Thread(new Client());
	Thread server = new Thread(new Server(client));
	server.start();
	client.start();
}
}