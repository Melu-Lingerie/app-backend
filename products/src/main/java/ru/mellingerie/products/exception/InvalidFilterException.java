package ru.mellingerie.products.exception;

public class InvalidFilterException extends RuntimeException {
    
    public InvalidFilterException(String message) {
        super(message);
    }
    
    public InvalidFilterException(String parameter, String reason) {
        super("Invalid filter parameter '" + parameter + "': " + reason);
    }
} 