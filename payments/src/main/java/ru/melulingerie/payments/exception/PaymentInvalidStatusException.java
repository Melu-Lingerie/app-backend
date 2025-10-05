package ru.melulingerie.payments.exception;

import ru.melulingerie.payments.domain.PaymentStatus;

public class PaymentInvalidStatusException extends PaymentException {

    public PaymentInvalidStatusException(String operation, PaymentStatus currentStatus) {
        super(PaymentErrorCode.PAYMENT_INVALID_STATUS,
              String.format("Cannot %s payment in status: %s", operation, currentStatus));
    }

    public PaymentInvalidStatusException(PaymentInvalidStatusOperation operation, PaymentStatus currentStatus, PaymentStatus... requiredStatuses) {
        super(PaymentErrorCode.PAYMENT_INVALID_STATUS,
              String.format("Cannot %s payment in status %s. Required status: %s",
                          operation, currentStatus, java.util.Arrays.toString(requiredStatuses)));
    }
}