package ru.melulingerie.facade.payments.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.payments.service.PaymentWebhookFacadeService;
import ru.melulingerie.payments.generated.model.Payment;
import ru.melulingerie.payments.service.PaymentWebhookService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookFacadeServiceImpl implements PaymentWebhookFacadeService {

    private final PaymentWebhookService paymentWebhookService;
    private final ObjectMapper objectMapper;

    @Override
    public void processPaymentAcquirerWebhook(String paymentJson, String authorization) {
        try {
            Payment payment = objectMapper.readValue(paymentJson, Payment.class);
            paymentWebhookService.processPaymentAcquirerWebhook(payment, authorization);
        } catch (Exception e) {
            log.error("Failed to parse webhook payment JSON: {}", paymentJson, e);
            throw new RuntimeException("Failed to process webhook payment", e);
        }
    }
}