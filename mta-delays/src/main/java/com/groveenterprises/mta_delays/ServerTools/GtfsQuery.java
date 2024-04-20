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
	
	private static long MAXIMUM_AGE_FROM_DATABASE = 300_000; //in milliseconds
	
	public static LinkedList<NextTrainUpdate> getNextTrainTimes(TrainTrackLine trainTrack) {
		String line = trainTrack.getLine();
		String stopName = trainTrack.getStopName();
		String direction = trainTrack.getDirection();
		
		//first get update time for stopID. 
		//if update time is within allowable tolerance
			//pull from database.
		//otherwise query from GTFS server and insert into database.
		String stopID = DatabaseOperations.getStopID(line, stopName, direction);
		
		//If last update for a stopID is older than allowable age, delete from database and query from server.
		//Else use the database values.
		Long lastUpdated = DatabaseOperations.getLastUpdate(stopID);
		Long timeAllowance = MAXIMUM_AGE_FROM_DATABASE + (new Date()).getTime();
		if (lastUpdated == null || Long.compare(lastUpdated, timeAllowance) > 0) {
			System.out.println("Accessing via API");
			return queryApiForUpdates(line, stopName, direction);
		} else {
			System.out.println("Accessing via Database");
			return DatabaseOperations.getNextTrainsAtStop(stopID);
		}


        // Return combined or database-only train times
        
	}
	
    private static LinkedList<NextTrainUpdate> queryApiForUpdates(String line, String station, String direction) {
        LinkedList<NextTrainUpdate> nextTrainTimes = new LinkedList<>();
        String api = DatabaseOperations.getAPI(line);
        String stopID = DatabaseOperations.getStopID(line, station, direction);
        URL url;

        try {
            url = new URL(api);
            FeedMessage feed = FeedMessage.parseFrom(url.openStream());
            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    TripUpdate tripUpdate = entity.getTripUpdate();
                    String tripID = tripUpdate.getTrip().getTripId();
                    String tripUpdateLine = tripUpdate.getTrip().getRouteId();
                    for (StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()) {
                        if (stu.hasStopId() && stu.getStopId().equals(stopID) &&
                                stu.hasArrival() && stu.getArrival().getTime() * 1000 > System.currentTimeMillis()) {
                        	// first insert into database
                            nextTrainTimes.add(new NextTrainUpdate(stopID, tripUpdateLine, stu.getArrival().getTime()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error accessing API or parsing data: " + e.getMessage());
        }

        // Sort updates by time until arrival
        nextTrainTimes.sort((e1, e2) -> Long.compare(e1.getTimeUntilArrival(), e2.getTimeUntilArrival()));

        //Database insertion here.
        DatabaseOperations.insertNextTrainUpdates(nextTrainTimes);
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
