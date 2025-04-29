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
                    pstmtAirport.setString(1, combo.data[4]);
                    pstmtAirport.setString(2, combo.data[5]);
                    pstmtAirport.addBatch();
                    pstmtAirport.setString(1, combo.data[6]);
                    pstmtAirport.setString(2, combo.data[7]);
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
                        pstmtAirline.executeBatch();
                        pstmtAirport.executeBatch();
                        pstmtFlight.executeBatch();
                        pstmtDelayReason.executeBatch();
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
            pstmtAirline.executeBatch();
            pstmtAirport.executeBatch();
            pstmtFlight.executeBatch();
            pstmtDelayReason.executeBatch();

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
        boolean valid = data[0].matches("(2019|2020|2021|2022|2023)-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");
        data[0] = data[0].replace("-", "");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Flight Date");
            return data_cleaned;
        }

        //Check Airline Code is Valid
        valid = data[3].matches("[a-zA-Z0-9]{2}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Airline Code");
            return data_cleaned;
        }

        //Check Flight Number
        valid = data[5].matches("[0-9]{1,4}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Flight Number");
            return data_cleaned;
        }

        //Check IATA Code of Airport is in Correct Format
        valid = data[6].matches("[A-Z]{3}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Departure Airport Code");
            return data_cleaned;
        }


        //Check IATA Code of Airport is in Correct Format
        valid = data[8].matches("[A-Z]{3}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Destination Airport Code");
            return data_cleaned;
        }


        //Remove add 0s so all times are formatted HHMM
        String temp = data[10];
        for (int i = data[10].length(); i < 4; i++) {
            temp = "0" + temp;
        }
        data[10] = temp;
        valid = data[10].matches("[0-9]{4}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Expected Departure Time");
            return data_cleaned;
        }


        //Remove trailing .0 and add 0 so all times are formatted HHMM
        temp = data[11].replace(".0", "");
        int temp_length = temp.length();
        for (int i = temp_length; i < 4; i++) {
            temp = "0" + temp;
        }
        data[11] = temp;

        valid = data[11].matches("[0-9]{4}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Actual Departure Time");
            return data_cleaned;
        }


        //Remove add 0s so all times are formatted HHMM
        temp = data[17];
        for (int i = data[17].length(); i < 4; i++) {
            temp = "0" + temp;
        }
        data[17] = temp;
        valid = data[17].matches("[0-9]{4}");

        if (!valid) {
            System.out.println("Line: " + count + " Error in Expected Arrival Time");
            return data_cleaned;
        }


        //Remove trailing .0 and add 0s so all times are formatted HHMM
        temp = data[18].replace(".0", "");
        temp_length = temp.length();

        for (int i = temp_length; i < 4; i++) {
            temp = "0" + temp;
        }
        data[18] = temp;

        valid = data[18].matches("[0-9]{4}");
        if (!valid) {
            System.out.println("Line: " + count + " Error in Actual Arrival Time");
            return data_cleaned;
        }


        //Delay Flight to be ignored
        if (data[20].equals("1.0")) {
            System.out.println("Line: "+count+ " Error Cancelled Flight");
            return data_cleaned;
        }

        //If row Contains delays data go through and collect Reasons and Calculate delay
        if (data.length == 32) {
            String delay_reason = "";
            for (int i = 27; i < 32; i++) {
                if (!Objects.equals(data[i], "")) {
                    if (data[i].equals("0.0")) {
                        delay_reason = switch (i) {
                            case 27 -> delay_reason + "Carrier,";
                            case 28 -> delay_reason + "Weather,";
                            case 29 -> delay_reason + "NAS,";
                            case 30 -> delay_reason + "Security,";
                            case 31 -> delay_reason + "Aircraft";
                            default -> throw new IllegalStateException("Unexpected value: " + i);
                        };
                    }
                }
            }
            data_cleaned.data[12] = delay_reason;
            //Calculated delay
            int delay = calculateDelay(data[17],data[18]);
            if (delay > 0) {
                data_cleaned.data[13] = String.valueOf(delay);
            }else {
                System.out.println("Line: "+count+ " Error when calculating delay");
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

    public Path getCsvPath() {
        return csvpath;
    }

    public void setCsvPath(Path csvpath) {
        this.csvpath = csvpath;
    }
}
