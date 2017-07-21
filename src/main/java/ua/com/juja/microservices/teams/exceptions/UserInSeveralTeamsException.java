package ua.com.juja.microservices.teams.exceptions;

public class UserInSeveralTeamsException extends TeamsException {
    public UserInSeveralTeamsException(String message) {
        super(message);
    }
}