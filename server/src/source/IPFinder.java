package source;


// Import Resources
import java.net.*;






/**
 * This object retrieves the local IP address. This address
 * is given to the client in order to connect to a server
 * set up on this machine. Works most of the time. It might 
 * depend upon network configuration.
 * 
 * @author ConorMarshall - A00199480
 * @author EmmetGill - A00196210 
 * @author GarethKennedy - A00192841
 * @author RichardFlanagan - A00193644
 * 
 * @version v0.1: 17-01-2014
 */
public class IPFinder {

// =============(VARIABLES)=============
	private InetAddress inetAddress;
	private String ipAddress;
	private String ipName;
	
	
	
	
	
// =============(METHODS)=============	
	/**
	 * Constructor
	 * 
	 * @throws UnknownHostException
	 */
	public IPFinder() throws UnknownHostException{
		inetAddress = InetAddress.getLocalHost();	// Returns the ip information as an Inet Object
		ipAddress = inetAddress.getHostAddress();	// Print the ip of the Inet returned above
		ipName = inetAddress.getHostName();			// Print the name of this machine
	}
	
	
	
	
	/**
	 * Returns the local machines IP address as a String
	 * 
	 * @return ipAddress - The local machines IP address as a String
	 */
	public String getAddress(){
		return ipAddress;
	}
	
	
	
	
	/**
	 * Returns the local machines name as a String
	 * 
	 * @return ipName - The local machines name as a String
	 */
	public String getName(){
		return ipName;
	}


}