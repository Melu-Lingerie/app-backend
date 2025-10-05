package ru.melulingerie.payments.dto;

import jakarta.validation.constraints.NotNull;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.domain.TransitionActor;

import java.time.LocalDateTime;

public record PaymentStateTransitionRequest(
        @NotNull(message = "Payment ID is required")
        Long paymentId,

        String acquirerPaymentId,

        LocalDateTime createdAt,

        @NotNull(message = "Old status is required")
        PaymentStatus oldStatus,

        @NotNull(message = "New status is required")
        PaymentStatus newStatus,

        PaymentTransitionReason reason,

        @NotNull(message = "Transition actor is required")
        TransitionActor createdBy
) {
}