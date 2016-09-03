package source;

import javax.swing.JOptionPane;




/**
 * Launches the Client.
 * 
 * @author ConorMarshall - A00199480
 * @author EmmetGill - A00196210 
 * @author GarethKennedy - A00192841
 * @author RichardFlanagan - A00193644
 * 
 * @version v0.5: 08-02-2014
 */
public class ClientLaunch {
	
	/**
	 * Main sets up and runs the Client object.
	 * Requires a server object already running.
	 * 
	 * If you wish to test this locally, use these values as parameters in client:
	 * 		("127.0.0.1", 2001)
	 * The first is the default localhost IP, and the second is the testing port number.
	 * 
	 * If you wish to test this across a network, input the server IP (cmd-> ipconfig),
	 * and input corresponding port numbers into both (default is 2001 unless otherwise changed).
	 * 
	 */
public static void main(String[] args){
		
		String targetIP = (String)JOptionPane.showInputDialog("Please enter the server ip:\n", "127.0.0.1");
		int targetPort = Integer.parseInt(JOptionPane.showInputDialog("Please enter the server port:\n", "2001"));
		@SuppressWarnings("unused")
		Client client = new Client(targetIP, targetPort);
	}


}
