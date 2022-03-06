package textencryptor;

import java.util.ArrayList;
import java.util.Scanner;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * TextEncryptor --- program to create, edit and read
 * encrypted files inside the program folder
 * 
 * @author giovannigando
 *
 */

public class TextEncryptor extends GeneralHelper{

private static final Scanner scan = new Scanner(System.in);	
public static final String filesPath = "./";
	/**
	 * Choose to create, edit or read encrypted files
	 * @param args A string array containing the 
	 * command line arguments.
	 * @exception Any exception
	 * @return No return value.
	 */
	public static void main(String[] args) throws IOException{
		boolean activeProgram = true;
		
		show(":::::::::::::TEXT ENCRYPTOR PROGRAM:::::::::::::");
		do {
			int option = choosingFromMainOptions();
			switch(option) {
				case 1->{
					activeProgram = editingFile();
				}
				case 2->{
					activeProgram = readingFile();
				}
				case 3->{
					activeProgram=false;
				}
				default ->{
					show("Try again choosing available options...\n");
				}
				}
		}while(activeProgram);
		show("\nGoodbye!!");
	}
	
	/*
	 * Displays the main functionalities of the program
	 * and gets the choice from the user.
	 * @return int
	 */
	public static int choosingFromMainOptions() {
		show("\nChoose one of the options below");
		show("[1] Write content into File");
		show("[2] Read content from File");
		show("[3] Exit");
		return readNumber();
	}
	
	/*
	 * Allows user to add encrypted text into existing file
	 * or in case it doesn't exists, it automatically creates 
	 * a new text file.
	 */
	public static boolean editingFile() throws IOException {
		
		//Displaying the existing files and receiving file name for edition 
		ArrayList <String> existingFiles = displayTextFiles();
		String fileName = choosingFile();
		boolean fileExists = verifyFileExistence(fileName, existingFiles);
		
		//Possibility for User to Go Back from File Creation
		if(!continueWithFileEdition(fileExists)) {
			return true;
		}
		
		//Setting up encryptor and password
		String seed = gettingPassword();
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(seed);
		
		String oldContentFromFileEncrypted;
		String oldContentFromFileDecrypted = "";
		
		//Reading and Displaying Old Contents from file
		if(fileExists) {
			oldContentFromFileEncrypted = getOldContentFromFile(fileName);
			try {
				oldContentFromFileDecrypted = encryptor.decrypt(oldContentFromFileEncrypted);
			}catch (Exception e) {
				oldContentFromFileDecrypted = "";
			}
			showFileContent(oldContentFromFileDecrypted);
		}else {
			oldContentFromFileEncrypted = "";
		}
		
		//Setting New Contents for the file
		String finalContentForFile = "";
		String finalContentForFileEncrypted;
		String newContentForFile = getNewTextFromUser();
		if (oldContentFromFileEncrypted==null) {
			oldContentFromFileEncrypted = "";
		}
		if (oldContentFromFileEncrypted.length()!=0) {
			finalContentForFile = oldContentFromFileDecrypted + " \n";
		}
		finalContentForFile = finalContentForFile + newContentForFile;
		finalContentForFileEncrypted = encryptor.encrypt(finalContentForFile);
		setNewContentForFile(fileName, finalContentForFileEncrypted);
		show("Success Editing the File.\nDo you want to exit the program?");
		return !yesNoOption();
	}
	
	/*
	 * Shows the content of the decrypted file
	 */
	public static void showFileContent(String content) {
		show("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		show(":::::::::::::::  CONTENT OF YOUR SECRET FILE  ::::::::::::::::");
		show("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		show(content);
		show("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		show(":::::::::::::::       END OF SECRET FILE      ::::::::::::::::");
		show("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
	}
	
	/*
	 * Allows the user to read the Contents of an Encrypted file
	 * by entering the secret password used to encrypt the text.
	 */
	public static boolean readingFile() throws IOException {
		ArrayList <String> existingFiles = displayTextFiles();
		boolean fileExists;
		String fileName;
		do {
			fileName = choosingFile();
			fileExists = verifyFileExistence(fileName, existingFiles);
			//Possibility for User to Go Back from File Creation
			if(!fileExists) {
				show("File does not exist. Do you want to keep trying?");
				if(!yesNoOption()) {
					return true;
				}
			}
		}while(!fileExists);
		String seed = gettingPassword();
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(seed);
		
		String oldContentFromFileEncrypted;
		String oldContentFromFileDecrypted = "";
		oldContentFromFileEncrypted = getOldContentFromFile(fileName);
		try {
			oldContentFromFileDecrypted = encryptor.decrypt(oldContentFromFileEncrypted);
		}catch (Exception e) {
			oldContentFromFileDecrypted = "";
		}
		showFileContent(oldContentFromFileDecrypted);
		show("Success Reading the File.\nDo you want to exit the program?");
		return !yesNoOption();
	}

	
	/*
	 * Displays all the text files in the ./src/main/files directory
	 */
	public static ArrayList<String> displayTextFiles() {
		show("\n-----Available text files-----");
		File file = new File(filesPath);
		String[] fileList = file.list();
		ArrayList <String> existingFiles = new ArrayList <String>();
		int count = 0;
		if(fileList!= null) {
			for(String str: fileList){
				if(str.substring(Math.max(0, str.length()-3)).equals("txt")){
					show("--->\t"+str);
					existingFiles.add(str);
					count++;
				}
			}
		}else {
			if(count==0) {
				show("  *** No files available ***");
			}
		}
		show("------------------------------");
		if (existingFiles.size()==0) {
			return null;
		}else {
			return existingFiles;
		}
	}
	
	/*
	 * Receive file name from user
	 */
	public static String choosingFile() {
		System.out.print("\nINSERT FILE NAME: ");
		return scan.nextLine();
	}
	
	/*
	 * Verifies the existence of the file name with the name inserted by user
	 */
	public static boolean verifyFileExistence(String fileName, ArrayList<String> existingFiles) {
		if (existingFiles!=null) {
			for (String str : existingFiles) {
				if(str.equals(fileName) || (str.equals(fileName+".txt"))) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Make sure if User should continue with file Edition
	 * @returns a boolean, true for continuation
	 */
	public static boolean continueWithFileEdition(boolean fileExists) {
		if (fileExists) {
			return true;
		}else {
			show("Are you sure you want to add a new file?");
			return yesNoOption();
		}
	}
	
	
	/**
	 * Makes sure to get without errors the content of encrypted text
	 * @returns String
	 */
	public static String getOldContentFromFile(String fileName) {
		if (!(fileName.substring(fileName.length()-3).equals(".txt"))){
			fileName=fileName+".txt";
		}
		try {
			return new String(Files.readAllBytes(Paths.get((filesPath+fileName), new String[0])), Charset.forName("UTF-8"));
		} catch (IOException ex) {
			show("[Error] Problem reading file " + fileName);
			return null;
		} 
	}
	
	/**
	 * Gets the readable content from the user to insert to a file
	 * @return String
	 */
	public static String getNewTextFromUser() {
		show("Add information you would like to encrypt:");
		return scan.nextLine();
	}
	
	/**
	 * Using the name and content for a file, it simply edits all the content of the 
	 * existing text file.
	 * @param fileName
	 * @param finalContentForFile
	 */
	public static void setNewContentForFile(String fileName, String finalContentForFile) {
		if (!(fileName.substring(fileName.length()-3).equals(".txt"))){
			fileName=fileName+".txt";
		}
		try{
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(finalContentForFile);
			fileWriter.close();
		}catch(Exception e) {
			show(e.toString());
		}
        
	}

}
