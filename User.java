import java.util.*;

public class User{
	private String IP;
	private LinkedList<String> files;

	public User(String IP, LinkedList<String> files){
		this.IP = IP;
		this.files = files;
	}

	public String getIP(){
		return IP;
	}

	public LinkedList<String> getFiles(){
		return new LinkedList<String>(files);
	}

	public void setFiles(LinkedList<String> newLinkedList){
		files=newLinkedList;
	}

	@Override
	public String toString(){
		return IP + " " +files;
	}

	public void addFile(String fileName){
		files.add(fileName);
	}

	public boolean contains(String file){
		if(files.contains(file)){
			return true;
		}
		return false;
	}

	

}