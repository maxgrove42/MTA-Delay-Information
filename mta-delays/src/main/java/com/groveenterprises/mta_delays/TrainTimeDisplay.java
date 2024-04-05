package com.groveenterprises.mta_delays;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;

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
			if (!e.getValueIsAdjusting())
				displayNextTrains();
		});
		
		JScrollPane scrollPane = new JScrollPane(directionsList);
		selections.add(scrollPane);
	}
	
	private void setUpDisplayTimes() {
		JPanel selectedTrainTimes = new JPanel();
		
		final JTextArea textArea = new JTextArea(1, 1);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		selectedTrainTimes.add(scrollPane);
		selectedTrainTimes.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	    times.add(scrollPane);  
	}
	
	private void displayStations() {

		String line = linesList.getSelectedValue();
		
		if (line == null) {
			stationsList.setListData(new String[0]); //set list to empty array
		} else {
			stationsList.setListData(DatabaseOperations.getStations(line));
		}
		
		//selections.repaint();
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
	private void displayNextTrains() {
		String station = stationsList.getSelectedValue();
		String line = linesList.getSelectedValue();
		String direction = directionsList.getSelectedValue();
		if (station == null || line == null || direction == null) {
			//display nothing.
		} else {
			//TODO
			String[] output = GtfsQuery.getNextTrainTimes(line, station, direction);
			System.out.println("Next " + line + " trains to arrive at " + station + " towards " + direction + ":");
			for (String s : output) {
				System.out.println(s);
			}
		}
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		TrainTimeDisplay ttd = new TrainTimeDisplay();
	}

}
