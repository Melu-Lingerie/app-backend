package ru.melulingerie.facade.payments.service;

public interface PaymentWebhookFacadeService {
    void processPaymentAcquirerWebhook(String paymentJson, String authorization);
}