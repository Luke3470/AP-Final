package UI.main;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Class for the data Model of the Flight Data required for Table Population
 * and DAta for ComboBox options
 */
public class ChooChooPlaneFlightModel {

    private String db_url ;
    private boolean has_db;

    private int pagination = 25;
    private int page = 1;
    private int maxPage = 10;

    private Object[][] searchResults;

    public ChooChooPlaneFlightModel() {
        this.has_db = false;

    }

    /**
     * Gets results for the Filter ComboBox
     *
     * @return String Array containing all distinct selay reasons
     */
    public String [] getComboBoxResults(){

        try (Connection conn = DriverManager.getConnection(db_url)){
            Statement stmt = conn.createStatement();
            Statement count_stmt = conn.createStatement();


            int total = Integer.parseInt(count_stmt.executeQuery("SELECT COUNT(DISTINCT(reason)) AS Total FROM Delay_Reason").getString("Total"));
            int count = 1;
            String [] comboBoxResults = new String[total+1];

            ResultSet rs = stmt.executeQuery("SELECT DISTINCT(reason) as d_reason FROM Delay_Reason");

            comboBoxResults[0] = "None";
            while (rs.next()){
                comboBoxResults[count] = rs.getString("d_reason");
                count ++;
            }

            return comboBoxResults;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets Search results for table populations
     * takes into account pagination and selected filters
     *
     * @param mapSearchParams Map Containing all Search Parameters
     * @param columns total number of columns in the GUI table
     * @param sort if any headers are being sorted on
     * @return Object 2D Array Containing data for the tables top be populated
     */

    public Object [][] getSearchResults(Map<Integer,String> mapSearchParams, int columns,Object [] sort) {
        Map<Integer,String> valueLookup = getValueLookup();

        searchResults = new Object[pagination][columns];
        List<Object> paramValues = new ArrayList<>();

        //Two string builders one to help with Pagination logic

        StringBuilder sql = new StringBuilder("""
                SELECT
                    Flight.flight_number,
                    Flight.date,
                    AirportOrigin.name AS origin_airport_name,
                    AirportDestination.name AS destination_airport_name,
                    Airline.name AS airline_name,
                    Flight.airline_code,
                    Delay_Reason.delay_length,
                    Delay_Reason.reason
                FROM Flight
                         JOIN Airport AS AirportOrigin ON AirportOrigin.iata_code = Flight.flight_origin
                         JOIN Airport AS AirportDestination ON AirportDestination.iata_code = Flight.flight_destination
                         JOIN Airline ON Airline.iata_code = Flight.airline_code
                         LEFT OUTER JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id\s""");

        StringBuilder sql_count = new StringBuilder("""
                SELECT COUNT(*) AS Total
                FROM Flight
                         JOIN Airport AS AirportOrigin ON AirportOrigin.iata_code = Flight.flight_origin
                         JOIN Airport AS AirportDestination ON AirportDestination.iata_code = Flight.flight_destination
                         JOIN Airline ON Airline.iata_code = Flight.airline_code
                         LEFT OUTER JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id""");

        //Apply filters
        if (!mapSearchParams.isEmpty()) {
            boolean first = true;
            for (Map.Entry<Integer, String> entry : mapSearchParams.entrySet()) {
                String column = valueLookup.get(entry.getKey());
                String value = entry.getValue();

                String condition;
                if (column !=null) {
                    switch (column) {
                        case "flight_origin":
                        case "flight_destination":
                        case "Airline.name":
                        case "airline_code":
                            value = "%" + value + "%";
                            condition = column + " LIKE ?";
                            paramValues.add(value);
                            break;
                        case "reason":
                            condition = column + "= ?";
                            paramValues.add(value);
                            break;
                        case "delay_length":
                            if (mapSearchParams.get(9) != null) {
                                condition = column + " <= ?";

                            } else {
                                condition = column + " >= ?";
                            }
                            paramValues.add(Integer.parseInt(value));
                            break;
                        case "flight_number":
                            paramValues.add(Integer.parseInt(value));
                            condition = column + " = ?";
                            break;
                        case "start_date":
                        case "end_date":
                            paramValues.add(Integer.parseInt(value));
                            if (column.equals("start_date")) {
                                condition = "date >= ?";
                            } else {
                                condition = "date <= ?";
                            }
                            break;
                        default:
                            continue;
                    }

                    if (first) {
                        first = false;
                        sql.append("\n WHERE \n").append(condition);
                        sql_count.append("\n WHERE \n").append(condition);
                    } else {
                        sql.append(" AND ").append(condition);
                        sql_count.append(" AND ").append(condition);
                    }
                }
            }
        }
        if(sort != null){
            String col = getCol(sort[0]);
            if (col != null) {
                if(Objects.equals(sort[1].toString(), "ASCENDING")) {
                    sql.append(" ORDER BY ").append(col).append(" ASC;");
                }else {
                sql.append(" ORDER BY ").append(col).append(" DESC;");
                }
            }
        }else{
            sql.append(" ;");
            sql_count.append(" ;");
        }

        System.out.println("SQL: " + sql);
        int count;


        try (Connection conn = DriverManager.getConnection(db_url)) {

             PreparedStatement pstmt = conn.prepareStatement(sql.toString());
             PreparedStatement count_pstmt = conn.prepareStatement(sql_count.toString());

            // Set parameter values
            for (int i = 0; i < paramValues.size(); i++) {
                pstmt.setObject(i + 1, paramValues.get(i));
                count_pstmt.setObject(i + 1, paramValues.get(i));
            }

            count = Integer.parseInt(count_pstmt.executeQuery().getString("Total"));

            maxPage = (int) Math.ceil((double) count /pagination);

            if (count > 0){
                int total = 0;
                int requiredRecords = 0;
                int start = ((getPage()-1)*pagination);
                int max = start + pagination;
                if (page == 1) {
                    max++;
                }

                pstmt.setMaxRows(getMaxPage()*pagination);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next() && requiredRecords != pagination) {
                    total ++;
                    if ((total<max) && (total>=start)) {

                        searchResults[requiredRecords] = new Object[] {
                                rs.getInt("flight_number"),
                                rs.getInt("date"),
                                rs.getString("origin_airport_name"),
                                rs.getString("destination_airport_name"),
                                rs.getString("airline_name"),
                                rs.getString("airline_code"),
                                rs.getInt("delay_length"),
                                rs.getString("reason"),
                        };
                        requiredRecords ++;
                    }
                }

            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }


        return searchResults;
    }


    /**
     * Lookup to compare Int values for Columns declared in ChooChooPlaneController
     *
     * @return Map to correlate int to its db column
     */
    public HashMap<Integer, String> getValueLookup(){
        HashMap<java.lang.Integer, java.lang.String> valueLookup = new HashMap<>();
        valueLookup.put(0,"flight_origin");
        valueLookup.put(1,"flight_destination");
        valueLookup.put(2,"flight_number");
        valueLookup.put(3,"Airline.name");
        valueLookup.put(4,"airline_code");
        valueLookup.put(5,"start_date");
        valueLookup.put(6,"end_date");
        valueLookup.put(7,"delay_length");
        valueLookup.put(8,"reason");
        return valueLookup;
    }

    public boolean hasDB(){
        return has_db;
    }

    /**
     * Lookup to return table name given a column header
     *
     * @param col a table header Column
     * @return equivalent String table values of that header
     */
    public String getCol(Object col){
        String str = col.toString();
        return switch (str) {
            case "Flight Number" -> "flight_number";
            case "Arrival Airport" -> "destination_airport_name";
            case "Departure Airport" -> "origin_airport_name";
            case "Airline" -> "airline_name";
            case "Airline Code" -> "Airline Code";
            case "Date" -> "date";
            case "Delay" -> "delay_length";
            case "Delay Reason" -> "reason";
            default -> null;
        };
    }

    //Getters and setters

    public void setPagination(int pagination) {
        this.pagination = pagination;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(File db_url) {
        this.db_url = "jdbc:sqlite:"+db_url;
        this.has_db = true;
    }

}

