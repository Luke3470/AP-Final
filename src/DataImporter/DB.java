package DataImporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    final private String DB_URL = "jdbc:sqlite:C:\\Users\\ljcad\\IdeaProjects\\AP-Project\\src\\DataImporter\\database.sqlite";

    DB() {
        // Open a connection
        System.out.println("Dropping All Tables");
        dropTables();
        System.out.println("Creating All Tables");
        createTables();
    }

    public void createIndex() {
        try (Statement stmt = getStmt()) {
            // Indexes for Flight table
            stmt.executeUpdate("CREATE INDEX idx_flight_airline_code ON Flight(airline_code);");
            stmt.executeUpdate("CREATE INDEX idx_flight_origin ON Flight(flight_origin);");
            stmt.executeUpdate("CREATE INDEX idx_flight_destination ON Flight(flight_destination);");
            stmt.executeUpdate("CREATE INDEX idx_flight_date ON Flight(date);");

            // Indexes for Delay_Reason table
            stmt.executeUpdate("CREATE INDEX idx_delay_flight_id ON Delay_Reason(flight_id);");
            stmt.executeUpdate("CREATE INDEX idx_delay_length ON Delay_Reason(delay_length);");
            stmt.executeUpdate("CREATE INDEX idx_delay_reason ON Delay_Reason(reason);");

            //Airline Airport
            stmt.executeUpdate("CREATE INDEX idx_airline_name ON Airline(name);");
            stmt.executeUpdate("CREATE INDEX idx_airport_name ON Airport(name);");


        } catch (SQLException e) {
            dropTables();
            System.out.println("Could Not Create Indexes");
            throw new RuntimeException(e);
        }
    }

    public void dropTables() {
        //Drop all Tables
        try (Statement stmt = getStmt()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS Delay_Reason");
            stmt.executeUpdate("DROP TABLE IF EXISTS Flight");
            stmt.executeUpdate("DROP TABLE IF EXISTS Airport");
            stmt.executeUpdate("DROP TABLE IF EXISTS Airline");
            //Clean DB space
            stmt.executeUpdate("VACUUM");
        } catch (SQLException e) {
            System.out.println("Tables Could not be Dropped");
            throw new RuntimeException(e);
        }
    }

    public void createTables() {
        try (Statement stmt = getStmt()) {
            stmt.executeUpdate("CREATE TABLE Airline(" +
                    "name      TEXT ," +
                    "iata_code TEXT CHECK (LENGTH(iata_code) = 2)" +
                    "   constraint iata_code" +
                    "       primary key);");

            stmt.executeUpdate("CREATE TABLE Airport(" +
                    "name      TEXT," +
                    "iata_code TEXT CHECK (LENGTH(iata_code) = 3)" +
                    "   constraint iata_code" +
                    "       primary key);");

            stmt.executeUpdate("CREATE TABLE Flight(" +
                    "date      TEXT CHECK (LENGTH(date) = 8)," +
                    "flight_number integer," +
                    "scheduled_departure integer," +
                    "actual_departure integer," +
                    "scheduled_arrival integer," +
                    "actual_arrival integer," +
                    "flight_id integer" +
                    "CONSTRAINT flight_id" +
                    "primary key," +
                    "airline_code TEXT CHECK (LENGTH(airline_code) = 2)" +
                    "   CONSTRAINT airline_code" +
                    "       references Airline," +
                    "flight_origin TEXT CHECK (LENGTH(flight_origin) = 3)" +
                    "   CONSTRAINT flight_origin" +
                    "       references Airport," +
                    "flight_destination TEXT CHECK (LENGTH(flight_destination) = 3)" +
                    "   CONSTRAINT flight_destination" +
                    "       references Airport);"
            );

            stmt.executeUpdate("CREATE TABLE Delay_Reason(" +
                    "reason      TEXT," +
                    "delay_length integer," +
                    "delay_id   integer" +
                    "   constraint delay_id " +
                    "       primary key ," +
                    "flight_id  integer" +
                    "   constraint flight_id" +
                    "       references Flight);");

        } catch (SQLException e) {
            dropTables();
            System.out.println("Error When Creating Tables");
            throw new RuntimeException(e);
        }
    }

    public Statement getStmt() throws SQLException {
        Connection conn = DriverManager.getConnection(getDB_URL());
        return conn.createStatement();

    }

    public Connection getConn() throws SQLException {
        return DriverManager.getConnection(getDB_URL());
    }

    public String getDB_URL() {
        return DB_URL;
    }
}