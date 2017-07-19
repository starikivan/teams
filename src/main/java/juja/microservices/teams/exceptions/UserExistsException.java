package juja.microservices.teams.exceptions;

/**
 * Created by Andrii Sidun
 */
public class UserExistsException extends RuntimeException {
    public UserExistsException(String message) {super(message);}
}
