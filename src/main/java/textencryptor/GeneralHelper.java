package textencryptor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * This is a collection of helper methods for the main TextEditor file
 * it allows to receive Yes/No input from the User, print text,
 * read numbers from the user, and get a password from the user
 * 
 * @author giovannigando
 *
 */
public class GeneralHelper {
	
	private static final Scanner scan = new Scanner(System.in);	

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
		}else if(continueOption ==2) {
			return false;
		}else {
			show("Insert either '1' or '2'...");
			return yesNoOption();
		}
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
	/*
	 * Gets a password from user using a Swing Window 
	 * @returns String
	 */
	public static String gettingPassword() throws IOException {
		show("Waiting for your password in the Pop-up Window...");
		JPanel panel = new JPanel();
        JLabel labelPassword = new JLabel("Enter a password: ");
		BufferedImage lockIcon;
        try {
			lockIcon = ImageIO.read(TextEncryptor.class.getResource("lock.png"));
	        JLabel labelIcon = new JLabel(new ImageIcon(lockIcon));
	        panel.add(labelIcon);
		}catch (Exception e) {
			JLabel labelIcon = new JLabel("!!");
			panel.add(labelIcon);
		}
        JPasswordField passwordField = new JPasswordField(10);
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
	
}
