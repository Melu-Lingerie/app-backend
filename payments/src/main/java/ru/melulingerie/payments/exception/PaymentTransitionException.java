package ru.melulingerie.payments.exception;

import ru.melulingerie.payments.domain.PaymentStatus;

import java.util.Set;

public class PaymentTransitionException extends RuntimeException {

    private final PaymentStatus fromStatus;
    private final PaymentStatus toStatus;
    private final Set<PaymentStatus> allowedStatuses;

    public PaymentTransitionException(PaymentStatus fromStatus, PaymentStatus toStatus, Set<PaymentStatus> allowedStatuses) {
        super(String.format("Invalid payment status transition from %s to %s. Allowed transitions: %s",
              fromStatus, toStatus, allowedStatuses));
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.allowedStatuses = allowedStatuses;
    }

    public PaymentStatus getFromStatus() {
        return fromStatus;
    }

    public PaymentStatus getToStatus() {
        return toStatus;
    }

    public Set<PaymentStatus> getAllowedStatuses() {
        return allowedStatuses;
    }
}