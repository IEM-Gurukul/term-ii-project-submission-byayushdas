package exception;

/**
 * Thrown when portfolio save or load operations fail.
 */
public class PortfolioPersistenceException extends Exception {
    public PortfolioPersistenceException(String message) {
        super(message);
    }
    public PortfolioPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
