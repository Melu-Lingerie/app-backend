package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.payments.domain.Payment;
import ru.melulingerie.payments.domain.PaymentStateTransition;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.exception.PaymentInvalidStatusException;
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
    private final PaymentStateTransitionMapper stateTransitionMapper;
    private final PaymentStatusTransitionValidator transitionValidator;
    private final PaymentStateTransitionRepository stateTransitionRepository;

    @Override
    @Transactional(noRollbackFor = PaymentInvalidStatusException.class)
    public void transitPaymentStatus(PaymentStateTransitionRequest request) {
        PaymentStatus fromStatus = request.oldStatus();
        PaymentStatus toStatus = request.newStatus();

        transitionValidator.validateTransition(fromStatus, toStatus);

        PaymentStateTransition transition = stateTransitionMapper.createTransition(request);

        stateTransitionRepository.save(transition);

        log.info("Payment[paymentId = {}] transitioned from {} to {} with reason: {}",
                request.paymentId(), fromStatus, toStatus, request.reason());
    }

    @Override
    public List<PaymentStateTransition> getPaymentHistory(Long paymentId) {
        return stateTransitionRepository.findByPaymentIdOrderByCreatedAtAsc(paymentId);
    }
}