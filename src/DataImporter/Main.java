package DataImporter;

import exceptions.DataBaseConnectionException;
import exceptions.IncorrectFilePathException;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws DataBaseConnectionException {

        DB dataBase = new DB();
        DI dataImporter = new DI();

        long startTime = System.currentTimeMillis();

        try {
            dataImporter.startImport(dataBase.getConn());
        } catch (SQLException e) {
            System.out.println("Couldn't Connect to DB Please Check Connection String");
            throw new DataBaseConnectionException((dataBase.getDB_URL()), e);
        } catch (IncorrectFilePathException e) {
            System.out.println("File Path for CSV is Incorrect");
            throw new RuntimeException(e);
        }


        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        //Timing of data import
        System.out.println("Data Import execution time: " + duration / 1000 + " Seconds");

        dataBase.createIndex();

    }

}
