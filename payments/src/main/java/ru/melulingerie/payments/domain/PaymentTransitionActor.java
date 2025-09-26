package ru.melulingerie.payments.domain;

public enum PaymentTransitionActor {
    SYSTEM,
    USER,
    ADMIN,
    PAYMENT_PROVIDER,
    API,
    WEBHOOK
}