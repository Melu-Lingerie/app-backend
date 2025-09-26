package ru.melulingerie.facade.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransitionActorDto {
    private String actorId;
    private PaymentTransitionActorDto actorType;
}