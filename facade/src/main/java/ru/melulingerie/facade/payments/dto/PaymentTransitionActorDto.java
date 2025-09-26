package ru.melulingerie.facade.payments.dto;

public enum PaymentTransitionActorDto {
    SYSTEM,
    USER,
    ADMIN,
    PAYMENT_PROVIDER,
    API,
    WEBHOOK
}