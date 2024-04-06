package com.groveenterprises.mta_delays;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;
import java.util.*;

public class TrainTimeDisplay extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final int FRAME_WIDTH = 700;
	private static final int FRAME_HEIGHT = 700;
	
	private JFrame frame = new JFrame();
    JPanel selections = new JPanel();
    JPanel times = new JPanel();
    
    JList<String> linesList;
    JList<String> stationsList;
    JList<String> directionsList;
    JList<String> nextTrainsList;
	
	public TrainTimeDisplay() throws FileNotFoundException, IOException {
		
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLayout(new BorderLayout()); 
		
        selections.setLayout(new GridLayout(1, 3));
        times.setLayout(new GridLayout(1,2)); //display next trains selected and all next trains.
        
		//display the lines selection
		setUpLines();
		setUpStations();
		setUpDirections();
		setUpDisplayTimes();
		
		selections.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(selections, BorderLayout.NORTH);
        frame.add(times, BorderLayout.CENTER);
        frame.setVisible(true);
	}
	
	private void setUpLines() {
		
		linesList = new JList<>(DatabaseOperations.getLines());
		
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
		
		if (line == null) {
			stationsList.setListData(new String[0]); //set list to empty array
		} else {
			stationsList.setListData(DatabaseOperations.getStations(line));
		}
	}
	private void displayDirections() {
		String station = stationsList.getSelectedValue();
		String line = linesList.getSelectedValue();

		if (station == null) {
			directionsList.setListData(new String[0]); //set list to empty array
		} else {
			directionsList.setListData(DatabaseOperations.getDirections(line, station));
		}
	}
	
	private LinkedList<NextTrainUpdate> getNextTrains() {
		String station = stationsList.getSelectedValue();
		String line = linesList.getSelectedValue();
		String direction = directionsList.getSelectedValue();
		if (station == null || line == null || direction == null) {
			//return an empty LinkedList
			return new LinkedList<NextTrainUpdate>();
		} else {
			return GtfsQuery.getNextTrainTimes(line, station, direction);
		}
	}
	private void displayTrainUpdates(LinkedList<NextTrainUpdate> nextTrains) {
		String[] nextTrainsArray = new String[nextTrains.size()];
		int i = 0;
		for (NextTrainUpdate ntu : nextTrains) {
			String s = ntu.getMinutesAway() + " min, " + ntu.getSecondsAway() + " sec (" +
		
					ntu.getLine() + " train)";
			nextTrainsArray[i++] = s;
		}
		nextTrainsList.setListData(nextTrainsArray);
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		TrainTimeDisplay ttd = new TrainTimeDisplay();
	}

}
