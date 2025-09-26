package ru.melulingerie.facade.payments.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.payments.dto.PaymentMethodDto;
import ru.melulingerie.facade.payments.dto.PaymentStatusDto;
import ru.melulingerie.facade.payments.dto.PaymentTransitionActorDto;
import ru.melulingerie.facade.payments.dto.PaymentTransitionReasonDto;
import ru.melulingerie.facade.payments.dto.TransitionActorDto;
import ru.melulingerie.facade.payments.dto.request.PaymentCancelFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.request.PaymentCreateFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.response.PaymentFacadeResponseDto;
import ru.melulingerie.payments.domain.PaymentMethod;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.domain.PaymentTransitionActor;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.domain.TransitionActor;
import ru.melulingerie.payments.dto.PaymentCancelRequest;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.PaymentResponse;

@Mapper(config = MapStructConfig.class)
public interface PaymentFacadeMapper {

    PaymentFacadeResponseDto toFacadeResponse(PaymentResponse paymentResponse);
    PaymentResponse fromFacadeResponse(PaymentFacadeResponseDto facadeResponse);

    PaymentCreateRequest toPaymentCreateRequest(PaymentCreateFacadeRequestDto facadeRequest);
    PaymentCreateFacadeRequestDto fromPaymentCreateRequest(PaymentCreateRequest request);

    PaymentCancelRequest toPaymentCancelRequest(PaymentCancelFacadeRequestDto facadeRequest);
    PaymentCancelFacadeRequestDto fromPaymentCancelRequest(PaymentCancelRequest request);

    PaymentMethodDto toPaymentMethodDto(PaymentMethod paymentMethod);
    PaymentMethod fromPaymentMethodDto(PaymentMethodDto paymentMethodDto);

    PaymentStatusDto toPaymentStatusDto(PaymentStatus paymentStatus);
    PaymentStatus fromPaymentStatusDto(PaymentStatusDto paymentStatusDto);

    PaymentTransitionReasonDto toPaymentTransitionReasonDto(PaymentTransitionReason reason);
    PaymentTransitionReason fromPaymentTransitionReasonDto(PaymentTransitionReasonDto reasonDto);

    PaymentTransitionActorDto toPaymentTransitionActorDto(PaymentTransitionActor actor);
    PaymentTransitionActor fromPaymentTransitionActorDto(PaymentTransitionActorDto actorDto);

    TransitionActorDto toTransitionActorDto(TransitionActor transitionActor);
    TransitionActor fromTransitionActorDto(TransitionActorDto transitionActorDto);
}