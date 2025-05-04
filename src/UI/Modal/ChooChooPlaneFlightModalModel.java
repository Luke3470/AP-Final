package UI.Modal;

import java.sql.*;

public class ChooChooPlaneFlightModalModel {

    private final String dbURL;

    private static final String query = """
        SELECT
            Flight.flight_number,
            Flight.date,
            AirportOrigin.name AS origin_airport_name,
            AirportOrigin.iata_code AS origin_iata_code,
            Flight.scheduled_departure,
            Flight.actual_departure,
            AirportDestination.name AS destination_airport_name,
            AirportDestination.iata_code AS destination_iata_code,
            Flight.scheduled_arrival,
            Flight.actual_arrival,
            Airline.name AS airline_name,
            Flight.airline_code,
            Delay_Reason.reason,
            Delay_Reason.delay_length
        FROM Flight
        JOIN Airport AS AirportOrigin ON AirportOrigin.iata_code = Flight.flight_origin
        JOIN Airport AS AirportDestination ON AirportDestination.iata_code = Flight.flight_destination
        JOIN Airline ON Airline.iata_code = Flight.airline_code
        LEFT OUTER JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id
        WHERE date = ? AND flight_number = ?
        """;

    /**
     * Sets the Connection URL for the Database using same connection string as selected in flight view and set in Flight Modal
     * The db_url argument Must give a sqllite Database connection string
     *
     * @param dbURL Same Connection string as database selected in main view
     * @see ChooChooPlaneFlightModalModel
     * @see ChooChooPlaneFlightModalView
     */
    public ChooChooPlaneFlightModalModel(String dbURL) {
        this.dbURL = dbURL;
    }


    /**
     * Collects all data about specific flight in the database
     * Using JDBC to query and collect data about a specific record
     * @param date Date of the selected record used a composite key to find correct DB entry
     * @param flightNum flight_number of the selected record used a composite key to find correct DB entry
     * @return Returns String array in order to populate the modal created in the Modal View
     * @see ChooChooPlaneFlightModalView
     * @see org.sqlite.JDBC
     */
    public String[] getData(int date, int flightNum) {
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, date);
            pstmt.setInt(2, flightNum);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                            String.valueOf(rs.getInt("flight_number")),
                            String.valueOf(rs.getInt("date")),
                            rs.getString("origin_airport_name"),
                            rs.getString("origin_iata_code"),
                            rs.getString("scheduled_departure"),
                            rs.getString("actual_departure"),
                            rs.getString("destination_airport_name"),
                            rs.getString("destination_iata_code"),
                            rs.getString("scheduled_arrival"),
                            rs.getString("actual_arrival"),
                            rs.getString("airline_name"),
                            rs.getString("airline_code"),
                            rs.getString("reason"),
                            rs.getString("delay_length") != null ? rs.getString("delay_length") : "" // handle null delay
                    };
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching flight data", e);
        }

        // Return empty/default data if no result found
        return new String[14];
    }
}