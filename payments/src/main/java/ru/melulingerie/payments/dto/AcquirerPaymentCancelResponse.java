package ru.melulingerie.payments.dto;

import ru.melulingerie.payments.dto.acquirer.AcquirerPaymentId;

import java.time.LocalDateTime;

public record AcquirerPaymentCancelResponse(
        boolean isSuccess,
        AcquirerPaymentId acquirerPaymentId,
        String errorMessage,
        String errorCode,
        LocalDateTime processedAt
) {

    public boolean isFailed() {
        return !isSuccess;
    }

    public static AcquirerPaymentCancelResponse success(AcquirerPaymentId acquirerPaymentId) {
        return new AcquirerPaymentCancelResponse(
                true,
                acquirerPaymentId,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static AcquirerPaymentCancelResponse failure(AcquirerPaymentId acquirerPaymentId, String errorMessage, String errorCode) {
        return new AcquirerPaymentCancelResponse(
                false,
                acquirerPaymentId,
                errorMessage,
                errorCode,
                LocalDateTime.now()
        );
    }

    public static AcquirerPaymentCancelResponse failure(AcquirerPaymentId acquirerPaymentId, String errorMessage) {
        return failure(acquirerPaymentId, errorMessage, "CANCEL_ERROR");
    }

    public static AcquirerPaymentCancelResponse invalidRequest(String errorMessage) {
        return failure(null, errorMessage, "INVALID_REQUEST");
    }
}