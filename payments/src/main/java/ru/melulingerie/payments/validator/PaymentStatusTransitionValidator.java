package ru.melulingerie.payments.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.exception.PaymentInvalidStatusException;

import java.util.Set;

@Component
@Slf4j
public class PaymentStatusTransitionValidator {

    public void validateTransition(PaymentStatus from, PaymentStatus to) {
        log.debug("Validating status transition from {} to {}", from, to);

        if (from == to) {
            log.debug("Status transition validation skipped: same status {} -> {}", from, to);
            return;
        }

        Set<PaymentStatus> allowedTransitions = getAllowedTransitions(from);

        if (!allowedTransitions.contains(to)) {
            log.error("Invalid status transition attempted: {} -> {}. Allowed transitions: {}",
                     from, to, allowedTransitions);
            throw new PaymentInvalidStatusException("transition", from, allowedTransitions.toArray(new PaymentStatus[0]));
        }

        log.debug("Status transition validation passed: {} -> {}", from, to);
    }

    private Set<PaymentStatus> getAllowedTransitions(PaymentStatus from) {
        return switch (from) {
            case PENDING -> Set.of(
                PaymentStatus.WAITING_FOR_CAPTURE,
                PaymentStatus.SUCCEEDED,
                PaymentStatus.CANCELED,
                PaymentStatus.FAILED,
                PaymentStatus.EXPIRED
            );

            case WAITING_FOR_CAPTURE -> Set.of(
                PaymentStatus.SUCCEEDED,
                PaymentStatus.CANCELED,
                PaymentStatus.EXPIRED
            );

            case SUCCEEDED -> Set.of(
                PaymentStatus.REFUNDED,
                PaymentStatus.PARTIALLY_REFUNDED
            );

            case PARTIALLY_REFUNDED -> Set.of(
                PaymentStatus.REFUNDED
            );

            case CANCELED, FAILED, EXPIRED, REFUNDED -> Set.of();
        };
    }

    public boolean isTerminalStatus(PaymentStatus status) {
        return switch (status) {
            case CANCELED, FAILED, EXPIRED, REFUNDED -> true;
            case PENDING, WAITING_FOR_CAPTURE, SUCCEEDED, PARTIALLY_REFUNDED -> false;
        };
    }

    public boolean canTransitionTo(PaymentStatus from, PaymentStatus to) {
        if (from == to) {
            return true;
        }
        return getAllowedTransitions(from).contains(to);
    }
}