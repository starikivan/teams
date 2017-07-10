package juja.microservices.teams.exceptions;

/**
 * Created by Andrii Sidun
 */
public class TeamUserExistsException extends RuntimeException {
    public TeamUserExistsException(String message) {super(message);}
}
