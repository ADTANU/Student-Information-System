package exception;

import java.sql.SQLException;

public class TeacherNotFoundException extends Exception {
    
    // Constructor that takes a message
    public TeacherNotFoundException(String message) {
        super(message);
    }

    // Constructor that takes a message and a SQLException as the cause
    public TeacherNotFoundException(String message, SQLException cause) {
        super(message, cause);
    }
}
