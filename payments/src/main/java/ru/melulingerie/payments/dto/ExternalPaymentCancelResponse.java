package ru.melulingerie.payments.dto;

import java.time.LocalDateTime;

public record ExternalPaymentCancelResponse(
        boolean isSuccess,
        String externalPaymentId,
        String errorMessage,
        String errorCode,
        LocalDateTime processedAt
) {

    public boolean isFailed() {
        return !isSuccess;
    }

    public static ExternalPaymentCancelResponse success(String externalPaymentId) {
        return new ExternalPaymentCancelResponse(
                true,
                externalPaymentId,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static ExternalPaymentCancelResponse failure(String externalPaymentId, String errorMessage, String errorCode) {
        return new ExternalPaymentCancelResponse(
                false,
                externalPaymentId,
                errorMessage,
                errorCode,
                LocalDateTime.now()
        );
    }

    public static ExternalPaymentCancelResponse failure(String externalPaymentId, String errorMessage) {
        return failure(externalPaymentId, errorMessage, "CANCEL_ERROR");
    }

    public static ExternalPaymentCancelResponse invalidRequest(String errorMessage) {
        return failure(null, errorMessage, "INVALID_REQUEST");
    }
}