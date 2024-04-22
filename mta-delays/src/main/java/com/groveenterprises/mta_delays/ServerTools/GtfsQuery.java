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
	
	public static LinkedList<NextTrainUpdate> queryApiForUpdates(TrainTrackLine ttl) {
		return queryApiForUpdates(ttl.getLine(), ttl.getStopName(), ttl.getDirection());
	}
	
    public static LinkedList<NextTrainUpdate> queryApiForUpdates(String line, String station, String direction) {
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
		String line = "7";
		String station = "46 St-Bliss St";
		String direction = "Manhattan";
		
		System.out.println("Next trains to arrive at " + station + " towards " + direction + ":");
		for (NextTrainUpdate ntu : GtfsQuery.queryApiForUpdates(new TrainTrackLine(line, station, direction))) {
			System.out.print(ntu.getMinutesAway() + " mins, ");
			System.out.print(ntu.getSecondsAway() + " secs ");
			System.out.print("(" + ntu.getLine() + " train)\n");
		}
	}

}
