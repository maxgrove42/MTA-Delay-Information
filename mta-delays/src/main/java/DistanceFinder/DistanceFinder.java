package DistanceFinder;

import java.io.*;
import java.net.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

public class DistanceFinder {
	
	private static final String API_URL = "https://api.openrouteservice.org/v2/directions/";

	
	private static final String WALKING_PROFILE = "foot-walking";
	
	
	private String apiKey;
	private String profile;
	
	private double startLatitude;
	private double startLongitude;
	private double endLatitude;
	private double endLongitude;
	
	
	public DistanceFinder(String apiKey, String profile) {
		this.apiKey = apiKey;
		this.profile = profile;
	}
	
	public double getDistance(double startLatitude, double startLongitude,
			double endLatitude, double endLongitude) {
		return getDistance(String.valueOf(startLatitude), String.valueOf(startLongitude),
				String.valueOf(endLatitude), String.valueOf(endLongitude));
	}
	
	public double getDistance(String startLatitude, String startLongitude,
			String endLatitude, String endLongitude) {
		String requestURL = createURL(startLatitude, startLongitude, endLatitude, endLongitude);
		Response response = getResponse(requestURL);
		
		
		return -1;
	}
	
	private String createURL(String startLatitude, String startLongitude,
			String endLatitude, String endLongitude) {
		return profile + "?" + API_URL + "api_key=" + apiKey +
				"&start=" + startLongitude + "," + startLatitude +
				"&end=" + endLongitude + "," + endLatitude;
	}
	
	private Response getResponse(String url) {
		Client client = ClientBuilder.newClient();
		Response response = client.target(url)
		  .request(MediaType.TEXT_PLAIN_TYPE)
		  .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
		  .get();
		
		return response;
	}


    public static void main(String[] args) {
    	DistanceFinder df = new DistanceFinder("5b3ce3597851110001cf62484704d78b310b4675ac0727cc3bf2dc34",
    			DistanceFinder.WALKING_PROFILE);
    	df.getDistance(8.687026,49.419593,8.687082,49.419598);
    }
}