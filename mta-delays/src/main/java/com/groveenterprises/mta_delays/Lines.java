package com.groveenterprises.mta_delays;

import java.util.*;

public class Lines {
	
	private static final String ACE_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace";
	private static final String G_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-g";
	private static final String NQRW_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-nqrw";
	private static final String NUMBERED_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs";
	private static final String BDFM_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-bdfm";
	private static final String JZ_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-jz";
	private static final String L_API = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-l";
	
	private static final Map<Character, String> lines = new HashMap<Character, String>() {{
	    put('1', NUMBERED_API);
	    put('2', NUMBERED_API);
	    put('3', NUMBERED_API);
	    put('4', NUMBERED_API);
	    put('5', NUMBERED_API);
	    put('6', NUMBERED_API);
	    put('7', NUMBERED_API);
	    put('A', ACE_API);
	    put('C', ACE_API);
	    put('E', ACE_API);
	    put('B', BDFM_API);
	    put('D', BDFM_API);
	    put('F', BDFM_API);
	    put('M', BDFM_API);
	    put('G', G_API);
	    put('L', L_API);
	    put('J', JZ_API);
	    put('Z', JZ_API);
	    put('N', NQRW_API);
	    put('Q', NQRW_API);
	    put('R', NQRW_API);
	    put('W', NQRW_API);
	}};
	

	public static Map<Character, String> getLines() {
		return lines;
	}
	public static void main(String[] args) {
		
	}

}
