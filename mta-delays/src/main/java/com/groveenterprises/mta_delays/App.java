package com.groveenterprises.mta_delays;

import com.groveenterprises.mta_delays.ClientTools.*;
import com.groveenterprises.mta_delays.ServerTools.*;

public class App 
{
    public static void main( String[] args )
    {
    	new Server();
    	new TrainTimeDisplay();
    	new TrainTimeDisplay();
    }
}
