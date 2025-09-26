package ru.melulingerie.facade.payments.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.melulingerie.facade.payments.dto.PaymentTransitionReasonDto;
import ru.melulingerie.facade.payments.dto.TransitionActorDto;

import java.util.UUID;

public record PaymentCancelFacadeRequestDto(
        @NotNull(message = "Payment ID is required")
        Long paymentId,

        @NotNull(message = "Idempotence key is required")
        UUID idempotenceKey,

        PaymentTransitionReasonDto transitionReason,

        @NotNull(message = "Transition actor is required")
        TransitionActorDto transitionActor
) {
    public PaymentCancelFacadeRequestDto(Long paymentId, UUID idempotenceKey) {
        this(paymentId, idempotenceKey, PaymentTransitionReasonDto.SYSTEM_CANCELLED,
             TransitionActorDto.builder()
                     .actorType(ru.melulingerie.facade.payments.dto.PaymentTransitionActorDto.SYSTEM)
                     .build());
    }

    public PaymentCancelFacadeRequestDto(Long paymentId, UUID idempotenceKey, PaymentTransitionReasonDto transitionReason) {
        this(paymentId, idempotenceKey, transitionReason,
             TransitionActorDto.builder()
                     .actorType(ru.melulingerie.facade.payments.dto.PaymentTransitionActorDto.SYSTEM)
                     .build());
    }
}