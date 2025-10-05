package ru.melulingerie.payments.validator;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.exception.PaymentInvalidStatusException;
import ru.melulingerie.payments.exception.PaymentInvalidStatusOperation;

import java.util.Set;

/**
 * Validates payment status transitions according to business rules.
 * Ensures all transitions are allowed per the transition matrix and provides clear validation error messages.
 * Acts as the single validation entry point for status transitions.
 */
@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class PaymentStatusTransitionValidator {
    private final PaymentStatusTransitionMatrix transitionMatrix;

    public void validateTransition(@NotNull PaymentStatus from, @NotNull PaymentStatus to) {
        log.debug("Validating status transition from {} to {}", from, to);

        Set<PaymentStatus> allowedTransitions = transitionMatrix.getAllowedTransitions(from);

        if (!allowedTransitions.contains(to)) {
            log.error("Invalid status transition attempted: {} -> {}. Allowed transitions: {}", from, to, allowedTransitions);
            throw new PaymentInvalidStatusException(PaymentInvalidStatusOperation.TRANSITION, from, allowedTransitions.toArray(new PaymentStatus[0]));
        }

        log.debug("Status transition validation passed: {} -> {}", from, to);
    }

    public boolean isTerminalStatus(@NotNull PaymentStatus status) {
        return transitionMatrix.isTerminalStatus(status);
    }

    public boolean canTransitionTo(@NotNull PaymentStatus from, @NotNull PaymentStatus to) {
        return transitionMatrix.isTransitionAllowed(from, to);
    }
}