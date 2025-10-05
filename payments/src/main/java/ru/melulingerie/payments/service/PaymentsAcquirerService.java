package ru.melulingerie.payments.service;

import ru.melulingerie.payments.dto.acquirer.AcquirerPaymentId;
import ru.melulingerie.payments.dto.AcquirerPaymentCancelResponse;
import ru.melulingerie.payments.dto.AcquirerPaymentCreateResponse;
import ru.melulingerie.payments.dto.PaymentCreateRequest;

import java.util.UUID;

public interface PaymentsAcquirerService {

    AcquirerPaymentCreateResponse createPayment(PaymentCreateRequest request);

    AcquirerPaymentCancelResponse cancelPayment( UUID idempotenceKey, AcquirerPaymentId acquirerPaymentId);
}