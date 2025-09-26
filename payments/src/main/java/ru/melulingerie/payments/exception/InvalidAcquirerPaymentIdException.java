package ru.melulingerie.payments.exception;

public class InvalidAcquirerPaymentIdException extends RuntimeException {

    public InvalidAcquirerPaymentIdException(String message) {
        super(message);
    }

    public InvalidAcquirerPaymentIdException(String message, Throwable cause) {
        super(message, cause);
    }
}