package ua.com.juja.microservices.teams.exceptions;

public class TeamsException extends RuntimeException {
    public TeamsException(String message) {
        super(message);
    }
}