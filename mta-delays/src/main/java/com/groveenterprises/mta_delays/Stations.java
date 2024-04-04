package com.groveenterprises.mta_delays;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Stations {

	private static final String STATIONS_FILE = "src/resources/stops.csv";
	private static ArrayList<Station> stations = new ArrayList<Station>();
	
	
	
	public Stations() throws FileNotFoundException, IOException {
		FileReader f;
		try {
			f = new FileReader(STATIONS_FILE);
			BufferedReader in = new BufferedReader(f);
			String ln = in.readLine();
			in.readLine(); // skip header row
			while (ln != null) {
				String[] stationParts = ln.split(",");
				stations.add(new Station(stationParts[0], stationParts[1].charAt(0), stationParts[2], stationParts[3].charAt(0), stationParts[4]));
				ln = in.readLine();
			} 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new FileNotFoundException("Unable to find " + STATIONS_FILE);
		} catch (IOException e) {
			throw new IOException("STATIONS_FILE found, but there was an error in reading it");
		}
	}
	
	public ArrayList<Station> getStations() {
		return stations;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Stations s = new Stations();
		ArrayList<Station> stations = s.getStations();
		for (Station st : stations) {
			System.out.println(st);
		}
	}

}
