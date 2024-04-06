package com.groveenterprises.mta_delays;

public class NextTrainUpdate {

	//Stop ID (i.e. 501N)
	private String stopID;
	
	//Train line (i.e. "7" or "N")
	private String line;
	
	//Seconds away (Double since Epoch)
	private Long timeUntilArrival;
	
	
	/**
	 * Takes a train line and arrivalTime (epoch seconds)
	 * @param stopID : String
	 * @param line : String
	 * @param arrivalTime : Long
	 */
	public NextTrainUpdate(String stopID, String line, Long arrivalTime) {
		this.stopID = stopID;
		this.line = line;
		
		Long timeUntilArrivalInMilli = arrivalTime * 1000 - System.currentTimeMillis();
		this.timeUntilArrival = timeUntilArrivalInMilli / 1000L;
	}
	
	public String getStopID() {
		return stopID;
	}

	public String getLine() {
		return line;
	}
	public Long getTimeUntilArrival() {
		return timeUntilArrival;
	}
	public int getMinutesAway() {
		return (int)(timeUntilArrival / 60);
	}

	public int getSecondsAway() {
		return (int)(((timeUntilArrival) - getMinutesAway() * 60));
	}

}
