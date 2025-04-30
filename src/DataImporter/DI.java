package DataImporter;

import exceptions.IncorrectFilePathException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Objects;


public class DI {

    private Path csvpath;

    private final String[] DELAY_REASONS = {
            "Carrier", "Weather", "NAS", "Security", "Aircraft"
    };


    DI() {
        setCsvPath(Path.of(System.getProperty("user.dir") + "\\lib\\Data\\flights_sample_3m.csv"));
    }

    public void startImport(Connection connection) throws IncorrectFilePathException {

        //Creates Connection to file
        try (BufferedReader br = Files.newBufferedReader(getCsvPath())) {
            String line;
            int total = 0;
            int successful = 0;
            int failed = 0;

            //Prepared Statement for all
            PreparedStatement pstmtAirline = connection.prepareStatement("INSERT OR IGNORE INTO Airline (name,iata_code) VALUES (?,?)");
            PreparedStatement pstmtAirport = connection.prepareStatement("INSERT OR IGNORE INTO Airport (name,iata_code) VALUES (?,?)");
            PreparedStatement pstmtFlight = connection.prepareStatement("INSERT OR IGNORE INTO Flight (date,flight_number,scheduled_departure,actual_departure,scheduled_arrival,actual_arrival,flight_id,airline_code,flight_origin,flight_destination) VALUES (?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement pstmtDelayReason = connection.prepareStatement("INSERT OR IGNORE INTO Delay_Reason (reason, delay_length,flight_id) VALUES (?,?,?)");


            //reads file
            while ((line = br.readLine()) != null) {
                Combo combo = isValid(line, total);


                //All data is Marked further down the only new key is using total as the PK for Flights to make joining
                //Tables simpler than using auto Increment
                if (combo.valid) {
                    successful += 1;
                    connection.setAutoCommit(false);

                    //Setting Airline Values
                    pstmtAirline.setString(1, combo.data[1]);
                    pstmtAirline.setString(2, combo.data[2]);
                    //Setting Airport Values
                    setAirportBatch(pstmtAirport, combo.data[5], combo.data[4]);
                    setAirportBatch(pstmtAirport, combo.data[7], combo.data[6]);
                    //Setting Flight Values
                    pstmtFlight.setString(1, combo.data[0]);
                    pstmtFlight.setInt(2, Integer.parseInt(combo.data[3]));
                    pstmtFlight.setInt(3, Integer.parseInt(combo.data[8]));
                    pstmtFlight.setInt(4, Integer.parseInt(combo.data[9]));
                    pstmtFlight.setInt(5, Integer.parseInt(combo.data[10]));
                    pstmtFlight.setInt(6, Integer.parseInt(combo.data[11]));
                    pstmtFlight.setInt(7, total);
                    pstmtFlight.setString(8, combo.data[2]);
                    pstmtFlight.setString(9, combo.data[4]);
                    pstmtFlight.setString(10, combo.data[6]);
                    //Adding to batch
                    pstmtAirline.addBatch();
                    pstmtAirport.addBatch();
                    pstmtFlight.addBatch();

                    if (combo.data[12] != null) {
                        pstmtDelayReason.setString(1, combo.data[12]);
                        pstmtDelayReason.setInt(2, Integer.parseInt(combo.data[13]));
                        pstmtDelayReason.setInt(3, total);

                        pstmtDelayReason.addBatch();
                    }

                    if ((total % 25000) == 0) {
                        executeAllBatches(pstmtAirline, pstmtAirport, pstmtFlight, pstmtDelayReason);
                    }


                } else {
                    failed += 1;
                }
                if ((total % 10000) == 0) {

                    System.out.println(total + " Lines have been parsed");
                    System.out.println("With " + successful + " Being Successful");
                    System.out.println("And " + failed + " Being Unsuccessful");
                    successful = 0;
                    failed = 0;
                }
                total += 1;
            }

            executeAllBatches(pstmtAirline, pstmtAirport, pstmtFlight, pstmtDelayReason);

            connection.commit();
            connection.close();

        } catch (IOException e) {
            throw new IncorrectFilePathException((getCsvPath()).toString(), e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //Combo class used to make data returns easier
    public static class Combo {
        Boolean valid;
        String[] data;

    }


    public Combo isValid(String Line, Integer count) {

        //Used to Parse CSV Which contains ,'s
        String[] data = Line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        //Remove all "'s from the list
        int len = data.length;
        data = Arrays.stream(data).map(s -> s.replace("\"", "")).toList().toArray(new String[len]);

        //Combo as tuple so validation and correction happen together (Python would have been useful)
        Combo data_cleaned = new Combo();
        data_cleaned.data = new String[14];
        data_cleaned.valid = false;

        //Check date time is between 2019-2023 and valid Whist Removing - to be in specified Format YYYYMMDD
        if (!isValidDate(data[0])) {
            System.out.println("Line: " + count + " Error in Flight Date");
            return data_cleaned;
        }
        data[0] = data[0].replace("-", "");

        if (!isValidAirlineCode(data[3])) {
            System.out.println("Line: " + count + " Error in Airline Code");
            return data_cleaned;
        }

        if (!isValidFlightNumber(data[5])) {
            System.out.println("Line: " + count + " Error in Flight Number");
            return data_cleaned;
        }

        if (!isValidIATACode(data[6])) {
            System.out.println("Line: " + count + " Error in Departure Airport Code");
            return data_cleaned;
        }

        if (!isValidIATACode(data[8])) {
            System.out.println("Line: " + count + " Error in Destination Airport Code");
            return data_cleaned;
        }

        data[10] = formatTime(data[10]);
        if (!data[10].matches("\\d{4}")) {
            System.out.println("Line: " + count + " Error in Expected Departure Time");
            return data_cleaned;
        }

        data[11] = formatTime(data[11]);
        if (!data[11].matches("\\d{4}")) {
            System.out.println("Line: " + count + " Error in Actual Departure Time");
            return data_cleaned;
        }

        data[17] = formatTime(data[17]);
        if (!data[17].matches("\\d{4}")) {
            System.out.println("Line: " + count + " Error in Expected Arrival Time");
            return data_cleaned;
        }

        data[18] = formatTime(data[18]);
        if (!data[18].matches("\\d{4}")) {
            System.out.println("Line: " + count + " Error in Actual Arrival Time");
            return data_cleaned;
        }

        if ("1.0".equals(data[20])) {
            System.out.println("Line: " + count + " Error Cancelled Flight");
            return data_cleaned;
        }

        //If row Contains delays data go through and collect Reasons and Calculate delay
        if (data.length == 32) {
            String delayReason = getDelayReason(data);
            data_cleaned.data[12] = delayReason;

            int delay = calculateDelay(data[17], data[18]);
            if (delay > 0) {
                data_cleaned.data[13] = String.valueOf(delay);
            } else {
                System.out.println("Line: " + count + " Error when calculating delay");
                return data_cleaned;
            }
        }

        //Assign Data to a cleaned small Array for Simplicity
        data_cleaned.valid = true;
        //Date
        data_cleaned.data[0] = data[0];
        //AirLine Name
        data_cleaned.data[1] = data[1];
        //Airline Code
        data_cleaned.data[2] = data[3];
        //Flight Number
        data_cleaned.data[3] = data[5];
        //Origin Code
        data_cleaned.data[4] = data[6];
        //Origin Name
        data_cleaned.data[5] = data[7];
        //Dest Code
        data_cleaned.data[6] = data[8];
        //Dest Name
        data_cleaned.data[7] = data[9];
        //Dept Time Scheduled
        data_cleaned.data[8] = data[10];
        //Dept Time Actual
        data_cleaned.data[9] = data[11];
        //Arrival Time Scheduled
        data_cleaned.data[10] = data[17];
        //Arrival Time Actual
        data_cleaned.data[11] = data[18];
        return data_cleaned;
    }

    //DB Batch Functions
    private void setAirportBatch(PreparedStatement pstmt, String code, String name) throws SQLException {
        pstmt.setString(1, code);
        pstmt.setString(2, name);
        pstmt.addBatch();
    }

    private void executeAllBatches(PreparedStatement... statements) throws SQLException {
        for (PreparedStatement stmt : statements) {
            if (stmt != null) stmt.executeBatch();
        }
    }


    //Validation Functions

    private int convertToMinutes(String timeStr) {
        // Handle times like "930", "1430", "1500"
        int hours = Integer.parseInt(timeStr.substring(0, timeStr.length() - 2));
        int minutes = Integer.parseInt(timeStr.substring(timeStr.length() - 2));

        return hours * 60 + minutes;  // Convert hours to minutes and add minutes
    }

    public int calculateDelay(String expectedStr, String actualStr) {

        int expectedMinutes = convertToMinutes(expectedStr);
        int actualMinutes = convertToMinutes(actualStr);

        // Calculate the delay in minutes
        int delay = actualMinutes - expectedMinutes;

        // Adjust for next day wrap-around
        if (delay < -720)
            delay += 1440;  // Add 24 hours
        else if (delay > 720)
            delay -= 1440;  // Subtract 24 hours


        return delay;
    }

    private boolean isValidDate(String date) {
        return date.matches("(2019|2020|2021|2022|2023)-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");
    }

    private boolean isValidAirlineCode(String code) {
        return code.matches("[a-zA-Z0-9]{2}");
    }

    private boolean isValidFlightNumber(String number) {
        return number.matches("\\d{1,4}");
    }

    private boolean isValidIATACode(String code) {
        return code.matches("[A-Z]{3}");
    }

    private String formatTime(String time) {
        time = time.replace(".0", "");
        while (time.length() < 4) {
            time = "0" + time;
        }
        return time;
    }

    private String getDelayReason(String[] data) {
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 27; i <= 31; i++) {
            if (!("0.0".equals(data[i]))) {
                reasonBuilder.append(DELAY_REASONS[i - 27]);
                if (i < 31) {
                    reasonBuilder.append(",");
                }
            }
        }
        // Remove trailing comma if any
        if (reasonBuilder.charAt(reasonBuilder.length() - 1) == ',') {
            reasonBuilder.setLength(reasonBuilder.length() - 1);
        }
        return reasonBuilder.toString();
    }


    //Getters Setters

    public Path getCsvPath() {
        return csvpath;
    }

    public void setCsvPath(Path csvpath) {
        this.csvpath = csvpath;
    }
}
