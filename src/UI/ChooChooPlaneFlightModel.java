package UI;

import java.io.File;
import java.sql.*;
import java.util.*;

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

    // Pagination logic
    public int getPagination() {
        return pagination;
    }

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

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }


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

    // Placeholder: Add search result logic later
    public Object [][] getSearchResults(Map<Integer,String> mapSearchParams, int columns,Object [] sort) {
        Map<Integer,String> valueLookup = getValueLookup();

        searchResults = new Object[pagination][columns];
        List<Object> paramValues = new ArrayList<>();


        StringBuilder sql = new StringBuilder("SELECT\n" +
                "    Flight.flight_number,\n" +
                "    Flight.date,\n" +
                "    AirportOrigin.name AS origin_airport_name,\n" +
                "    AirportDestination.name AS destination_airport_name,\n" +
                "    Airline.name AS airline_name,\n" +
                "    Flight.airline_code,\n" +
                "    Delay_Reason.delay_length,\n" +
                "    Delay_Reason.reason\n" +
                "FROM Flight\n" +
                "         JOIN Airport AS AirportOrigin ON AirportOrigin.iata_code = Flight.flight_origin\n" +
                "         JOIN Airport AS AirportDestination ON AirportDestination.iata_code = Flight.flight_destination\n" +
                "         JOIN Airline ON Airline.iata_code = Flight.airline_code\n" +
                "         LEFT OUTER JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id ");

                StringBuilder sql_count = new StringBuilder("SELECT COUNT(*) AS Total\n"+
                "FROM Flight\n" +
                "         JOIN Airport AS AirportOrigin ON AirportOrigin.iata_code = Flight.flight_origin\n" +
                "         JOIN Airport AS AirportDestination ON AirportDestination.iata_code = Flight.flight_destination\n" +
                "         JOIN Airline ON Airline.iata_code = Flight.airline_code\n" +
                "         LEFT OUTER JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id");


        if (!mapSearchParams.isEmpty()) {
            boolean first = true;
            for (Map.Entry<Integer, String> entry : mapSearchParams.entrySet()) {
                String column = valueLookup.get(entry.getKey());
                String value = entry.getValue();

                String condition;
                switch (column) {
                    case "flight_origin":
                    case "flight_destination":
                    case "airline_name":
                    case "airline_code":
                        value = "%"+value+"%";
                        condition = column + " LIKE ?";
                        paramValues.add(value);
                        break;
                    case "reason":
                        condition = column + "= ?";
                        paramValues.add(value);
                        break;
                    case "delay_length":
                    case "flight_number":
                    case "date":
                        paramValues.add(Integer.parseInt(value));
                        condition = column + " = ?";
                        break;
                    default:
                        continue;
                }
                if (first) {
                    first = false;

                    sql.append("\n WHERE \n").append(condition);
                    sql_count.append("\n WHERE \n").append(condition);
                }else {
                    sql.append(" AND ").append(condition);
                    sql_count.append(" AND ").append(condition);
                }

            }
        }
        if(sort != null){
            String col = getCol(sort[0]);
            if (col != null) {
                if(sort[1].toString() == "ASCENDING") {
                    sql.append(" ORDER BY ").append(col).append(" ASC;");
                }else {
                sql.append(" ORDER BY ").append(col).append(" DESC;");
                }
            }
        }else{
            sql.append(" ;");
            sql_count.append(" ;");
        }

        System.out.println("SQL: " + sql.toString());
        int count = 0;


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

    public void setSearchResults(Object[][] searchResults) {
        this.searchResults = searchResults;
    }


    public HashMap<Integer, String> getValueLookup(){
        HashMap<java.lang.Integer, java.lang.String> valueLookup = new HashMap<>();
        valueLookup.put(0,"flight_origin");
        valueLookup.put(1,"flight_destination");
        valueLookup.put(2,"flight_number");
        valueLookup.put(3,"airline_name");
        valueLookup.put(4,"airline_code");
        valueLookup.put(5,"date");
        valueLookup.put(6,"date");
        valueLookup.put(7,"delay_length");
        valueLookup.put(8,"reason");
        return valueLookup;
    }

    public void setDb_url(File db_url) {
        this.db_url = "jdbc:sqlite:"+db_url;
        this.has_db = true;
    }

    public boolean hasDB(){
        return has_db;
    }

    public String getDb_url() {
        return db_url;
    }

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

}

