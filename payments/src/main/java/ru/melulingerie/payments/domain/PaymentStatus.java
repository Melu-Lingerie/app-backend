package ru.melulingerie.payments.domain;

public enum PaymentStatus {
    PENDING,
    WAITING_FOR_CAPTURE,
    SUCCEEDED,
    CANCELED,
    FAILED,
    EXPIRED,
    REFUNDED,
    PARTIALLY_REFUNDED
}