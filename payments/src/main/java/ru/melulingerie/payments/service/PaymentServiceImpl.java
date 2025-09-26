package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.melulingerie.payments.domain.Payment;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.dto.ExternalPaymentCancelResponse;
import ru.melulingerie.payments.dto.ExternalPaymentResponse;
import ru.melulingerie.payments.dto.PaymentCancelRequest;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.PaymentResponse;
import ru.melulingerie.payments.exception.PaymentCreationException;
import ru.melulingerie.payments.exception.PaymentInvalidStatusException;
import ru.melulingerie.payments.exception.PaymentNotFoundException;
import ru.melulingerie.payments.exception.PaymentOperationException;
import ru.melulingerie.payments.mapper.PaymentMapper;
import ru.melulingerie.payments.mapper.PaymentStateTransitionMapper;
import ru.melulingerie.payments.repository.PaymentRepository;
import ru.melulingerie.payments.config.properties.PaymentsApiProperties;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentStateService paymentStateService;
    private final PaymentsAcquirerService paymentsAcquirerService;
    private final PaymentsApiProperties paymentsApiProperties;
    private final PaymentMapper paymentMapper;
    private final PaymentStateTransitionMapper stateTransitionMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        log.info("Creating payment for order: {}, amount: {}, method: {}",
                request.getOrderId(), request.getAmount(), request.getPaymentMethod());

        String returnUrl = StringUtils.hasText(request.getReturnUrl())
            ? request.getReturnUrl()
            : paymentsApiProperties.api().returnUrl();

        PaymentCreateRequest requestWithIdempotence = PaymentCreateRequest.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .returnUrl(returnUrl)
                .confirmationUrl(request.getConfirmationUrl())
                .idempotenceKey(UUID.randomUUID())
                .build();

        Payment payment = paymentMapper.toEntity(requestWithIdempotence);
        payment = paymentRepository.save(payment);

        ExternalPaymentResponse externalResponse = paymentsAcquirerService.createExternalPayment(requestWithIdempotence);

        if (externalResponse.isFailed()) {
            try {
                paymentStateService.transitPaymentStatus(
                        stateTransitionMapper.createFailureRequest(
                                payment.getId(),
                                PaymentTransitionReason.EXTERNAL_PAYMENT_FAILED
                        )
                );
            } catch (Exception e) {
                log.error("Failed to update payment status after external failure for payment: {}", payment.getId(), e);
            }

            throw new PaymentCreationException(request.getOrderId(), externalResponse.errorMessage());
        }

        if (externalResponse.externalPaymentId() != null) {
            payment.setExternalPaymentId(externalResponse.externalPaymentId());
            payment.setConfirmationUrl(externalResponse.confirmationUrl());

            payment = paymentRepository.save(payment);

            if (externalResponse.status() != null && externalResponse.status() != payment.getStatus()) {
                paymentStateService.transitPaymentStatus(
                        stateTransitionMapper.createStatusUpdateRequest(
                                payment.getId(),
                                externalResponse.status(),
                                PaymentTransitionReason.EXTERNAL_PAYMENT_STATUS_UPDATED
                        )
                );
            }

            log.info("Successfully created payment with ID: {}, external ID: {}",
                    payment.getId(), externalResponse.externalPaymentId());
        } else {
            // ! handle this case externalResponse.externalPaymentId() == null
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByExternalId(String externalPaymentId) {
        Payment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new PaymentNotFoundException(externalPaymentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(request.paymentId()));

        if (!payment.canBeCancelled()) {
            throw new PaymentInvalidStatusException("cancel", payment.getStatus(),
                    PaymentStatus.PENDING, PaymentStatus.WAITING_FOR_CAPTURE);
        }

        String externalPaymentId = payment.getExternalPaymentId();
        ExternalPaymentCancelResponse cancelResponse =
                paymentsAcquirerService.cancelExternalPayment(externalPaymentId, request.idempotenceKey());

        if (cancelResponse.isFailed()) {
            log.error("Failed to cancel external payment: {}", cancelResponse.errorMessage());
            throw PaymentOperationException.cancellation(request.paymentId(), cancelResponse.errorMessage());
        }

        paymentStateService.transitPaymentStatus(
                stateTransitionMapper.fromCancelRequest(request)
        );

        log.info("Successfully canceled payment: {}", request.paymentId());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.canBeRefunded()) {
            throw new PaymentInvalidStatusException("refund", payment.getStatus(), PaymentStatus.SUCCEEDED);
        }

        try {
            paymentStateService.transitPaymentStatus(
                    stateTransitionMapper.createRefundRequest(
                            paymentId,
                            PaymentTransitionReason.REFUND_REQUESTED
                    )
            );

            log.info("Successfully refunded payment: {}", paymentId);
        } catch (Exception e) {
            log.error("Failed to refund payment: {}", paymentId, e);
            throw PaymentOperationException.refund(paymentId, e.getMessage(), e);
        }

        return paymentMapper.toResponse(payment);
    }
}