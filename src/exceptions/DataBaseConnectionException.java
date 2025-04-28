package exceptions;

import java.sql.SQLException;

public class DataBaseConnectionException extends SQLException {
    public DataBaseConnectionException(String errorMessage, Throwable err){
        super(errorMessage, err);
    }
}
