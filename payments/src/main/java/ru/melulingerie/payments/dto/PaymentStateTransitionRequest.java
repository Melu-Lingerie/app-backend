package ru.melulingerie.payments.dto;

import jakarta.validation.constraints.NotNull;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.domain.TransitionActor;

public record PaymentStateTransitionRequest(
        @NotNull(message = "Payment ID is required")
        Long paymentId,

        @NotNull(message = "New status is required")
        PaymentStatus newStatus,

        PaymentTransitionReason reason,

        @NotNull(message = "Transition actor is required")
        TransitionActor createdBy
) {
}