package com.groveenterprises.mta_delays.ClientTools;

import java.awt.*;
import java.io.*;
import java.net.Socket;

import javax.swing.*;

import com.groveenterprises.mta_delays.ConfigurationTools.*;
import com.groveenterprises.mta_delays.DatabaseTools.*;
import com.groveenterprises.mta_delays.HelperClasses.*;

import java.util.*;

public class TrainTimeDisplay extends JFrame {

	//Version ID
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 700;
	private static final int FRAME_HEIGHT = 700;

	private JFrame frame = new JFrame();
	
	// Panel to store and display selection boxes
	JPanel selections = new JPanel();
	
	// Panel to store and display upcoming train times
	JPanel times = new JPanel();

	//List of all possible lines
	JList<String> linesList;
	
	//List of all possible stations in a line
	JList<String> stationsList;
	
	//List of all possible directions in a station-line
	JList<String> directionsList;
	
	//List of upcoming trains at the StopID
	JList<String> nextTrainsList;

	public TrainTimeDisplay() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setLayout(new BorderLayout()); 

		selections.setLayout(new GridLayout(1, 3));
		
		//display next trains coming to the stopID selected
		times.setLayout(new GridLayout(1,1));

		setUpLines();
		setUpStations();
		setUpDirections();
		setUpDisplayTimes();

		selections.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		times.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		
		frame.add(selections, BorderLayout.NORTH);
		frame.add(times, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}

	private void setUpLines() {
		//Query the database for all possible lines.
		linesList = new JList<>(DatabaseOperations.getLines());

		//Add action listener to display possible stations once a Line is selected
		linesList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				displayStations();
		});
		
		linesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(linesList);
		selections.add(scrollPane);
	}
	
	private void setUpStations() {
		stationsList = new JList<String>();
		stationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Add action listener to display possible directions once a line and station is selected
		stationsList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				displayDirections();
		});

		JScrollPane scrollPane = new JScrollPane(stationsList);
		selections.add(scrollPane);
	}
	
	private void setUpDirections() {
		directionsList = new JList<String>();
		directionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Add action listener to display upcoming trains once all selections are selected
		directionsList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) { 
				LinkedList<NextTrainUpdate> nextTrains = getNextTrains();
				displayTrainUpdates(nextTrains);
			}
		});

		JScrollPane scrollPane = new JScrollPane(directionsList);
		selections.add(scrollPane);
	}

	private void setUpDisplayTimes() {
		nextTrainsList = new JList<String>();
		nextTrainsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(nextTrainsList);
		times.add(scrollPane);  
	}

	private void displayStations() {
		String line = linesList.getSelectedValue();

		//If line isn't selected, clear out stations display
		if (line == null) {
			stationsList.setListData(new String[0]);
		} else {
			stationsList.setListData(DatabaseOperations.getStations(line));
		}
	}
	
	private void displayDirections() {
		String station = stationsList.getSelectedValue();
		String line = linesList.getSelectedValue();

		//if a station isn't selected, clear out directions displays
		if (station == null) {
			directionsList.setListData(new String[0]);
		} else {
			directionsList.setListData(DatabaseOperations.getDirections(line, station));
		}
	}

	/**
	 * Query the server for next trains at a TrainTrackLine
	 * @return LinkedList of NextTrainUpdates
	 */
	private LinkedList<NextTrainUpdate> getNextTrains() {
		if (isAnySelectionNull()) {
			//return an empty LinkedList
			return new LinkedList<NextTrainUpdate>();
		} else {
			String station = stationsList.getSelectedValue();
			String line = linesList.getSelectedValue();
			String direction = directionsList.getSelectedValue();
			TrainTrackLine ttl = new TrainTrackLine(line, station, direction);
			return getNextTrainTimesFromServer(ttl);
		}
	}
	
	private void displayTrainUpdates(LinkedList<NextTrainUpdate> nextTrains) {
		String[] nextTrainsArray;
		if (isAnySelectionNull()) {
			nextTrainsArray = new String[1];
			nextTrainsArray[0] = "Please select a line, station, and direction.";
		}
		else if (nextTrains.size() == 0) {
			nextTrainsArray = new String[1];
			nextTrainsArray[0] = "No upcoming trains";
		} else {
			int i = 0;
			nextTrainsArray = new String[nextTrains.size()];
			for (NextTrainUpdate ntu : nextTrains) {
				String s = ntu.getMinutesAway() + " min, " + ntu.getSecondsAway() + " sec (" +
						ntu.getLine() + " train)";
				nextTrainsArray[i++] = s;
			}
		}
		nextTrainsList.setListData(nextTrainsArray);
	}

	private LinkedList<NextTrainUpdate> getNextTrainTimesFromServer(TrainTrackLine ttl) {
		System.out.println("Sending client request.");
		try (Socket socket = new Socket(Configuration.getServerAddress(), Configuration.getServerSocketPort());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
			
			out.writeObject(ttl);
			out.flush();

			LinkedList<NextTrainUpdate> trainUpdates = (LinkedList<NextTrainUpdate>) in.readObject();
			return trainUpdates;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return new LinkedList<NextTrainUpdate>();
		}
	}
	
	private boolean isAnySelectionNull() {
		String station = stationsList.getSelectedValue();
		String line = linesList.getSelectedValue();
		String direction = directionsList.getSelectedValue();
		return (station == null || line == null || direction == null);
	}


	public static void main(String[] args) throws FileNotFoundException, IOException {
		TrainTimeDisplay ttd = new TrainTimeDisplay();
	}

}
