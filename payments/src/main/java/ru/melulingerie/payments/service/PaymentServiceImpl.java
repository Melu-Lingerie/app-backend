package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.payments.dto.acquirer.AcquirerPaymentId;
import ru.melulingerie.payments.domain.Payment;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.dto.AcquirerPaymentCancelResponse;
import ru.melulingerie.payments.dto.AcquirerPaymentCreateResponse;
import ru.melulingerie.payments.dto.PaymentCancelRequest;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.PaymentResponse;
import ru.melulingerie.payments.exception.*;
import ru.melulingerie.payments.mapper.PaymentMapper;
import ru.melulingerie.payments.mapper.PaymentStateTransitionMapper;
import ru.melulingerie.payments.repository.PaymentRepository;
import ru.melulingerie.payments.validator.PaymentCreateValidator;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentStateService paymentStateService;
    private final PaymentsAcquirerService paymentsAcquirerService;
    private final PaymentStateTransitionMapper stateTransitionMapper;
    private final PaymentCreateValidator paymentCreateValidator;

    @Override
    @Transactional(noRollbackFor = { PaymentCreationException.class, PaymentInvalidStatusException.class })
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        log.info("Creating payment for order: {}, amount: {}, method: {}",
                request.getOrderId(), request.getAmount(), request.getPaymentMethod());

        // TODO В Фасаде засетить returnURL, если управлять будем с бэка.
        paymentCreateValidator.validate(request);

        Payment payment = paymentRepository.save(
                paymentMapper.toEntity(request)
        );

        AcquirerPaymentCreateResponse acquirerResponse = paymentsAcquirerService.createPayment(request);

        handleFailedResponse(request, acquirerResponse, payment);

        if (Objects.nonNull(acquirerResponse.acquirerPaymentId())) {
            payment.setAcquirerPaymentId(acquirerResponse.acquirerPaymentId().toString());
            payment.setConfirmationUrl(acquirerResponse.confirmationUrl());

            if (Objects.nonNull(acquirerResponse.status()) && acquirerResponse.status() != payment.getStatus()) {
                paymentStateService.transitPaymentStatus(
                        stateTransitionMapper.createStatusUpdateRequest(
                                payment.getId(),
                                acquirerResponse.status(),
                                PaymentTransitionReason.ACQUIRER_PAYMENT_STATUS_UPDATED
                        )
                );
            } else {
                log.error("Acquirer respond with nullable status or status is equal to payment status: " +
                        "paymentStatus = {}, acquirerStatus = {}", payment.getStatus(), acquirerResponse.status());
                throw new PaymentCreationException(request.getOrderId(), "Acquirer respond with nullable status or status is equal to payment status");
            }

            log.info("Successfully created payment with ID: {}, acquirer ID: {}", payment.getId(), acquirerResponse.acquirerPaymentId());
        } else {
            throw new PaymentCreationException(request.getOrderId(), "No acquirer payment ID returned");
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
    public PaymentResponse getPaymentByAcquirerPaymentId(String acquirerPaymentId) {
        Payment payment = paymentRepository.findByAcquirerPaymentId(acquirerPaymentId)
                .orElseThrow(() -> new PaymentNotFoundException(acquirerPaymentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(request.paymentId()));

        /*
            TODO Логика в payment.canBeCancelled() дублирует логику в компоненте PaymentStatusTransitionMatrix.
            Необходимо подумать и определиться, как написать так, чтобы эта логика была в одном месте.
         */
        if (!payment.canBeCancelled()) {
            throw new PaymentInvalidStatusException(
                    PaymentInvalidStatusOperation.CANCEL,
                    payment.getStatus(),
                    PaymentStatus.PENDING,
                    PaymentStatus.WAITING_FOR_CAPTURE
            );
        }

        AcquirerPaymentCancelResponse cancelResponse = paymentsAcquirerService.cancelPayment(
                        request.idempotenceKey(),
                        AcquirerPaymentId.of(payment.getAcquirerPaymentId())
                );

        if (cancelResponse.isFailed()) {
            log.error("Failed to cancel acquirer payment: {}", cancelResponse.errorMessage());
            throw PaymentOperationException.cancellation(request.paymentId(), cancelResponse.errorMessage());
        }

        payment.setStatus(PaymentStatus.CANCELED);

        paymentStateService.transitPaymentStatus(
                stateTransitionMapper.fromCancelRequest(request)
        );

        log.info("Successfully canceled payment: {}", request.paymentId());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    // TODO подумаь о переименовании в requestRefundPayment и создании отдельного refundPayment метода, который будет запускать только админ.
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        /*
            TODO Логика в payment.canBeRefunded() дублирует логику в компоненте PaymentStatusTransitionMatrix.
            Необходимо подумать и определиться, как написать так, чтобы эта логика была в одном месте.
         */
        if (!payment.canBeRefunded()) {
            throw new PaymentInvalidStatusException(
                    PaymentInvalidStatusOperation.REFUND,
                    payment.getStatus(),
                    PaymentStatus.SUCCEEDED
            );
        }
        // TODO Отсутствует запрос на возврат денежных средств клиенту у эквайера.
        // Видимо в этом методе необходимо сделать запрос на возврат денежных средств, а одобрить или отказать - решает администратор магазина.
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

    private void handleFailedResponse(PaymentCreateRequest request, AcquirerPaymentCreateResponse acquirerResponse, Payment payment) {
        if (acquirerResponse.isFailed()) {
            log.error("Failed to create payment[paymentId = {}] due {}", payment.getId(), acquirerResponse.errorMessage());
            payment.setStatus(PaymentStatus.FAILED);
            try {
                paymentStateService.transitPaymentStatus(
                        stateTransitionMapper.createFailureRequest(
                                payment.getId(),
                                PaymentTransitionReason.ACQUIRER_PAYMENT_FAILED
                        )
                );
            } catch (Exception e) {
                log.error("Failed to update payment status after acquirer failure for paymentId = {}", payment.getId(), e);
            }

            throw new PaymentCreationException(request.getOrderId(), acquirerResponse.errorMessage());
        }
    }
}