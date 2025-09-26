package ru.melulingerie.payments.service;

import ru.melulingerie.payments.domain.PaymentStateTransition;
import ru.melulingerie.payments.dto.PaymentStateTransitionRequest;

import java.util.List;

public interface PaymentStateService {

    void transitPaymentStatus(PaymentStateTransitionRequest request);

    List<PaymentStateTransition> getPaymentHistory(Long paymentId);
}