package ru.melulingerie.payments.dto;

import lombok.Builder;
import ru.melulingerie.payments.dto.acquirer.AcquirerPaymentId;
import ru.melulingerie.payments.domain.PaymentStatus;

import java.time.LocalDateTime;

@Builder
public record AcquirerPaymentCreateResponse(
        boolean isSuccess,
        AcquirerPaymentId acquirerPaymentId,
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

    public static AcquirerPaymentCreateResponse success(AcquirerPaymentId acquirerPaymentId, PaymentStatus status, String confirmationUrl) {
        return new AcquirerPaymentCreateResponse(
                true,
                acquirerPaymentId,
                status,
                confirmationUrl,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static AcquirerPaymentCreateResponse failure(String errorMessage, String errorCode) {
        return new AcquirerPaymentCreateResponse(
                false,
                null,
                null,
                null,
                errorMessage,
                errorCode,
                LocalDateTime.now()
        );
    }

    public static AcquirerPaymentCreateResponse failure(String errorMessage) {
        return failure(errorMessage, "ACQUIRER_ERROR");
    }
}