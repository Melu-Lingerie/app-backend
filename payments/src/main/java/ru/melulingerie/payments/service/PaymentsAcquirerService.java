package ru.melulingerie.payments.service;

import ru.melulingerie.payments.dto.ExternalPaymentCancelResponse;
import ru.melulingerie.payments.dto.ExternalPaymentResponse;
import ru.melulingerie.payments.dto.PaymentCreateRequest;

import java.util.UUID;

public interface PaymentsAcquirerService {

    ExternalPaymentResponse createExternalPayment(PaymentCreateRequest request);

    ExternalPaymentCancelResponse cancelExternalPayment(String externalPaymentId, UUID idempotenceKey);
}