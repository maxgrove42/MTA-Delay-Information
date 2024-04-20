package com.groveenterprises.mta_delays.HelperClasses;

import java.io.Serializable;

public class NextTrainUpdate implements Serializable {

	private static final long serialVersionUID = 1L;

	//Stop ID (i.e. 501N)
	private String stopID;
	
	//Train line (i.e. "7" or "N")
	private String line;
	
	//arrival time of the train
	private Long arrivalTime;
	
	
	/**
	 * Takes a train line and arrivalTime (epoch seconds)
	 * @param stopID : String
	 * @param line : String
	 * @param arrivalTime : Long
	 */
	public NextTrainUpdate(String stopID, String line, Long arrivalTime) {
		this.stopID = stopID;
		this.line = line;
		this.arrivalTime = arrivalTime;
	}
	
	/**
	 * Returns the arrival time in milliseconds since epoch
	 * @return Long
	 */
	public Long getArrivalTime() {
		return arrivalTime;
	}
	
	public String getStopID() {
		return stopID;
	}

	public String getLine() {
		return line;
	}
	public Long getTimeUntilArrival() {
		Long timeUntilArrivalInMilli = arrivalTime * 1000 - System.currentTimeMillis();
		return timeUntilArrivalInMilli / 1000L;
	}
	public int getMinutesAway() {
		return (int)(getTimeUntilArrival() / 60);
	}

	public int getSecondsAway() {
		return (int)(((getTimeUntilArrival()) - getMinutesAway() * 60));
	}

}
