package com.groveenterprises.mta_delays.ServerTools;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import com.google.transit.realtime.GtfsRealtime.*;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.*;
import com.groveenterprises.mta_delays.DatabaseTools.*;
import com.groveenterprises.mta_delays.HelperClasses.NextTrainUpdate;
import com.groveenterprises.mta_delays.HelperClasses.TrainTrackLine;

public class GtfsQuery {
	
	
	public static LinkedList<NextTrainUpdate> getNextTrainTimes(TrainTrackLine trainTrack) {
		String line = trainTrack.getLine();
		String stopName = trainTrack.getStopName();
		String direction = trainTrack.getDirection();
		
		URL url;
		String api = DatabaseOperations.getAPI(line);
		String stopID = DatabaseOperations.getStopID(line, stopName, direction);
		LinkedList<NextTrainUpdate> nextTrainTimes = new LinkedList<NextTrainUpdate>();
		try {
			url = new URL(api);
			FeedMessage feed = FeedMessage.parseFrom(url.openStream());
			for (FeedEntity entity : feed.getEntityList()) {
			      if (entity.hasTripUpdate()) {
			    	TripUpdate tripUpdate = entity.getTripUpdate();
			    	String tripUpdateLine = tripUpdate.getTrip().getRouteId();
			    	for (StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()) {
			    		if (stu.hasStopId() && stu.getStopId().equals(stopID) &&
		    					stu.hasArrival() && stu.getArrival().getTime() * 1000 > System.currentTimeMillis()) {
			    			nextTrainTimes.add(new NextTrainUpdate(stopID, tripUpdateLine, stu.getArrival().getTime()));
			    		}
			    	}
			      }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException("URL is not valid.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error when accessing the URL API.");
		}
		//return the trains in sorted order since GTFS will default to train order
		//  and some tracks might share trains
		nextTrainTimes.sort((e1, e2) -> Long.compare(e1.getTimeUntilArrival(), e2.getTimeUntilArrival()));
		return nextTrainTimes;
	}
	

	public static void main(String[] args) {
		String line = "N";
		String station = "Times Sq-42 St";
		String direction = "Uptown";
		
		System.out.println("Next trains to arrive at " + station + " towards " + direction + ":");
		for (NextTrainUpdate ntu : GtfsQuery.getNextTrainTimes(new TrainTrackLine(line, station, direction))) {
			System.out.print(ntu.getMinutesAway() + " mins, ");
			System.out.print(ntu.getSecondsAway() + " secs ");
			System.out.print("(" + ntu.getLine() + " train)\n");
		}
	}

}
