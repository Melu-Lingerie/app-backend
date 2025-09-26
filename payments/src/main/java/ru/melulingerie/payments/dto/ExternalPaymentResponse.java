package ru.melulingerie.payments.dto;

import ru.melulingerie.payments.domain.PaymentStatus;

import java.time.LocalDateTime;

public record ExternalPaymentResponse(
        boolean isSuccess,
        String externalPaymentId,
        PaymentStatus status,
        String confirmationUrl,
        String errorMessage,
        String errorCode,
        LocalDateTime processedAt
) {

    public boolean isFailed() {
        return !isSuccess;
    }

    public boolean hasConfirmationUrl() {
        return confirmationUrl != null && !confirmationUrl.isBlank();
    }

    public static ExternalPaymentResponse success(String externalPaymentId, PaymentStatus status, String confirmationUrl) {
        return new ExternalPaymentResponse(
                true,
                externalPaymentId,
                status,
                confirmationUrl,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static ExternalPaymentResponse failure(String errorMessage, String errorCode) {
        return new ExternalPaymentResponse(
                false,
                null,
                null,
                null,
                errorMessage,
                errorCode,
                LocalDateTime.now()
        );
    }

    public static ExternalPaymentResponse failure(String errorMessage) {
        return failure(errorMessage, "EXTERNAL_ERROR");
    }
}