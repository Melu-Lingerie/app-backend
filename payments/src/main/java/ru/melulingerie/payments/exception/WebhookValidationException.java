package ru.melulingerie.payments.exception;

public class WebhookValidationException extends PaymentException {
    public WebhookValidationException(String message) {
        super(PaymentErrorCode.WEBHOOK_VALIDATION_FAILED, message);
    }
}