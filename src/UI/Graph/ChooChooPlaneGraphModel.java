package UI.Graph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChooChooPlaneGraphModel {

    private String db_url;

    private Object [][] graphData;


    ChooChooPlaneGraphModel(String DBUrl){
        this.db_url = DBUrl;
    }


    public Object [][] getGraphData(String [][] filters){
        try (Connection conn = DriverManager.getConnection(db_url)){

            PreparedStatement pstmt = conn.prepareStatement("SELECT Delay_Reason.delay_length as Length, COUNT(Flight.flight_id) as Total\n" +
                    "FROM Flight\n" +
                    "         JOIN Delay_Reason ON Delay_Reason.flight_id = Flight.flight_id\n" +
                    "GROUP BY delay_length;");





        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return graphData;
    }

}