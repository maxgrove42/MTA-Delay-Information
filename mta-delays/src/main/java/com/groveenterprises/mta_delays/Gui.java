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
    JList<Character> linesList;
    JList<String> stationsList;
	
	public Gui() throws FileNotFoundException, IOException {
		
		LINES = Lines.getLines();
		STATIONS = (new Stations()).getStations();
		
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLayout(new BorderLayout()); 
		
        selections.setLayout(new GridLayout(1, 3));
        
		//display the lines selection
		setUpLines();
		setUpStations();
		
		selections.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(selections, BorderLayout.NORTH);
        frame.setVisible(true);
	}
	
	private void setUpLines() {
		Character[] charLines = new Character[LINES.keySet().size()];
		LINES.keySet().toArray(charLines);
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
		JScrollPane scrollPane = new JScrollPane(stationsList);
		
		selections.add(scrollPane);

	}
	private void displayStations() {
		Set<String> filteredStationNames = new HashSet<String>();

		Character line = linesList.getSelectedValue();
		for (Station s : STATIONS) {
			if (s.getLine() == line)
				filteredStationNames.add(s.getStopName());
		}

		
		
		String[] stringStations = new String[filteredStationNames.size()];
		filteredStationNames.toArray(stringStations);
		Arrays.sort(stringStations);
		stationsList.setListData(stringStations);
		
		//selections.repaint();
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Gui g = new Gui();
	}

}
