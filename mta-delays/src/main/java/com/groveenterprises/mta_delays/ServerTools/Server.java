package com.groveenterprises.mta_delays.ServerTools;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.*;

import com.groveenterprises.mta_delays.ConfigurationTools.*;
import com.groveenterprises.mta_delays.DatabaseTools.DatabaseOperations;
import com.groveenterprises.mta_delays.HelperClasses.*;

//should add a server window to stop / start server.
public class Server extends JFrame {
	
	private static long MAXIMUM_AGE_FROM_DATABASE = 300_000; //in milliseconds

	private static final long serialVersionUID = 1L;
	protected JFrame frame = new JFrame();
	protected JMenuBar menuBar = new JMenuBar();
	protected JTextArea textArea = new JTextArea();

	protected static final int FRAME_WIDTH = 400;
	protected static final int FRAME_HEIGHT = 400;

	public Server() {
		super("Server");
		initializeGUI();
		activateServer();
	}

	private void initializeGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setJMenuBar(menuBar);

		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		frame.add(scrollPane);

		frame.setJMenuBar(menuBar);
		menuBar.add(createFileMenu());

		frame.setVisible(true);
	}

	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createFileExitItem());
		return menu;
	}

	/**
	 * Creates the File->Exit menu item and sets its action listener.
	 */
	protected JMenuItem createFileExitItem()
	{
		JMenuItem item = new JMenuItem("Exit");      
		item.addActionListener(e -> System.exit(0));
		return item;
	}


	/**
	 * Appends the message to text area in GUI
	 * @param message
	 */
	public void displayMessage(String message) {
		textArea.append(message + "\n");
	}

	private void activateServer() {
		try {
			// Create a server socket
			ServerSocket serverSocket = new ServerSocket(Configuration.getServerSocketPort());
			displayMessage("Server activated on port: " + serverSocket.getLocalPort());
			while (true) {
				// Listen for a connection request
				Socket socket = serverSocket.accept();
				// Create and start a new thread for the connection
				new Thread(new ClientHandler(socket)).start();
			}
		} catch (BindException e) {
			displayMessage("Port is already in use. Please try a different port for server");
			e.printStackTrace();
		}
		catch (IOException ioe) {
			displayMessage("Error in recieving connection.");
			ioe.printStackTrace();
		}
	}

	private class ClientHandler implements Runnable {
		private Socket socket; // A connected socket
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			ObjectInputStream inputFromClient = null;
			ObjectOutputStream outputToClient = null;
			try {
				inputFromClient = new ObjectInputStream(socket.getInputStream());

				//outputToClient
				outputToClient = new ObjectOutputStream(socket.getOutputStream());

				// Read a trainTrackLine Update from client.
				Object object = inputFromClient.readObject();
				TrainTrackLine ttl = (TrainTrackLine)object;
				
				displayMessage("Client requesting " + ttl.getStopName() + ", "
						+ ttl.getLine() + " train, direction: " + ttl.getDirection());
				
				/*
				 * Try to pull it from the database first.
				 * If db is empty, go to the Gtfs query.
				 */
				LinkedList<NextTrainUpdate> ntu = getNextTrainTimes(ttl);

				//write a LinkedList<NextTrainUpdate> to client
				outputToClient.writeObject(ntu);
				outputToClient.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (inputFromClient != null) {
					try {
						inputFromClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputToClient != null) {
					try {
						outputToClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public LinkedList<NextTrainUpdate> getNextTrainTimes(TrainTrackLine trainTrack) {
		String line = trainTrack.getLine();
		String stopName = trainTrack.getStopName();
		String direction = trainTrack.getDirection();
		
		//first get update time for stopID. 
		//if update time is within allowable tolerance
			//pull from database.
		//otherwise query from GTFS server and insert into database.
		String stopID = DatabaseOperations.getStopID(line, stopName, direction);
		
		//If last update for a stopID is older than allowable age, delete from database and query from server.
		//Else use the database values.
		Long lastUpdated = DatabaseOperations.getLastUpdate(stopID);
		Long currentTime = ((new Date()).getTime());
		Long timeAllowance = currentTime - MAXIMUM_AGE_FROM_DATABASE;
		if (lastUpdated == null || Long.compare(lastUpdated, timeAllowance) < 0) {
			displayMessage("Accessing via API");
			return GtfsQuery.queryApiForUpdates(line, stopName, direction);
		} else {
			displayMessage("Accessing via Database");
			return DatabaseOperations.getNextTrainsAtStop(stopID);
		}
	}
	
	public static void main(String[] args) {
		Server mts = new Server();
	}
}
