package ru.melulingerie.payments.dto.acquirer;

import ru.melulingerie.payments.exception.InvalidAcquirerPaymentIdException;

public record AcquirerPaymentId(String value) {

    public AcquirerPaymentId {
        if (value == null || value.isBlank()) {
            throw new InvalidAcquirerPaymentIdException("Acquirer payment ID cannot be null or blank");
        }
    }

    public static AcquirerPaymentId of(String value) {
        return new AcquirerPaymentId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}