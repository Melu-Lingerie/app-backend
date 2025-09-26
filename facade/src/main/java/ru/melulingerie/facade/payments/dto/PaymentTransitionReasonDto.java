package ru.melulingerie.facade.payments.dto;

import lombok.Getter;

@Getter
public enum PaymentTransitionReasonDto {
    EXTERNAL_PAYMENT_CREATED("External payment was successfully created"),
    EXTERNAL_PAYMENT_FAILED("External payment creation failed"),
    EXTERNAL_PAYMENT_STATUS_UPDATED("External payment status was updated"),

    USER_CANCELLED("Payment was cancelled by user"),
    ADMIN_CANCELLED("Payment was cancelled by administrator"),
    SYSTEM_CANCELLED("Payment was cancelled by system"),

    AUTOMATIC_EXPIRY("Payment expired automatically"),
    CAPTURE_TIMEOUT("Payment capture timeout exceeded"),

    REFUND_REQUESTED("Refund was requested"),
    PARTIAL_REFUND_REQUESTED("Partial refund was requested"),

    CHARGEBACK_RECEIVED("Chargeback was received from payment provider"),
    FRAUD_DETECTED("Fraudulent activity detected"),

    WEBHOOK_NOTIFICATION("Status updated via webhook notification"),
    MANUAL_OVERRIDE("Manual status override by administrator");

    private final String description;

    PaymentTransitionReasonDto(String description) {
        this.description = description;
    }
}