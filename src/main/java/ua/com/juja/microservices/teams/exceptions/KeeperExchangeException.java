package ua.com.juja.microservices.teams.exceptions;

/**
 * @author Ivan Shapovalov
 */
public class KeeperExchangeException extends RuntimeException {
    private final ApiErrorMessage error;
    public KeeperExchangeException(ApiErrorMessage error,String message) {
        super(message);
        this.error=error;
    }
}
