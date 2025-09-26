package ru.melulingerie.payments.exception;

import lombok.Getter;

@Getter
public enum PaymentErrorCode {
    PAYMENT_NOT_FOUND("PAYMENT_001", "Payment not found"),
    PAYMENT_INVALID_STATUS("PAYMENT_002", "Payment status does not allow this operation"),
    PAYMENT_CREATION_FAILED("PAYMENT_003", "Failed to create payment"),
    PAYMENT_CANCELLATION_FAILED("PAYMENT_004", "Failed to cancel payment"),
    PAYMENT_REFUND_FAILED("PAYMENT_005", "Failed to refund payment"),
    EXTERNAL_PAYMENT_FAILED("PAYMENT_006", "External payment service error"),
    UNSUPPORTED_PAYMENT_METHOD("PAYMENT_007", "Unsupported payment method"),
    INVALID_PAYMENT_REQUEST("PAYMENT_008", "Invalid payment request data"),
    WEBHOOK_VALIDATION_FAILED("PAYMENT_009", "Webhook signature validation failed");

    private final String code;
    private final String defaultMessage;

    PaymentErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}