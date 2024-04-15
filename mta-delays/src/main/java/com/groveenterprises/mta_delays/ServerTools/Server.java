package com.groveenterprises.mta_delays.ServerTools;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import com.groveenterprises.mta_delays.ConfigurationTools.*;
import com.groveenterprises.mta_delays.HelperClasses.*;

//should add a server window to stop / start server.
public class Server implements Runnable {
	
	public Server() {
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			// Create a server socket
			ServerSocket serverSocket = new ServerSocket(Configuration.getServerSocketPort());

			while (true) {
				// Listen for a connection request
				Socket socket = serverSocket.accept();
				// Create and start a new thread for the connection
				new Thread(new ClientHandler(socket)).start();
			}
		} catch (IOException ioe) {
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

				/*
				* Try to pull it from the database first.
    				* If db is empty, go to the Gtfs query.
				*/
				LinkedList<NextTrainUpdate> ntu = GtfsQuery.getNextTrainTimes(ttl);

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
	
	public static void main(String[] args) {
	    Server mts = new Server();
	}
}
