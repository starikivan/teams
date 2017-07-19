package juja.microservices.teams.exceptions;

/**
 * Created by Andrii Sidun
 */
public class UserAlreadyInTeamException extends RuntimeException {
    public UserAlreadyInTeamException(String message) {super(message);}
}
