package textencryptor;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import java.awt.image.BufferedImage;
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

public class TextEncryptor {

private static final Scanner scan = new Scanner(System.in);	
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
					show("Goodbye!!");
					activeProgram=false;
				}
				default ->{
					show("Try again choosing available options...\n");
				}
				}
		}while(activeProgram);
	}
	
	/*
	 * Displays the main functionalities of the program
	 * and gets the choice from the user.
	 * @return int
	 */
	public static int choosingFromMainOptions() {
		show("Choose one of the options below");
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
		show("\n:::::::::::::CONTENT OF YOUR SECRET FILE:::::::::::::");
		show(content);
		show(":::::::::::::::END OF SECRET FILE:::::::::::::::::::::\n");
	}
	
	
	/*
	 * Shows Option Yes as 1 and true and No as 0 and false
	 * @returns boolean
	 */
	public static boolean yesNoOption() {
		show("[1] Yes");
		show("[2] No");
		int continueOption = readNumber();
		if(continueOption==1) {
			return true;
		}else {
			return false;
		}
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
	 * Prints any string in separate lines
	 * @param showing A String type
	 * @return No return value.
	 */
	public static void show(String showing) {
		System.out.println(showing);
	}
	
	
	/*
	 * Makes sure to get a number from the User.
	 * returns '0' if it's a letter
	 * @returns int
	 */
	public static int readNumber() {
		System.out.print("ENTER OPTION: ");
		int n;
		try{
			n = scan.nextInt();
		}catch(InputMismatchException e) {
			n = 0;
			show("[Error] Just numbers allowed.");
		}
		scan.nextLine();
		return n;
	}
	
	
	public static ArrayList<String> displayTextFiles() {
		show("\n-----Available text files-----");
		File file = new File("./src/main/files");
		String[] fileList = file.list();
		ArrayList <String> existingFiles = new ArrayList <String>();
		int count = 0;
		for(String str: fileList){
			if(str.substring(Math.max(0, str.length()-3)).equals("txt")){
				show("--->\t"+str);
				existingFiles.add(str);
				count++;
			}
		}
		if(count==0) {
			show("No files available");
		}
		show("------------------------------\n");
		if (existingFiles.size()==0) {
			return null;
		}else {
			return existingFiles;
		}
	}
	
	public static String choosingFile() {
		System.out.print("INSERT FILE NAME: ");
		return scan.nextLine();
	}
	
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
	public static boolean continueWithFileEdition(boolean fileExists) {
		if (fileExists) {
			return true;
		}else {
			show("Are you sure you want to add a new File?");
			show("[1] Yes");
			show("[2] No");
			int continueOption = readNumber();
			switch (continueOption){
				case 1 -> {
					return true;
				}
				case 2 -> {
					return false;
				}
				default ->{
					return continueWithFileEdition(fileExists);
				}
			}
				
		}
	}
	
	public static String gettingPassword() throws IOException {
		show("Waiting for your password in the Pop-up Window...");
		BufferedImage lockIcon = ImageIO.read(new File("./src/img/lock.png"));
        JPanel panel = new JPanel();
        JLabel labelIcon = new JLabel(new ImageIcon(lockIcon));
        JLabel labelPassword = new JLabel("Enter a password: ");

        JPasswordField passwordField = new JPasswordField(10);
        panel.add(labelIcon);
        panel.add(labelPassword);
        panel.add(passwordField);
        String[] options = new String[]{"OK", "Cancel"};
        int optionss = JOptionPane.showOptionDialog(null, panel, "Restricted Access",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        String passString = null;
        if (optionss == 0) // pressing OK button
        {
            char[] password = passwordField.getPassword();
            passString = new String(password);
        }
        return passString;
    }
	
	public static String getOldContentFromFile(String fileName) {
		if (!(fileName.substring(fileName.length()-3).equals(".txt"))){
			fileName=fileName+".txt";
		}
		try {
			return new String(Files.readAllBytes(Paths.get(("./src/main/files/"+fileName), new String[0])), Charset.forName("UTF-8"));
		} catch (IOException ex) {
			show("[Error] Problem reading file " + fileName);
			return null;
		} 
	}
	
	public static String getNewTextFromUser() {
		show("Add information you would like to encrypt:");
		return scan.nextLine();
	}
	
	public static void setNewContentForFile(String fileName, String finalContentForFile) {
		if (!(fileName.substring(fileName.length()-3).equals(".txt"))){
			fileName=fileName+".txt";
		}
		try{
			FileWriter fileWriter = new FileWriter("./src/main/files/"+fileName);
			fileWriter.write(finalContentForFile);
			fileWriter.close();
		}catch(Exception e) {
			show("[Error] It was impossible to edit the file.");
		}
        
	}

}
