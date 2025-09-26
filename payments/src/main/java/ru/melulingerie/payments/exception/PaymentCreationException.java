package ru.melulingerie.payments.exception;

import java.util.UUID;

public class PaymentCreationException extends PaymentException {

    public PaymentCreationException(UUID orderId, String reason, Throwable cause) {
        super(PaymentErrorCode.PAYMENT_CREATION_FAILED,
              String.format("Failed to create payment for order %s: %s", orderId, reason),
              cause);
    }

    public PaymentCreationException(UUID orderId, String reason) {
        super(PaymentErrorCode.PAYMENT_CREATION_FAILED,
              String.format("Failed to create payment for order %s: %s", orderId, reason));
    }
}