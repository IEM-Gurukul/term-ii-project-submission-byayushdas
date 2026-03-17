package exception;

/**
 * Thrown when investment input data is invalid.
 */
public class InvalidInvestmentException extends Exception {
    public InvalidInvestmentException(String message) {
        super(message);
    }
    public InvalidInvestmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
