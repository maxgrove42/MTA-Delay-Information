package com.groveenterprises.mta_delays;
import java.io.*;
import java.util.Properties;

public class Configuration {

	private static String apiKey;
    private static String databaseUrl;
    private static String databaseUser;
	private static String databasePassword;
	private static String databaseName;
	
    static {
    	//easy loading of a config.properties file.
    	Properties prop = new Properties();
    	try (FileInputStream input = new FileInputStream("src/resources/config.properties")) {
            // Load the properties file
            prop.load(input);
            
            // Get the properties values
            apiKey = prop.getProperty("gtfs.api.key");
            databaseUrl = prop.getProperty("database.url");
            databaseUser = prop.getProperty("database.user");
            databasePassword = prop.getProperty("database.password");
            databaseName = prop.getProperty("database.name");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getApiKey() {
		return apiKey;
	}

	public static String getDatabaseUrl() {
		return databaseUrl;
	}

	public static String getDatabaseUser() {
		return databaseUser;
	}

	public static String getDatabasePassword() {
		return databasePassword;
	}
	
	public static String getDatabaseName() {
		return databaseName;
	}
	
	public static void main(String[] args) {
		System.out.println("api: " + Configuration.getApiKey());
		System.out.println("url: " + Configuration.getDatabaseUrl());
		System.out.println("User: " + Configuration.getDatabaseUser());
		System.out.println("Password: " + Configuration.getDatabasePassword());
		System.out.println("DB Name: " + Configuration.getDatabaseName());
	}

}
