package exception;

public class InvalidCourseDataException extends Exception {

    // Constructor accepting a custom message
    public InvalidCourseDataException(String message) {
        super(message);  // Pass the message to the parent Exception class
    }

    // Constructor without any message (optional)
    public InvalidCourseDataException() {
        super("Invalid course data provided.");
    }
}

