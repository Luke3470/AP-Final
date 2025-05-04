package UI.Graph;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooChooPlaneGraphModel {

    private String db_url;

    private Object [][] graphData;

    private final Map<Integer,String> tableMap=new HashMap<Integer,String>(){{
        put(0,"Flight.flight_origin like ?");
        put(1,"Flight.flight_destination like ?");
        put(3,"Airline.name like ?");
        put(4,"Flight.airline_code like ?");
        put(5,"date <= ?");
        put(6,"date >= ?");
    }};


    ChooChooPlaneGraphModel(String DBUrl){
        this.db_url = DBUrl;
    }


    public Object [][] getGraphData(String [][] filters){

        List<Object> paramValues = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT 
                    Delay_Reason.delay_length as Length,     
                    COUNT(Flight.flight_id) as Total                    
                FROM Flight
                JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id
                """);

        StringBuilder countSql = new StringBuilder("""
                SELECT 
                    COUNT(DISTINCT(Delay_Reason.delay_length)) as Count
                FROM Flight
                JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id
                """);
        boolean first=true;
        for(int i=0;i<filters.length;i++){
            String condition = null;
            if (filters[i][0]!= null) {
                switch (filters[i][0]){
                   case "0":
                       paramValues.add(filters[i][0]);
                       break;
                   case "3":
                       sql.append("JOIN Airline ON Airline.iata_code = Flight.airline_Code");
                       countSql.append("JOIN Airline ON Airline.iata_code = Flight.airline_Code");
                       paramValues.add(filters[i][0]);
                       break;
                   case "5":
                       paramValues.add(Integer.parseInt(filters[i][1]));
                       break;
                    default:
                        continue;
                }
                condition = tableMap.get(filters[i][1]);
                if ((first) && (condition != null)){
                    sql.append("\n GROUP BY Delay_Reason.delay_length \n");
                    countSql.append("\n GROUP BY Delay_Reason.delay_length \n");
                    sql.append(" HAVING ").append(condition);
                    countSql.append(" HAVING ").append(condition);
                    first=false;
                }else {
                    sql.append(" AND ").append(condition);
                    countSql.append(" AND ").append(condition);
                }
            }
        }
        if (first){
            sql.append("\n GROUP BY Delay_Reason.delay_length \n");
        }
        sql.append(" ;");

        System.out.println("SQL:"+sql.toString());
        int count;

        try (Connection conn = DriverManager.getConnection(db_url)){

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            PreparedStatement count_pstmt = conn.prepareStatement(countSql.toString());


            for(int i=0;i<paramValues.size();i++){
                pstmt.setObject(i+1,paramValues.get(i));
                count_pstmt.setObject(i+1,paramValues.get(i));
            }

            count = Integer.parseInt(count_pstmt.executeQuery().getString("Count"));

            if (count!=0){
                ResultSet rs = pstmt.executeQuery();
                graphData = new Object[count][3];

                int i = 0;
                while (rs.next()) {
                    int length = rs.getInt("Length");
                    int total = rs.getInt("Total");

                    graphData[i++] = new Object[]{
                            total,                 // Value (Y-axis)
                            "Length " + length,    // Row key (series label)
                            "Flights"              // Column key (X-axis category)
                    };
                }
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return graphData;
    }

}