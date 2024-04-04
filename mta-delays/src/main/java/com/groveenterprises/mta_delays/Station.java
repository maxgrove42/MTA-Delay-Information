package com.groveenterprises.mta_delays;

public class Station {

	private String stopId;
	private char line;
	private String stopName;
	private char borough;
	private String direction;
	
	/**
	 * Construct a new Station
	 * @param stopId
	 * @param line
	 * @param stopName
	 * @param borough
	 * @param direction
	 */
	public Station(String stopId, char line, String stopName, char borough, String direction) {
		super();
		this.stopId = stopId;
		this.line = line;
		this.stopName = stopName;
		this.borough = borough;
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "Station [stopId=" + stopId + ", line=" + line + ", stopName=" + stopName + ", borough=" + borough
				+ ", direction=" + direction + "]";
	}

	public String getStopId() {
		return stopId;
	}

	public String getStopName() {
		return stopName;
	}

	public char getBorough() {
		return borough;
	}
	public String getDirection() {
		return direction;
	}
	public char getLine() {
		return line;
	}


}
