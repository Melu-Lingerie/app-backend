package ru.melulingerie.payments.validator;

import org.springframework.stereotype.Component;
import ru.melulingerie.payments.domain.PaymentStatus;

import java.util.Map;
import java.util.Set;

@Component
public class PaymentStatusTransitionMatrix {
    private final Map<PaymentStatus, Set<PaymentStatus>> transitionMatrix;

    public PaymentStatusTransitionMatrix() {
        this.transitionMatrix = Map.of(
                PaymentStatus.PENDING, Set.of(
                        PaymentStatus.WAITING_FOR_CAPTURE,
                        PaymentStatus.SUCCEEDED,
                        PaymentStatus.CANCELED,
                        PaymentStatus.FAILED,
                        PaymentStatus.EXPIRED
                ),
                PaymentStatus.WAITING_FOR_CAPTURE, Set.of(
                        PaymentStatus.SUCCEEDED,
                        PaymentStatus.CANCELED,
                        PaymentStatus.FAILED,
                        PaymentStatus.EXPIRED
                ),
                PaymentStatus.SUCCEEDED, Set.of(
                        PaymentStatus.REFUNDED,
                        PaymentStatus.PARTIALLY_REFUNDED
                ),
                PaymentStatus.PARTIALLY_REFUNDED, Set.of(
                        PaymentStatus.REFUNDED
                ),
                PaymentStatus.CANCELED, Set.of(),
                PaymentStatus.FAILED, Set.of(),
                PaymentStatus.EXPIRED, Set.of(),
                PaymentStatus.REFUNDED, Set.of()
        );
    }

    public Set<PaymentStatus> getAllowedTransitions(PaymentStatus from) {
        return transitionMatrix.getOrDefault(from, Set.of());
    }

    public boolean isTransitionAllowed(PaymentStatus from, PaymentStatus to) {
        return getAllowedTransitions(from).contains(to);
    }

    public boolean isTerminalStatus(PaymentStatus status) {
        return getAllowedTransitions(status).isEmpty();
    }
}