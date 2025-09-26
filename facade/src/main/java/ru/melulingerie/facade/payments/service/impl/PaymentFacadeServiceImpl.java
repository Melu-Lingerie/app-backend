package ru.melulingerie.facade.payments.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.payments.dto.request.PaymentCancelFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.request.PaymentCreateFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.response.PaymentFacadeResponseDto;
import ru.melulingerie.facade.payments.mapper.PaymentFacadeMapper;
import ru.melulingerie.facade.payments.service.PaymentFacadeService;
import ru.melulingerie.payments.dto.PaymentCancelRequest;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.PaymentResponse;
import ru.melulingerie.payments.service.PaymentService;

@Service
@RequiredArgsConstructor
public class PaymentFacadeServiceImpl implements PaymentFacadeService {

    private final PaymentService paymentService;
    private final PaymentFacadeMapper paymentFacadeMapper;

    @Override
    public PaymentFacadeResponseDto createPayment(PaymentCreateFacadeRequestDto request) {
        PaymentCreateRequest paymentRequest = paymentFacadeMapper.toPaymentCreateRequest(request);
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return paymentFacadeMapper.toFacadeResponse(response);
    }

    @Override
    public PaymentFacadeResponseDto getPaymentById(Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return paymentFacadeMapper.toFacadeResponse(response);
    }

    @Override
    public PaymentFacadeResponseDto getPaymentByExternalId(String externalPaymentId) {
        PaymentResponse response = paymentService.getPaymentByExternalId(externalPaymentId);
        return paymentFacadeMapper.toFacadeResponse(response);
    }

    @Override
    public PaymentFacadeResponseDto cancelPayment(PaymentCancelFacadeRequestDto request) {
        PaymentCancelRequest cancelRequest = paymentFacadeMapper.toPaymentCancelRequest(request);
        PaymentResponse response = paymentService.cancelPayment(cancelRequest);
        return paymentFacadeMapper.toFacadeResponse(response);
    }

    @Override
    public PaymentFacadeResponseDto refundPayment(Long paymentId, String reason) {
        PaymentResponse response = paymentService.refundPayment(paymentId, reason);
        return paymentFacadeMapper.toFacadeResponse(response);
    }
}