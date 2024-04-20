package com.groveenterprises.mta_delays.DatabaseTools;
import java.sql.*;
import java.util.*;

import com.groveenterprises.mta_delays.HelperClasses.NextTrainUpdate;
import com.groveenterprises.mta_delays.HelperClasses.TrainTrackLine;

public class DatabaseOperations {

	/**
	 * Takes a query and variable amount of parameters. Executes query using the params.
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
	 * Query the SQL database for the NextTrainUpdates on a line, station, direction
	 * @param line
	 * @param station
	 * @param direction
	 * @param allowableTimeToLive : allowable age of cache in milliseconds 
	 * @param maximumResults : maximum results to get from the SQL database
	 * @return LinkedList of NextTrainUpdate
	 */
	public static LinkedList<NextTrainUpdate> getTrainUpdatesFromDB(String line, String station, String direction, long allowableTimeToLive) {

		LinkedList<NextTrainUpdate> updates = new LinkedList<>();
		String stopID = getStopID(line, station, direction);
		//select arrivals that have not come yet and are younger than the allowable interval
		String sql = "SELECT * FROM arrivalTimeCache "
				+ "WHERE stopID = ? AND arrivalTime > NOW() AND insertionTime > DATE_ADD(NOW(3), INTERVAL ? MICROSECOND) ";



		try (Connection conn = DatabaseConnector.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, stopID);
			pstmt.setLong(2, -1*1000*allowableTimeToLive);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				// Assuming you have a constructor like this
				updates.add(new NextTrainUpdate(rs.getString("stopID"), rs.getString("line"), rs.getLong("arrivalTime")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updates;
	}

	/**
	 * Gets all subway lines in the system. Lines ordered alphanumerically
	 * @return String[]
	 */
	public static String[] getLines() {
		return executeQueryWithSingleResultColumn("SELECT line FROM apis ORDER BY line");
	}

	public static String getStopID(String line, String stopName, String direction) {
		if (line == null || line.isEmpty() || stopName == null || stopName.isEmpty() ||
				direction == null || direction.isEmpty())
			return null;
		String[] stopIDs = executeQueryWithSingleResultColumn("SELECT distinct stopID FROM stations " +
				"WHERE line = ? AND stopName = ? AND direction = ?", line, stopName, direction);
		return (stopIDs.length == 0) ? null : stopIDs[0];

	}
	
	public static String getStopID(TrainTrackLine ttl) {
		return getStopID(ttl.getLine(), ttl.getStopName(), ttl.getDirection());
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
		if (line == null || line.isEmpty())
			throw new IllegalArgumentException("Line cannot be null or empty");
		String[] apis = executeQueryWithSingleResultColumn("SELECT api FROM apis WHERE line = ?", line);
		return (apis.length == 0) ? null : apis[0];
	}

	/**
	 * Gets the last update to the stopID as milliseconds since Epoch.
	 * @param stopID
	 * @return
	 */
	public static Long getLastUpdate(String stopID) {
		Long lastUpdate = null;
		String query = "SELECT lastUpdate FROM lastUpdate WHERE stopID = ?";
		//auto try resources to ensure closure.
		try (Connection conn = DatabaseConnector.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, stopID);
			try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) { // Check if the result set is not empty
	                java.util.Date lastUpdateDate = rs.getTimestamp("lastUpdate");
	                lastUpdate = lastUpdateDate.getTime();
	            }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lastUpdate;
	}

	public static LinkedList<NextTrainUpdate> getNextTrainsAtStop(String stopID) {
		LinkedList<NextTrainUpdate> nextTrains = new LinkedList<>();
		String query = "SELECT stopID, line, arrivalTime FROM arrivalTimeCache WHERE stopID = ? AND arrivalTime > UNIX_TIMESTAMP() ORDER BY arrivalTime";
		try (Connection conn = DatabaseConnector.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, stopID);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String line = rs.getString(2);
					Long arrivalTime = rs.getLong(3);
					nextTrains.add(new NextTrainUpdate(stopID, line, arrivalTime));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return nextTrains;
	}

	public static void insertNextTrainUpdates(LinkedList<NextTrainUpdate> nextTrains) {
		if (nextTrains.isEmpty()) {
			return; // Return early if there's nothing to insert
		} 
		String query = "INSERT INTO arrivalTimeCache (stopID, line, arrivalTime) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE arrivalTime = VALUES(arrivalTime);";
		String updateQuery = "INSERT INTO lastUpdate (stopID, lastUpdate) VALUES (?, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE lastUpdate = CURRENT_TIMESTAMP;";
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement updateStmt = null;
		try {
			conn = DatabaseConnector.getConnection();
			conn.setAutoCommit(false); // Start transaction
			stmt = conn.prepareStatement(query);
			updateStmt = conn.prepareStatement(updateQuery);

			for (NextTrainUpdate nextTrain : nextTrains) {
				stmt.setString(1, nextTrain.getStopID());
				stmt.setString(2, nextTrain.getLine());
				stmt.setLong(3, nextTrain.getArrivalTime());
				stmt.executeUpdate(); // Execute each insert individually
			}

			//now also update the last updated table
			String stopID = nextTrains.getFirst().getStopID();
			// Update lastUpdate table only once
			updateStmt.setString(1, stopID);
			updateStmt.executeUpdate();

			conn.commit(); // Commit transaction only if all inserts are successful

			//now modify the insertion time.
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback(); // Rollback transaction in case of failure
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close(); // Close PreparedStatement
				}
				if (conn != null) {
					conn.setAutoCommit(true); // Restore auto-commit
					conn.close(); // Close Connection
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

	}
}
