package com.groveenterprises.mta_delays.HelperClasses;

import java.io.Serializable;

import com.groveenterprises.mta_delays.DatabaseTools.DatabaseOperations;

/**
 * Represents a line, stopName, and direction. I.e. a train line that passes through a <br>
 * given station in a given direction 
 */
public class TrainTrackLine implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String line;
	private String stopName;
	private String direction;
	private String stopID;
	
	public TrainTrackLine(String line, String stopName, String direction) {
		this.line = line;
		this.stopName = stopName;
		this.direction = direction;
		this.stopID = DatabaseOperations.getStopID(this);
	}

	public String getLine() {
		return line;
	}

	public String getStopName() {
		return stopName;
	}

	public String getDirection() {
		return direction;
	}

	public String getStopID() {
		return stopID;
	}
	
}
