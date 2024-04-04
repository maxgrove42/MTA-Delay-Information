package com.groveenterprises.mta_delays;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class Gui extends JFrame {

	private static final int FRAME_WIDTH = 700;
	private static final int FRAME_HEIGHT = 700;
	
	private static Map<Character, String> LINES;
	private static ArrayList<Station> STATIONS;
	
	private JFrame frame = new JFrame();
    JPanel selections = new JPanel();
    JPanel times = new JPanel();
    JList<Character> linesList;
    JList<String> stationsList;
    JList<String> directionsList;
	
	public Gui() throws FileNotFoundException, IOException {
		
		LINES = Lines.getLines();
		STATIONS = (new Stations()).getStations();
		
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
		Character[] charLines = new Character[LINES.keySet().size()];
		LINES.keySet().toArray(charLines);
		Arrays.sort(charLines);
		
		linesList = new JList<>(charLines);
		
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
		Set<String> filteredStationNames = new HashSet<String>();

		Character line = linesList.getSelectedValue();
		
		if (line == null) {
			stationsList.setListData(new String[0]); //set list to empty array
		} else {
			
			for (Station s : STATIONS) {
				if (s.getLine() == line)
					filteredStationNames.add(s.getStopName());
			}
			
			String[] stringStations = new String[filteredStationNames.size()];
			filteredStationNames.toArray(stringStations);
			Arrays.sort(stringStations);
			stationsList.setListData(stringStations);
		}
		
		//selections.repaint();
	}
	private void displayDirections() {
		Set<String> directions = new HashSet<String>();
		String station = stationsList.getSelectedValue();
		Character line = linesList.getSelectedValue();

		if (station == null) {
			directionsList.setListData(new String[0]); //set list to empty array
		} else {
		
			for (Station s : STATIONS) {
				if (s.getStopName().equals(station) && s.getLine() == line && !s.getDirection().equals("Last Stop"))
					directions.add(s.getDirection());
			}
			String[] stringDirections = new String[directions.size()];
			directions.toArray(stringDirections);
			Arrays.sort(stringDirections);
			directionsList.setListData(stringDirections);
		}
	}
	private void displayNextTrains() {
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Gui g = new Gui();
	}

}
