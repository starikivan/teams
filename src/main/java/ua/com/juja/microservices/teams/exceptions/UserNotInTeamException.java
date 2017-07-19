package ua.com.juja.microservices.teams.exceptions;

public class UserNotInTeamException extends TeamsException {
    public UserNotInTeamException(String message) {
        super(message);
    }
}