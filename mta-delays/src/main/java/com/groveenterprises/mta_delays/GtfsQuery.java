package com.groveenterprises.mta_delays;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.transit.realtime.GtfsRealtime.*;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.*;

public class GtfsQuery {
	
	
	public static String[] getNextTrainTimes(String line, String stopName, String direction) {
		URL url;
		String api = DatabaseOperations.getAPI(line);
		String stopID = DatabaseOperations.getStopID(line, stopName, direction);
		List<String> nextTrainTimes = new LinkedList<String>();
		try {
			url = new URL(api);
			FeedMessage feed = FeedMessage.parseFrom(url.openStream());
			for (FeedEntity entity : feed.getEntityList()) {
			      if (entity.hasTripUpdate()) {
			    	TripUpdate tripUpdate = entity.getTripUpdate();
			    	if (tripUpdate.getTrip().hasRouteId() &&
			    			tripUpdate.getTrip().getRouteId().equals(line)) {
			    		for (StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()) {
			    			if (stu.hasStopId() && stu.getStopId().equals(stopID) &&
			    					stu.hasArrival() && stu.getArrival().getTime() * 1000 > System.currentTimeMillis()) {
			    				Long timeUntilArrivalInMilli = stu.getArrival().getTime() * 1000 - System.currentTimeMillis();
			    				double timeUntilArrivalInMinutes = timeUntilArrivalInMilli / 1000.0 / 60.0;
			    				int minutes = (int)timeUntilArrivalInMinutes;
			    				int seconds = (int)((timeUntilArrivalInMinutes - minutes) * 60);
			    				
			    				if (minutes == 0) {
			    					nextTrainTimes.add(seconds + " sec");
			    				} else {
			    					nextTrainTimes.add(minutes + " min, " + seconds + " sec");
			    				}
			    			}
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
		

		//lookup the stopID and use the private helper function
		return nextTrainTimes.toArray(new String[0]);
	}
	

	public static void main(String[] args) {
		String line = "7";
		String station = "5 Av";
		String direction = "Queens";
		
		String[] output = GtfsQuery.getNextTrainTimes(line, station, direction);
		System.out.println("Next " + line + " trains to arrive at " + station + " towards " + direction + ":");
		for (String s : output) {
			System.out.println(s);
		}
	}

}
