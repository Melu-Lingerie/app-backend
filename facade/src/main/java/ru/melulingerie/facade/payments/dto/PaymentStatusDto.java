package ru.melulingerie.facade.payments.dto;

public enum PaymentStatusDto {
    PENDING,
    WAITING_FOR_CAPTURE,
    SUCCEEDED,
    CANCELED,
    FAILED,
    EXPIRED,
    REFUNDED,
    PARTIALLY_REFUNDED
}