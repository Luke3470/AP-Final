package UI;

import java.sql.*;

public class ChooChooPlaneFlightModalModel {

    private final String dbUrl;

    private static final String QUERY = """
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

    public ChooChooPlaneFlightModalModel(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String[] getData(int date, int flightNum) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(QUERY)) {

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
                            rs.getString("reason"), // can be null
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