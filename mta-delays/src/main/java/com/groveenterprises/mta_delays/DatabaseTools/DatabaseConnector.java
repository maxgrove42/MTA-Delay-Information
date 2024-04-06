package com.groveenterprises.mta_delays.DatabaseTools;
import java.sql.*;

import com.groveenterprises.mta_delays.ConfigurationTools.*;

public class DatabaseConnector {

	private static final String DATABASE_NAME = Configuration.getDatabaseName();
	private static final String URL = Configuration.getDatabaseUrl() + DATABASE_NAME;
    private static final String USER = Configuration.getDatabaseUser();
    private static final String PASSWORD = Configuration.getDatabasePassword();
	
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
    	Connection c = DatabaseConnector.getConnection();
    	Statement stmt;
		try {
			stmt = c.createStatement();
	    	ResultSet r = stmt.executeQuery("SELECT * from boroughs");
	    	while (r.next()) {
	    		System.out.println(r.getString(1) + ": " + r.getString(2));
	    	}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("SQL error");
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    	System.out.println("done");
    }

}
