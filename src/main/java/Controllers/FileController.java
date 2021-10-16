package Controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileController {
	static String mainFolderName = "time_manager";
	String selectedFilePath;
	static File customDir  = new File(System.getProperty("user.home"),mainFolderName);
	
	public static String readFile(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			Scanner sc = new Scanner(file);
			String str = "";
			while(sc.hasNext()) {
				str = sc.nextLine();
				sb.append(str + "\n");
	        } 
			sc.close();
			
		}
		catch (IOException e) { 
			e.printStackTrace ();
		}
		return sb.toString();
		
	}
	
	public static File writeFile(File file, String str) {
		try {
		FileWriter writer = new FileWriter(file);
		writer.write(str);
		writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static String getAbsolutePath (File file) {
		return file.getAbsolutePath();
	}
	
	public static File fileChooser() {
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle ("Select File");
   
    	File selectedFile = fileChooser.showOpenDialog(new Stage());
   
    	return selectedFile;
	}

	
	public static void moveFileToDir(File file) throws IOException {
		if (!hasHomeDirectory()) {
			customDir.mkdirs();
		}
		FileUtils.copyFileToDirectory(file, customDir);
		
	}
	
	public static boolean hasHomeDirectory() {
		if (customDir.exists()) {
		    return true;
		} 
		else {
			return false;
		}
	}
	

}
