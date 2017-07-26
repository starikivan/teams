package ua.com.juja.microservices.teams.exceptions;

/**
 * Created by Andrii Sidun
 */
public class UserAlreadyInTeamException extends TeamsException {
    public UserAlreadyInTeamException(String message) {super(message);}
}
