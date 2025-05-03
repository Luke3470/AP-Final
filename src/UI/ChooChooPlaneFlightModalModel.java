package UI;

import java.sql.*;


public class ChooChooPlaneFlightModalModel {

    private String dbUrl;

    ChooChooPlaneFlightModalModel(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String[] getData(int date, int flightNum) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT\n" +
                    "    Flight.flight_number,\n" +
                    "    Flight.date,\n" +
                    "    AirportOrigin.name AS origin_airport_name,\n" +
                    "    AirportOrigin.iata_code as origin_iata_code,\n" +
                    "    Flight.scheduled_departure,\n" +
                    "    Flight.actual_departure,\n" +
                    "    AirportDestination.name AS destination_airport_name,\n" +
                    "    AirportDestination.iata_code AS destination_iata_code,\n" +
                    "    Flight.scheduled_arrival,\n" +
                    "    Flight.actual_arrival,\n" +
                    "    Airline.name AS airline_name,\n" +
                    "    Flight.airline_code,\n" +
                    "    Delay_Reason.reason,\n" +
                    "    Delay_Reason.delay_length\n" +
                    "FROM Flight\n" +
                    "         JOIN Airport AS AirportOrigin ON AirportOrigin.iata_code = Flight.flight_origin\n" +
                    "         JOIN Airport AS AirportDestination ON AirportDestination.iata_code = Flight.flight_destination\n" +
                    "         JOIN Airline ON Airline.iata_code = Flight.airline_code\n" +
                    "         LEFT OUTER JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id\n" +
                    "WHERE date = ? and flight_number = ?;");

            pstmt.setInt(1, date);
            pstmt.setInt(2, flightNum);
            ResultSet rs = pstmt.executeQuery();
            String[] data = new String[15];
            while (rs.next()) {
                data[0] = String.valueOf(rs.getInt("flight_number"));
                data[1] = String.valueOf(rs.getInt("date"));
                data[2] = rs.getString("origin_airport_name");
                data[3] = rs.getString("origin_iata_code");
                data[4] = rs.getString("scheduled_departure");
                data[5] = rs.getString("actual_departure");
                data[6] = rs.getString("destination_airport_name");
                data[7] = rs.getString("destination_iata_code");
                data[8] = rs.getString("scheduled_arrival");
                data[9] = rs.getString("actual_arrival");
                data[10] = rs.getString("airline_name");
                data[11] = rs.getString("airline_code");
                data[12] = rs.getString("reason");
                data[13] = String.valueOf(rs.getInt("delay_length"));
            }
            return data;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}