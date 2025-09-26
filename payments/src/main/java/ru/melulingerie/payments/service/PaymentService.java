package ru.melulingerie.payments.service;

import ru.melulingerie.payments.dto.PaymentCancelRequest;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentCreateRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse getPaymentByExternalId(String externalPaymentId);

    PaymentResponse cancelPayment(PaymentCancelRequest request);

    PaymentResponse refundPayment(Long paymentId, String reason);
}