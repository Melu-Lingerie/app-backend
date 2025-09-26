package ru.melulingerie.payments.service;

import ru.melulingerie.payments.generated.model.Payment;

public interface PaymentWebhookService {
    void processPaymentAcquirerWebhook(Payment payment, String authorization);
}