package com.groveenterprises.mta_delays;
import java.sql.*;
import java.util.*;

public class DatabaseOperations {

	/**
	 * Takes a query and variable amount of parameters. Executes query using the params.<br>
	 * Parameter placeholders in query must match count of params.
	 * @param query : String
	 * @param params : String
	 * @return String[]
	 */
	private static String[] executeQueryWithSingleResultColumn(String query, String... params) {
		List<String> results = new LinkedList<>();
		//auto try resources to ensure closure.
		try (Connection conn = DatabaseConnector.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			for (int i = 0; i < params.length; i++) {
				stmt.setString(i + 1, params[i]);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					results.add(rs.getString(1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database operation failed", e);
		}
		return results.toArray(new String[0]);
	}

	/**
	 * Gets all subway lines in the system. Lines ordered alphanumerically
	 * @return String[]
	 */
	public static String[] getLines() {
		return executeQueryWithSingleResultColumn("SELECT line FROM apis ORDER BY line");
	}

	/**
	 * Returns all stations on a given line. Stations ordered alphanumerically
	 * @param line : String
	 * @return String[]
	 */
	public static String[] getStations(String line) {
		if (line == null || line.isEmpty())
			throw new IllegalArgumentException("Line cannot be null or empty");
		return executeQueryWithSingleResultColumn("SELECT DISTINCT stopName FROM stations WHERE line = ? ORDER BY stopName", line);
	}

	/**
	 * Returns all directions on a given station on a line (i.e. "7" line, "5 Av" station)<br>
	 * Directions ordered alphanumerically.
	 * @param line : String
	 * @param stopName : String
	 * @return String[]
	 */
	public static String[] getDirections(String line, String stopName) {
		if (line == null || line.isEmpty() || stopName == null || stopName.isEmpty())
			throw new IllegalArgumentException("Line and stopName cannot be null or empty");
		return executeQueryWithSingleResultColumn("SELECT direction FROM stations WHERE line = ? AND stopName = ? ORDER BY direction", line, stopName);
	}

	/**
	 * Returns the API URL for a given subway line. Returns null if line is not in system.
	 * @param line
	 * @return api : String
	 */
	public static String getAPI(String line) {
		if (line == null || line.isEmpty()) throw new IllegalArgumentException("Line cannot be null or empty");
		String[] apis = executeQueryWithSingleResultColumn("SELECT api FROM apis WHERE line = ?", line);
		return (apis.length == 0) ? null : apis[0];
	}
	
	public static void main(String[] args) {
		
	}
}
