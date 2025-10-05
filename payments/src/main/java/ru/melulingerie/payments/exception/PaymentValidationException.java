package ru.melulingerie.payments.exception;

public class PaymentValidationException extends PaymentException {

    public PaymentValidationException(String userMessage) {
        super(PaymentErrorCode.INVALID_PAYMENT_REQUEST, userMessage);
    }

    public PaymentValidationException(String userMessage, Throwable cause) {
        super(PaymentErrorCode.INVALID_PAYMENT_REQUEST, userMessage, cause);
    }
}