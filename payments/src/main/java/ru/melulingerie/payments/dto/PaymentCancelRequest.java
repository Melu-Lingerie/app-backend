package ru.melulingerie.payments.dto;

import jakarta.validation.constraints.NotNull;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.domain.TransitionActor;

import java.util.UUID;

public record PaymentCancelRequest(
        @NotNull(message = "Payment ID is required")
        Long paymentId,

        @NotNull(message = "Idempotence key is required")
        UUID idempotenceKey,

        PaymentTransitionReason transitionReason,

        @NotNull(message = "Transition actor is required")
        TransitionActor transitionActor
) {
    public PaymentCancelRequest(Long paymentId, UUID idempotenceKey) {
        this(paymentId, idempotenceKey, PaymentTransitionReason.SYSTEM_CANCELLED,
             TransitionActor.builder()
                     .actorType(ru.melulingerie.payments.domain.PaymentTransitionActor.SYSTEM)
                     .build());
    }

    public PaymentCancelRequest(Long paymentId, UUID idempotenceKey, PaymentTransitionReason transitionReason) {
        this(paymentId, idempotenceKey, transitionReason,
             TransitionActor.builder()
                     .actorType(ru.melulingerie.payments.domain.PaymentTransitionActor.SYSTEM)
                     .build());
    }
}