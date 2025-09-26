package ru.melulingerie.facade.payments.service;

import ru.melulingerie.facade.payments.dto.request.PaymentCancelFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.request.PaymentCreateFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.response.PaymentFacadeResponseDto;

public interface PaymentFacadeService {
    PaymentFacadeResponseDto createPayment(PaymentCreateFacadeRequestDto request);
    PaymentFacadeResponseDto getPaymentById(Long paymentId);
    PaymentFacadeResponseDto getPaymentByExternalId(String externalPaymentId);
    PaymentFacadeResponseDto cancelPayment(PaymentCancelFacadeRequestDto request);
    PaymentFacadeResponseDto refundPayment(Long paymentId, String reason);
}