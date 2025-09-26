package ru.melulingerie.payments.exception;

import ru.melulingerie.payments.domain.PaymentMethod;

import java.util.UUID;

public class ExternalPaymentException extends PaymentException {

    public ExternalPaymentException(String operation, PaymentMethod paymentMethod, String reason, Throwable cause) {
        super(PaymentErrorCode.EXTERNAL_PAYMENT_FAILED,
              String.format("Failed to %s external %s payment: %s", operation, paymentMethod, reason),
              cause);
    }

    public ExternalPaymentException(String operation, PaymentMethod paymentMethod, String reason) {
        super(PaymentErrorCode.EXTERNAL_PAYMENT_FAILED,
              String.format("Failed to %s external %s payment: %s", operation, paymentMethod, reason));
    }

    public static ExternalPaymentException creation(PaymentMethod paymentMethod, String reason, Throwable cause) {
        return new ExternalPaymentException("create", paymentMethod, reason, cause);
    }

    public static ExternalPaymentException creation(PaymentMethod paymentMethod, String reason) {
        return new ExternalPaymentException("create", paymentMethod, reason);
    }

    public static ExternalPaymentException cancellation(UUID externalPaymentId, String reason, Throwable cause) {
        return new ExternalPaymentException("cancel", null,
                String.format("payment %s: %s", externalPaymentId, reason), cause);
    }

    public static ExternalPaymentException cancellation(UUID externalPaymentId, String reason) {
        return new ExternalPaymentException("cancel", null,
                String.format("payment %s: %s", externalPaymentId, reason));
    }
}