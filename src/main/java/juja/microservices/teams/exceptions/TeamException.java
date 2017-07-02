package juja.microservices.teams.exceptions;

public class TeamException extends RuntimeException {
    public TeamException(String message) {
        super(message);
    }
}