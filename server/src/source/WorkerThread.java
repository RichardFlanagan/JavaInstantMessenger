package source;


// Import Resources
import java.io.*;
import java.net.*;






/**
 * This thread is created by the server object when a
 * connection is accepted. This thread handles the connection,
 * and streams to the client.
 * 
 * @author ConorMarshall - A00199480
 * @author EmmetGill - A00196210 
 * @author GarethKennedy - A00192841
 * @author RichardFlanagan - A00193644
 * 
 * @version v0.5: 08-02-2014
 */
public class WorkerThread extends Thread {
	
	
// =============(VARIABLES)=============
	private String recievedMessage = "";
	private Socket s;
	private InputStream input;
	private OutputStream output;
	
	
	
	
	
	
// =============(CONSTRUCTOR)=============
	/**
	 * Constructor
	 * 
	 * @param s - The socket from the Server
	 */
	public WorkerThread(Socket s) {
		this.s = s;
	}

	
	
	
	

// =============(METHODS)=============
	/**
	 * (Thread) This method is called when thread.start() is called.
	 * 
	 */
	public void run(){
		try {
			while (true){
				
				// Establish Connections
				input = s.getInputStream();
				output = s.getOutputStream();
				output.flush();
		        
		        try {
		        	
		        	do{
		        		recievedMessage = (String)  new ObjectInputStream(input).readObject();
		        		//Server.displayMessage(recievedMessage, "Client");
		        		Server.sendMessage(recievedMessage, "Client");
		        	}while(true);//!message.split("-")[1].equals(" END"));

				} catch (ClassNotFoundException e) {	// If readObject() cannot be read:
					Server.displayMessage("User sent invalid data", "Error");
					if (Server.logging){e.printStackTrace();}
					
				} catch (SocketException e) {	// If connection is terminated by the client
					Server.displayMessage("Client Disconnected", "System");
					if (Server.logging){e.printStackTrace();}
					
					LogPrinter.printToLog("\n [SYS] { Client Disconnected }");
					
					input.close();
					output.close();
					s.close();
					return;		// End thread
				} 
			}
				
		} catch (IOException e) {	// If a connection cannot be established
			Server.displayMessage("Failed to connect to client", "Error");
			if (Server.logging){e.printStackTrace();}
		}
	} 
	
	
	
	
	/**
	 * (Thread) Returns output, the OutputStream between the thread and a client.
	 * 
	 * @return output - The OutputStream between the thread and a client.
	 */
	public OutputStream getOutputStream(){
		return output;
	}
	
	
}