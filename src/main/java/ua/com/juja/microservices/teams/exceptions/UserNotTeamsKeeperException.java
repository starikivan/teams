package ua.com.juja.microservices.teams.exceptions;

/**
 * @author Ivan Shapovalov
 */
public class UserNotTeamsKeeperException extends RuntimeException {
    public UserNotTeamsKeeperException(String message) {
        super(message);
    }
}
