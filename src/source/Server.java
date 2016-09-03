package source;


// Import Resources
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;






/**
 * The server application for the Instant Messenger.
 * Waits for incoming connections and creates a thread
 * which handles the connection.
 * Builds the GUI for the server.
 * 
 * @author ConorMarshall - A00199480
 * @author EmmetGill - A00196210 
 * @author GarethKennedy - A00192841
 * @author RichardFlanagan - A00193644
 * 
 * @version v0.6: 23-03-2014
 */
public class Server extends JFrame{
	// Default Serial Version UID
	private static final long serialVersionUID = 1L;
	
	
// =============(Variables)=============
	// GUI
	private static final String TITLE = "IM Server v0.6";
	private static final int FRAME_WIDTH = 200;
	private static final int FRAME_HEIGHT = 300;
	
	private JPanel window;

	private JPanel infoPanel;
	private JPanel creditsPanel;
	private JLabel credits;
	private JPanel serverInfoPanel;
	private static JLabel serverInfo;
	private JLabel connectionsWindow;
	
	private JPanel chatPanel;
	private static JTextArea chatWindow;
	private JScrollPane scrollPane;
	private static JTextField userText;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu prefMenu;
	private JMenu helpMenu;
	
	private String chatCommands = "<html> Enter the following commands to get the desired results: <br/>"
			+ "<br/>/help - Brings up all the commands you can enter."
			+ "<br/>/exit - Terminates the current connection."
			+ "<br/>/s10 - Sets the font size to 10."
			+ "<br/>/s12 - Sets the font size to 12."
			+ "<br/>/s14 - Sets the font size to 14."
			+ "<br/>/s16 - Sets the font size to 16."
			+ "<br/>/s18 - Sets the font size to 18."
			+ "<br/>/fse - Sets the font face to Serif."
			+ "<br/>/fss - Sets the font face to Sans Serif."
			+ "<br/>/fms - Sets the font face to Monospaced."
			+ "<br/>/fda - Sets the font face to Dialog."
			+ "<br/>/fdi - Sets the font face to Dialog Input."
			+ "</html>";
	
	private String manual = "Type in the chat box under the display window to input your message. "
			+ "Hit enter to send.\nChange font and display name in the edit menu.\n"
			+ "Information on your client/server is displayed on the left.\n"
			+ "Current connections are displayed above the information panel." ;
	
	
	// Colours
	private final Color DEFAULTGREY = new Color(238, 238, 238);
	private final Color BLACK = new Color(0 , 0 , 0);
	private final Color WHITE = new Color(255 , 255 , 255);
	private final Color GREY = new Color(50 , 50 , 50);
	private final Color RED = new Color(136 , 0 , 21);
	private final Color GREEN = new Color(0 , 255 , 0);
	private final Color GOLD = new Color(255 , 201 , 14);
	
	
	// Font
	final int DEFAULT_SIZE = 12;
	
	final String SERIF = "Serif";
	final String SANS_SERIF = "SansSerif";
	final String MONOSPACE = "Monospaced";
	final String DIALOG = "Dialog";
	final String DIALOG_INPUT = "DialogInput";
	
	private int fontSize = DEFAULT_SIZE;
	private String fontFace = "SansSerif";
	private int fontStyle = Font.PLAIN;
	public Font chatFont = new Font(fontFace, fontStyle, fontSize);
	
	private JRadioButtonMenuItem serifButton;
	private JRadioButtonMenuItem sansSerifButton;
	private JRadioButtonMenuItem monospacedButton;
	private JRadioButtonMenuItem dialogButton;
	private JRadioButtonMenuItem dialogInputButton;
	
	private JRadioButtonMenuItem smallestButton;
	private JRadioButtonMenuItem smallButton;
	private JRadioButtonMenuItem mediumButton; 
	private JRadioButtonMenuItem largeButton;
	private JRadioButtonMenuItem largestButton;
	
	
	// Network
	private boolean listening = true;
	private static ArrayList<WorkerThread> threadList = new ArrayList<WorkerThread>();
	public static boolean logging = false;
	
	private static int port = 2001;
	private static int currentConnections = 0;
	
	private IPFinder ipFinder;
	private static String ipAddress;
	private static String ipName;
	
	private static String name = "Server";
	
	
	
	
	
	
// =============(CONSTRUCTOR)=============
	
	/**
	 * (Constructor) Sets up the JFrame, calls the createComponent method
	 * 		and the run method.
	 * 
	 */
	public Server(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(TITLE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setVisible(true);
		setResizable(false);
		setIconImage(new ImageIcon("SmallServerIcon.jpg").getImage());
		
		createComponents();
		
		run();
	}
	
	
	
	
	
	
// =============(GUI)=============
	
	/** 
	 * (GUI) Creates the components requires to build the application.
	 * 
	 */
	private void createComponents(){
		
		window = new JPanel();
		window.setBackground(new Color(50,50,50));
		
		createMenuBar();
		createChatPanel();
		createInfoPanel();
		
		add(window);
		pack();		// Sets the frame size to the preferred size of all the components
		
		LogPrinter.printStartTime();
	}
	
	
	
	
	/**
	 * (GUI) Builds the chat panel which includes the inputField and chatWindow
	 * 
	 */
	private void createChatPanel(){
		chatPanel = new JPanel(new BorderLayout());
		chatPanel.setBorder(new TitledBorder(new EtchedBorder(), "Chat"));
	
		userText = new JTextField();
		userText.setFont(chatFont);
		
		
		/**
		 * (ActionListener) Action Listener for userText field. Calls upon pressing ENTER.
		 * Sends the message to the thread to be processed, prints out the message to the
		 * server, and resets the userText field.
		 * 
		 * @author Richard Flanagan
		 */
		class userTextListener implements ActionListener{
			public void actionPerformed(ActionEvent event){
				if (!isCommand((String)(event.getActionCommand()))){
					sendMessage((String)(event.getActionCommand()), "Server");
				}
				else{
					userText.setText("");
				}
			}
		}
		
		userText.addActionListener(new userTextListener());
		userText.setPreferredSize(new Dimension(300, 100));
		chatPanel.add(userText, BorderLayout.SOUTH);
		
		chatWindow = new JTextArea();
		chatWindow.setFont(chatFont);
		chatWindow.setLineWrap(true);
		chatWindow.setEditable(false);
		
		scrollPane = new JScrollPane(chatWindow);
		scrollPane.setPreferredSize(new Dimension(300, 403));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setAutoscrolls(true);
		chatPanel.add(scrollPane, BorderLayout.CENTER);
		
		window.add(chatPanel,BorderLayout.WEST);
	}
	
	
	
	
	/**
	 * (GUI) Builds the info panel which includes the connections area, server info and credits
	 * 
	 */
	private void createInfoPanel(){
		infoPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(new TitledBorder(new EtchedBorder(), "Information"));
		
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("ServerIcon.jpg"));
			connectionsWindow = new JLabel(new ImageIcon(myPicture));
		} catch (IOException e) {
			if (logging) { e.printStackTrace(); }
		}
		
		scrollPane = new JScrollPane(connectionsWindow);
		infoPanel.add(scrollPane, BorderLayout.NORTH);
		
		createServerPanel();
		
		creditsPanel = new JPanel();
		creditsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Credits"));
		credits = new JLabel();
		
		credits.setText("<html>Created by:	<br/>"
				+ "-Richard Flanagan <br/>"
				+ "-Emmet Gill <br/>"
				+ "-Gareth Kennedy <br/>"
				+ "-Conor Marshall <br/>"
				+ "\u00a9 2014 </html>");
		creditsPanel.add(credits);
		infoPanel.add(creditsPanel, BorderLayout.SOUTH);
		window.add(infoPanel,BorderLayout.EAST);
	}
	
	
	
	
	/**
	 * (GUI) Creates the serverInfo panel
	 * 
	 */
	private void createServerPanel(){
		serverInfoPanel = new JPanel();
		serverInfoPanel.setBorder(new TitledBorder(new EtchedBorder(), "Server Info"));
		serverInfo = new JLabel();
		
		try {
			ipFinder = new IPFinder();
			ipAddress = ipFinder.getAddress();
			ipName = ipFinder.getName();
		} catch (UnknownHostException e) {
			ipAddress = "UnknownHostException";
			ipName = "UnknownHostException";
		}
		
		serverInfo.setText("<html>Server Name: " + name
				+ "<br/>Machine Name: " + ipName
				+ "<br/>IP Address: " + ipAddress
				+ "<br/>Port Number: " + port
				+ "<br/>Current Connections: " + currentConnections
				+ "<br/></html>");
		serverInfoPanel.add(serverInfo);
		infoPanel.add(serverInfoPanel, BorderLayout.CENTER);
	}
	
	
	
	
	/**
	 * (GUI) Creates the top menu bar and menus.
	 * 
	 */
	private void createMenuBar(){
		menuBar = new JMenuBar();
		
		createFileMenu();
		createEditMenu();
		createPrefMenu();
		createHelpMenu();

		setJMenuBar(menuBar);
	}
	
	
	
	
	/**
	 * (GUI) Create the File menu on the MenuBar
	 * 
	 */
	private void createFileMenu(){
		// File
		fileMenu = new JMenu("File");
			// Exit
			class ExitItemListener implements ActionListener{
				public void actionPerformed(ActionEvent event){
					System.exit(0);
				}
			}
			JMenuItem exitItem = new JMenuItem("Exit");
			exitItem.addActionListener(new ExitItemListener());
			fileMenu.add(exitItem);
		menuBar.add(fileMenu);
	}
	
	
	
	
	/**
	 * (GUI) Create the Edit menu on the MenuBar
	 * 
	 */
	private void createEditMenu(){
		// Edit
		editMenu = new JMenu("Edit");
			
			// Set Face
			JMenu faceMenu = new JMenu("Set Face");
				ButtonGroup faceGroup = new ButtonGroup();
				// Serif
				serifButton = createFaceItem("Serif", SERIF);
				faceGroup.add(serifButton);
				faceMenu.add(serifButton);
				// SansSerif
				sansSerifButton = createFaceItem("SansSerif", SANS_SERIF);
				sansSerifButton.setSelected(true);
				faceGroup.add(sansSerifButton);
				faceMenu.add(sansSerifButton);
				// Monospaced
				monospacedButton = createFaceItem("Monospaced", MONOSPACE);
				faceGroup.add(monospacedButton);
				faceMenu.add(monospacedButton);
				// Dialog
				dialogButton = createFaceItem("Dialog", DIALOG);
				faceGroup.add(dialogButton);
				faceMenu.add(dialogButton);
				// DialogInput
				dialogInputButton = createFaceItem("DialogInput", DIALOG_INPUT);
				faceGroup.add(dialogInputButton);
				faceMenu.add(dialogInputButton);
			editMenu.add(faceMenu);
			
			
			// Set Size
			JMenu sizeMenu = new JMenu("Set Size");
				ButtonGroup sizeGroup = new ButtonGroup();
				// Smallest
				smallestButton = createSizeItem("10", 10);
				sizeGroup.add(smallestButton);
				sizeMenu.add(smallestButton);
				// Small
				smallButton = createSizeItem("12 (default)", DEFAULT_SIZE);
				smallButton.setSelected(true);
				sizeGroup.add(smallButton);
				sizeMenu.add(smallButton);
				// Medium
				mediumButton = createSizeItem("14", 14);
				sizeMenu.add(mediumButton);
				sizeGroup.add(mediumButton);
				// Large
				largeButton = createSizeItem("16", 16);
				sizeGroup.add(largeButton);
				sizeMenu.add(largeButton);
				// Largest
				largestButton = createSizeItem("18", 18);
				sizeGroup.add(largestButton);
				sizeMenu.add(largestButton);
			editMenu.add(sizeMenu);
		menuBar.add(editMenu);
				
	}
	
	
	
	
	/**
	 * (GUI) Create the Preferences menu on the MenuBar
	 * 
	 */
	private void createPrefMenu(){
		// Preferences
		prefMenu = new JMenu("Preferences");
		
		// Set Theme
		JMenu setTheme = new JMenu("Set Theme"); 
		ButtonGroup themes = new ButtonGroup();
			//Normal Theme
			JRadioButtonMenuItem normalTheme = (createThemeItem("Normal"));
			normalTheme.setSelected(true);
			themes.add(normalTheme);
			setTheme.add(normalTheme);
			//Green Theme
			JRadioButtonMenuItem greenTheme = (createThemeItem("Green"));
			themes.add(greenTheme);
			setTheme.add(greenTheme);
			//Red Theme
			JRadioButtonMenuItem redTheme = (createThemeItem("Red"));
			themes.add(redTheme);
		setTheme.add(redTheme);
		
		// Set Name
		JMenuItem setName = new JMenuItem("Set Name");
		class SetNameListener implements ActionListener{
			public void actionPerformed(ActionEvent event){
				String oldname = name;
				name = JOptionPane.showInputDialog("This is how you will appear in chat: ");
				
				serverInfo.setText("<html>Server Name: " + name
						+ "<br/>Machine Name: " + ipName
						+ "<br/>IP Address: " + ipAddress
						+ "<br/>Port Number: " + port
						+ "<br/>Current Connections: " + currentConnections
						+ "<br/></html>");
				
				String nameMessage = ("\n [SYS] { " + oldname + " has change their name to " + name + " }");
				sendMessage(nameMessage, "Client");
			}
		}
		ActionListener nameListener = new SetNameListener();
		setName.addActionListener(nameListener);
		
			
		prefMenu.add(setTheme);
		prefMenu.add(setName);
		menuBar.add(prefMenu);
	}
	
	
	
	
	/**
	 * (GUI) Create the Help menu on the MenuBar
	 * 
	 */
	private void createHelpMenu(){
		// Help
		helpMenu = new JMenu("Help");
			// Show Chat Commands
			JMenuItem showChatCommands = new JMenuItem("Chat Commands");
			
			class showChatCommandsListener implements ActionListener{
				public void actionPerformed(ActionEvent event){
					JOptionPane.showMessageDialog(null, chatCommands, "Chat Commands ", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			ActionListener commandsListener = new showChatCommandsListener();
			showChatCommands.addActionListener(commandsListener);
			
			helpMenu.add(showChatCommands);
			
			// Manual
			JMenuItem showManual = new JMenuItem("Manual");
			
			class ShowManualListener implements ActionListener{
				public void actionPerformed(ActionEvent event){
					JOptionPane.showMessageDialog(null, manual, "Manual ", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			ActionListener showManualListener = new ShowManualListener();
			showManual.addActionListener(showManualListener);
			
			helpMenu.add(showManual);
		menuBar.add(helpMenu);
	}

	
	
	
	/**
	 * (GUI) Creates a radio button for sizeItem, creates and assigns an ActionListener
	 * 
	 * @param name - The label to appear on the button
	 * @param size - The size of the text
	 * @return The buttonMenuItem
	 */
	private JRadioButtonMenuItem createSizeItem(String name, final int size){
		class SizeItemListener implements ActionListener{
			public void actionPerformed(ActionEvent event){
				fontSize = size;
				chatFont = new Font(fontFace, fontStyle, fontSize);
				userText.setFont(chatFont);
				chatWindow.setFont(chatFont);
			}
		}
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(name);
		ActionListener listener = new SizeItemListener();
		item.addActionListener(listener);
		return item;
	}
	
	
	
	
	/**
	 * (GUI) Creates a radio button for faceItem, creates and assigns an ActionListener
	 * 
	 * @param name - The label to appear on the button
	 * @param face - The face style of the text
	 * @return The buttonMenuItem
	 */
	private JRadioButtonMenuItem createFaceItem(String name, final String face){
		class FaceItemListener implements ActionListener{
			public void actionPerformed(ActionEvent event){
				fontFace = face;
				chatFont = new Font(fontFace, fontStyle, fontSize);
				userText.setFont(chatFont);
				chatWindow.setFont(chatFont);
			}
		}
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(name);
		ActionListener listener = new FaceItemListener();
		item.addActionListener(listener);
		return item;
	}
	
	
	
	
	/**
	 * (GUI) Creates a radio button for setTheme, creates and assigns an ActionListener
	 * 
	 * @param name - The label to appear on the button
	 * @param theme - The theme to use
	 * @return The buttonMenuItem
	 */
	private JRadioButtonMenuItem createThemeItem(final String name){
		class ThemeItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				if (name.equals("Normal"))
				{
					setDefaultTheme();
				}
				else if (name.equals("Red"))
				{
					setTheme(GOLD , RED);
				}
				else if (name.equals("Green"))
				{
					setTheme(GREEN , BLACK);
				}
			}
		}
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(name);
		item.addActionListener(new ThemeItemListener());
		return item;
	}
	
	
	
	
	/**
	 * (GUI) Changes the theme of the server.
	 * 
	 * @param baseColour
	 * @param accentColour
	 */
	private void setTheme (Color baseColour , Color accentColour)
	{
		window.setBackground(baseColour);
		connectionsWindow.setForeground(baseColour);
		connectionsWindow.setBackground(BLACK);
		creditsPanel.setBackground(accentColour);
		credits.setForeground(baseColour);
		infoPanel.setBackground(accentColour);
		infoPanel.setForeground(baseColour);
		serverInfoPanel.setBackground(accentColour);
		serverInfo.setForeground(baseColour);
		chatWindow.setBackground(BLACK);
		chatWindow.setForeground(baseColour);
		chatPanel.setBackground(accentColour);
		userText.setBackground(BLACK);
		userText.setForeground(baseColour);
		
		menuBar.setBackground(accentColour);
		fileMenu.setForeground(baseColour);
		editMenu.setForeground(baseColour);
		prefMenu.setForeground(baseColour);
		helpMenu.setForeground(baseColour);
	}
	
	
	
	
	/**
	 * (GUI) Resets to the default theme.
	 * 
	 * @param baseColour
	 * @param accentColour
	 */
	private void setDefaultTheme (){
		window.setBackground(GREY);
		infoPanel.setBackground(DEFAULTGREY);
		chatPanel.setBackground(DEFAULTGREY);
		
		connectionsWindow.setForeground(BLACK);
		connectionsWindow.setBackground(WHITE);
		
		serverInfo.setForeground(BLACK);
		serverInfoPanel.setBackground(DEFAULTGREY);
		
		credits.setForeground(BLACK);
		creditsPanel.setBackground(DEFAULTGREY);
		
		chatWindow.setForeground(BLACK);
		chatWindow.setBackground(WHITE);
		
		userText.setForeground(BLACK);
		userText.setBackground(WHITE);
		
		menuBar.setBackground(DEFAULTGREY);
		fileMenu.setForeground(BLACK);
		editMenu.setForeground(BLACK);
		prefMenu.setForeground(BLACK);
		helpMenu.setForeground(BLACK);
	}
	
	
	
	
	
	
// =============(SERVER)=============
	
	
	/**
	 * (Server) Listens for a connection to the ServerSocket.
	 * When one is heard, it is accepted and a thread is created to handle it.
	 * 
	 */
	public void run(){
		currentTime();
		try (ServerSocket server = new ServerSocket(port)) {
		    while (listening) {
		    	
		    	WorkerThread t = new WorkerThread(server.accept());
		    	threadList.add(t);
		    	t.start();
		    	
		    	LogPrinter.printToLog(" [SYS] { Client Connected }");
		    	displayMessage("Client Connected", "System");
		    	calcCurrentConnections();
		    	
		    }
		} catch (IOException e) {	// If a connection cannot be made.
			displayMessage("Could not connect to a client", "Error");
			if (logging){e.printStackTrace();}
		}
	}
	
	
	
	
	/**
	 * (Server) Takes a message and a sender, and appends the message to 
	 * the chatWindow. Style depends on the sender.
	 * 
	 * @param text - String to be added to the textArea
	 * @param sender - The "speaker" of the message. Exceptions are Errors,
	 * 					network events are System, server announcements are
	 * 					Server, and chat messages are Client.
	 */
	public static void displayMessage(String text, String sender){
		if (sender.equals("Server")){
			chatWindow.append(text);
		}
		else if (sender.equals("Client")){
			chatWindow.append(text);
		}
		else if (sender.equals("Error")){
			chatWindow.append("\n [ERROR] ---" + text + "---");
		}
		else if (sender.equals("System")){
			chatWindow.append("\n [SYS] { " + text + " }");
		}
	}
	
	
	
	
	/**
	 * (Server) Broadcasts the message to the clients
	 * 
	 * @param message - The message to send to the clients
	 */
	public static void sendMessage(String message, String sender){
		userText.setText("");
		String toSend = "error";
		if (sender.equals("Server")){
			toSend = ("\n [H] " + name + " - " + message);
			displayMessage(toSend, sender);
			LogPrinter.printToLog("\n [H] " + name + " - " + message);
		}
		else if (sender.equals("Client")){
			toSend = (message);
			displayMessage(toSend, sender);
			LogPrinter.printToLog(message);
		}
		else if (sender.equals("System")){
			toSend = (message);
			displayMessage(toSend, sender);
			LogPrinter.printToLog(message);
		}
		
		try{
			for (WorkerThread thread : threadList){
				if (thread.isAlive()){	// If thread is still running a connection
					
					try {
						// Send the message to the thread to be sent to the client
						new ObjectOutputStream(thread.getOutputStream()).writeObject(toSend);
						thread.getOutputStream().flush();
						calcCurrentConnections();
		
					} catch (IOException e) {	// If sending fails
						displayMessage("Message could not be sent", "Error");
						if (logging){e.printStackTrace();}
					}
		
				}else{
					threadList.remove(thread);
				}
			}
		} catch(ConcurrentModificationException e){
			if (logging){e.printStackTrace();}
		}
	}
	
	
	
	
	/**
	 * (Server) Calculates the current number of clients
	 * connected to the server and updates the serverInfo panel.
	 * 
	 */
	public static void calcCurrentConnections(){
		currentConnections = threadList.size();
		serverInfo.setText("<html>Server Name: " + name
				+ "<br/>Machine Name: " + ipName
				+ "<br/>IP Address: " + ipAddress
				+ "<br/>Port Number: " + port
				+ "<br/>Current Connections: " + currentConnections
				+ "<br/></html>");
	}
	
	
	
	
	/**
	 * Time Thread
	 */
	public void currentTime(){
		Thread clock = new Thread(){
			public void run(){
				for(;;){
					Calendar cal = new GregorianCalendar();
					int second = cal.get(Calendar.SECOND);
					int minute = cal.get(Calendar.MINUTE);
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					setTitle(TITLE + "    " +hour+":"+minute+":"+second );
					try{
						sleep(1000);	
					} catch (InterruptedException e){
						if (logging){ e.printStackTrace(); }
					}
					
				}
			}
		};
		clock.start();
	}
	
	
	
	/**
	 * This method will check whether the message being added is a console command, and if so
	 * it will send it to the listener, and change the setting of the client automatically. 
	 * 
	 * @param text - The text that is being inputed into the client.
	 */
	public boolean isCommand(String text){
		if(text.equals("/exit")){
			System.exit(0);
			return true;
		}
		
		else if(text.equals("/help")){
			JOptionPane.showMessageDialog(null, chatCommands, "Chat Commands ", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		
		else if(text.equals("/s10")){
			fontSize = 10;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			return true;
		}
		else if(text.equals("/s12")){
			fontSize = 12;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			return true;
		}
		else if(text.equals("/s14")){
			fontSize = 14;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			return true;
		}
		else if(text.equals("/s16")){
			fontSize = 16;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			return true;
		}
		else if(text.equals("/s18")){
			fontSize = 18;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			return true;
		}
		
		else if(text.equals("/fse")){
			fontFace = SERIF;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			serifButton.setSelected(true);
			return true;
		}
		
		else if(text.equals("/fss")){
			fontFace = SANS_SERIF;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			sansSerifButton.setSelected(true);
			return true;
		}
		
		else if(text.equals("/fms")){
			fontFace = MONOSPACE;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			monospacedButton.setSelected(true);
			return true;
		}
		
		else if(text.equals("/fdi")){
			fontFace = DIALOG;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			dialogButton.setSelected(true);
			return true;
		}
		
		else if(text.equals("/fdl")){
			fontFace = DIALOG_INPUT;
			chatFont = new Font(fontFace, fontStyle, fontSize);
			userText.setFont(chatFont);
			chatWindow.setFont(chatFont);
			dialogInputButton.setSelected(true);
			return true;
		}
		
		else{
			return false;
		}
		
	}


}