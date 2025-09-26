package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.payments.domain.Payment;
import ru.melulingerie.payments.domain.PaymentStateTransition;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.exception.PaymentNotFoundException;
import ru.melulingerie.payments.repository.PaymentRepository;
import ru.melulingerie.payments.repository.PaymentStateTransitionRepository;
import ru.melulingerie.payments.validator.PaymentStatusTransitionValidator;
import ru.melulingerie.payments.mapper.PaymentStateTransitionMapper;
import ru.melulingerie.payments.dto.PaymentStateTransitionRequest;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStateServiceImpl implements PaymentStateService {
    private final PaymentRepository paymentRepository;
    private final PaymentStateTransitionRepository stateTransitionRepository;
    private final PaymentStatusTransitionValidator transitionValidator;
    private final PaymentStateTransitionMapper stateTransitionMapper;

    @Override
    @Transactional
    public void transitPaymentStatus(PaymentStateTransitionRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(request.paymentId()));

        PaymentStatus oldStatus = payment.getStatus();

        if (oldStatus == request.newStatus()) {
            log.debug("Payment {} already in status {}", request.paymentId(), request.newStatus());
            return;
        }

        transitionValidator.validateTransition(oldStatus, request.newStatus());

        PaymentStateTransition transition = stateTransitionMapper.createTransition(
                payment, oldStatus, request.newStatus(), request.reason(), request.createdBy());

        payment.setStatus(request.newStatus());

        paymentRepository.save(payment);
        stateTransitionRepository.save(transition);

        log.info("Payment {} transitioned from {} to {} with reason: {}",
                request.paymentId(), oldStatus, request.newStatus(), request.reason());
    }

    @Override
    public List<PaymentStateTransition> getPaymentHistory(Long paymentId) {
        return stateTransitionRepository.findByPaymentIdOrderByCreatedAtAsc(paymentId);
    }
}