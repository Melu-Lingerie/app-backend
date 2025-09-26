package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.mapper.PaymentStateTransitionMapper;
import ru.melulingerie.payments.mapper.PaymentsAcquirerMapper;
import ru.melulingerie.payments.generated.model.Payment;
import ru.melulingerie.payments.dto.PaymentResponse;
import ru.melulingerie.payments.exception.WebhookValidationException;
import ru.melulingerie.payments.config.properties.PaymentsApiProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookServiceImpl implements PaymentWebhookService {
    private final PaymentService paymentService;
    private final PaymentStateService paymentStateService;
    private final PaymentStateTransitionMapper stateTransitionMapper;
    private final PaymentsAcquirerMapper paymentsAcquirerMapper;
    private final PaymentsApiProperties paymentsApiProperties;

    @Override
    @Transactional
    public void processPaymentAcquirerWebhook(Payment webhookPayment, String authorization) {
        log.info("Processing YooKassa webhook for payment: {}", webhookPayment.getId());

        validateWebhookSignature(authorization);

        String externalPaymentId = webhookPayment.getId();

        PaymentResponse localPayment = paymentService.getPaymentByExternalId(externalPaymentId);

        ru.melulingerie.payments.domain.PaymentStatus newStatus = paymentsAcquirerMapper.mapStatus(webhookPayment.getStatus());

        if (localPayment.getStatus() != newStatus) {
            paymentStateService.transitPaymentStatus(
                stateTransitionMapper.createStatusUpdateRequest(
                    localPayment.getId(),
                    newStatus,
                    PaymentTransitionReason.WEBHOOK_NOTIFICATION
                )
            );

            log.info("Payment {} status updated to {} via webhook", localPayment.getId(), newStatus);
        } else {
            log.debug("Payment {} already in status {}", localPayment.getId(), newStatus);
        }
    }

    private void validateWebhookSignature(String authorization) {
        String webhookSecret = paymentsApiProperties.webhook().secret();

        if (!webhookSecret.equals(authorization)) {
            throw new WebhookValidationException("Invalid webhook signature");
        }
    }
}