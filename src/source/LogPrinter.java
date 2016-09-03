package source;


// Import Resources
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;






/**
 * Prints out records of the conversation into a txt file.
 * 
 * @author ConorMarshall - A00199480
 * @author EmmetGill - A00196210 
 * @author GarethKennedy - A00192841
 * @author RichardFlanagan - A00193644
 * 
 * @version v0.1: 12-03-2014
 */
public class LogPrinter {
	
	
// =============(VARIABLES)=============
	static String outFile = "Logs.txt";
	static PrintWriter out;
	
	
	
	
	
	
// =============(METHODS)=============	
	
	
	/**
	 * Prints the string to the out file
	 * 
	 * @param t - The text to print to logs.
	 */
	public static void printToLog(String t){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile, true)))) {
			out.println(printTime() + t);
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * Prints the time and date at the start of a new session.
	 * 
	 */
	public static void printStartTime(){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile, true)))) {
			Date date = new Date();
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
			out.println("\r\n\r\n\r\n==================================================");
			out.println("Server booted: " + ft.format(date) + "\r\n");
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * Returns the time a message was sent.
	 * 
	 */
	public static String printTime(){
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
		return ("" + ft.format(date));
	}
	
	
}
