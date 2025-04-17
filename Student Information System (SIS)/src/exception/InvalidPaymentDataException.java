package exception;

public class InvalidPaymentDataException extends Exception {
    public InvalidPaymentDataException(String message) {
        super(message);
    }
}
