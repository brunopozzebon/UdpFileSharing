import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class Auxiliar{
	public static String readFile(String fileName)throws IOException{
		String line = null;
		String text="";
		fileName="./MyFiles/"+fileName;

		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while((line = bufferedReader.readLine()) != null) {
			text+=line+"\n";
		}   
		bufferedReader.close();         
		return text;
	}

	public static String getMyFilesSeparedByComma(){
		File folder = new File("./MyFiles");
		String filesName ="";
		for(File file:folder.listFiles()){
			filesName = filesName+file.getName()+",";
		}
		if (filesName.length()>1) {
			filesName=filesName.substring(0,filesName.length()-1);
		}		
		return filesName;
	}

	public static void saveFile(String fileContent,String fileName){
		try{
			fileName = "./DownloadedFiles/"+fileName;
			BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileName));
			bufferWriter.write(fileContent);
			bufferWriter.close();
		}catch(IOException i){
			System.out.println("Error writing the file"+fileName);                  
		}
	}

	public static LinkedList<User> buildUsers(){
		String line = null;
		LinkedList<User> users = new LinkedList<User>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("config.txt"));
			while((line = bufferedReader.readLine()) != null) {
				String[] ipFiles = line.split(";");
				String ip = ipFiles[0];
				LinkedList<String> linkedFiles = new LinkedList<String>();
				if(ipFiles.length>1){
					String fileString = ipFiles[1];
					String[] files = fileString.split(",");
					for(String file : files){
						linkedFiles.add(file);
					}
				}
				users.add(new User(ip,linkedFiles));
			}   
			bufferedReader.close();         
		}catch(IOException error) {
			System.out.println("Error reading the config file");
			return null;                  
		}
		return users;
	}

	public static void removeFile(String pathName){
		if(pathName!=null){
			pathName = "./DownloadedFiles/"+pathName.trim();
			File file = new File(pathName);
			file.delete();
			System.out.println("File "+pathName+" deleted");
		}
	}

	public static void saveStructure(LinkedList<User> users){
		try{
			BufferedWriter bufferWriter = new BufferedWriter( new FileWriter("config.txt"));
			bufferWriter.write("");
			bufferWriter.write(listToFile(users));
			bufferWriter.close();
		}catch(IOException i){
			System.out.println(i.toString());
			System.out.println("Error writing the configuration file");                  
		}
	}

	private static String listToFile(LinkedList<User> list){
		String text = "";
		for(int i =0;i<list.size();i++){
			User user = list.get(i);
			text+=user.getIP()+";";
			for(String file : user.getFiles()){
				text+=file+",";
			}
			if(text.charAt(text.length()-1)==','){
				text = text.substring(0,text.length()-1);
			}
			text+="\n";
		}
		return text;
	}

	public static void printArray(LinkedList<User> users){
		for(User user : users){
			System.out.println(user.toString());
		}
	}

	public static void calculateTime(String method,long time){
		long difference = System.currentTimeMillis()-time;
		System.out.println("\tTIME: "+method+" | "+difference+" milliseconds");
	}
}