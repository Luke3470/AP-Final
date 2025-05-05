package UI.Graph;


import java.sql.*;
import java.util.*;

public class ChooChooPlaneGraphModel {

    private String db_url;

    private Object [][] graphData;

    ChooChooPlaneGraphModel(String DBUrl){
        this.db_url = DBUrl;
    }


    public Object[][] getBarChartData(Map<Integer,String> mapParams,String groupBy) {

        List<Object> paramValues = new ArrayList<>();
        boolean joinAirline =false;
        StringBuilder sqlSelect = new StringBuilder("""
                select
                    round(AVG(Delay_Reason.delay_length),0) as Average,
                """);
        StringBuilder sqlJoins = new StringBuilder("""
                     JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id
                """);

        if(Objects.equals(groupBy, "Group by Airline")){
            sqlSelect.append("\n airline_code").append("\nFrom Flight");
            sqlJoins.append("\n join Airline on Flight.airline_code = Airline.iata_code");
        }else {
            sqlSelect.append("\n airline_code").append("\nFrom Flight");
            sqlJoins.append("\n join Airport on Flight.flight_destination = Airport.iata_code");
            joinAirline=true;
        }

        StringBuilder sqlWhere = new StringBuilder("""
                
                """);

        Map<Integer,String> valueLookup = getValueLookup();
        if (!mapParams.isEmpty()) {
            boolean first = true;
            for (Map.Entry<Integer, String> entry : mapParams.entrySet()) {
                String column = valueLookup.get(entry.getKey());
                String value = entry.getValue();
                String condition;
                if ((column !=null)&&(!column.equals("Flight.flight_origin"))) {
                    switch (column) {
                        case "Flight.flight_destination":
                        case "Airline.name":
                            if (!joinAirline){
                                sqlJoins.append("JOIN Airline ON Airline.iata_code = Flight.airline_code");
                                joinAirline = true;
                            }
                        case "Flight.airline_code":
                            value = "%" + value + "%";
                            condition = column + " LIKE ?";
                            paramValues.add(value);
                            break;
                        case "reason":
                            condition = column + "= ?";
                            paramValues.add(value);
                            break;
                        case "delay_length":
                            if (mapParams.get(9) != null) {
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
                        sqlWhere.append("Where ").append(condition);

                    } else {
                        sqlWhere.append(" AND ").append(condition);
                    }
                }
            }
        }
        String sql = sqlSelect.append("\n").append(sqlJoins).append("\n").append(sqlWhere).append("\n").append("GROUP BY Delay_reason.delay_length;").toString();

        System.out.println("SQL:"+sql);

        try (Connection conn = DriverManager.getConnection(db_url)){

            PreparedStatement pstmt = conn.prepareStatement(sql);


            for(int i=0;i<paramValues.size();i++){
                pstmt.setObject(i+1,paramValues.get(i));
            }


            int count = 0;

            ResultSet rs = pstmt.executeQuery();
            List<Object[]> dataList = new ArrayList<>();

            while (rs.next()) {
                count++;
                String title;
                String group;
                if(Objects.equals(groupBy, "Group by Airline")) {
                    group = rs.getString("airline_code");
                    title = "Airline " + group ;
                }else {
                    group = rs.getString("Flight.flight_origin");
                    title = "Destination Airport " + group ;
                }
                int average = rs.getInt("Average");

                dataList.add(new Object[]{
                        average,                     // Value (Y-axis)
                        title,   // Row key (series label)
                        group                     // Column key (X-axis category)
                });
            }

            this.graphData = dataList.toArray(new Object[0][]);
            if (count == 0){
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return graphData;
    }

    public Object [][] getLineChartData(Map<Integer,String> mapParams){

        List<Object> paramValues = new ArrayList<>();
        StringBuilder sqlSelect = new StringBuilder("""
                SELECT
                    Delay_Reason.delay_length as Length,
                    COUNT(Flight.flight_id) as Total
                FROM Flight
                """);
        StringBuilder sqlJoins = new StringBuilder("""
                     JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id
                """);
        StringBuilder sqlWhere = new StringBuilder("""
                
                """);

        Map<Integer,String> valueLookup = getValueLookup();
        if (!mapParams.isEmpty()) {
            boolean first = true;
            boolean joinAirline =false;
            for (Map.Entry<Integer, String> entry : mapParams.entrySet()) {
                String column = valueLookup.get(entry.getKey());
                String value = entry.getValue();

                String condition;
                if (column !=null) {
                    switch (column) {
                        case "Flight.flight_origin":
                        case "Flight.flight_destination":
                        case "Airline.name":
                            if (!joinAirline){
                                sqlJoins.append("JOIN Airline ON Airline.iata_code = Flight.airline_code");
                                joinAirline = true;
                            }
                        case "Flight.airline_code":
                            value = "%" + value + "%";
                            condition = column + " LIKE ?";
                            paramValues.add(value);
                            break;
                        case "reason":
                            condition = column + "= ?";
                            paramValues.add(value);
                            break;
                        case "delay_length":
                            if (mapParams.get(9) != null) {
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
                        sqlWhere.append("Where ").append(condition);

                    } else {
                        sqlWhere.append(" AND ").append(condition);
                    }
                }
            }
        }
        String sql = sqlSelect.append("\n").append(sqlJoins).append("\n").append(sqlWhere).append("\n").append("GROUP BY Delay_reason.delay_length;").toString();

        System.out.println("SQL:"+sql);

        try (Connection conn = DriverManager.getConnection(db_url)){

            PreparedStatement pstmt = conn.prepareStatement(sql);


            for(int i=0;i<paramValues.size();i++){
                pstmt.setObject(i+1,paramValues.get(i));
            }


            int count = 0;

            ResultSet rs = pstmt.executeQuery();
            List<Object[]> dataList = new ArrayList<>();

            while (rs.next()) {
                count++;
                int length = rs.getInt("Length");
                int total = rs.getInt("Total");

                dataList.add(new Object[]{
                        total,                     // Value (Y-axis)
                        "Time Of Delay",   // Row key (series label)
                        length                     // Column key (X-axis category)
                });
            }

            this.graphData = dataList.toArray(new Object[0][]);
            if (count == 0){
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return graphData;
    }

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

}