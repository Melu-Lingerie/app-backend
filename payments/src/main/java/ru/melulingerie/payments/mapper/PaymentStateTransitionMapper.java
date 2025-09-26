package ru.melulingerie.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.melulingerie.payments.domain.Payment;
import ru.melulingerie.payments.domain.PaymentStateTransition;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.domain.PaymentTransitionReason;
import ru.melulingerie.payments.domain.PaymentTransitionActor;
import ru.melulingerie.payments.domain.TransitionActor;
import ru.melulingerie.payments.dto.PaymentCancelRequest;
import ru.melulingerie.payments.dto.PaymentStateTransitionRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentStateTransitionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "fromStatus", source = "oldStatus")
    @Mapping(target = "toStatus", source = "newStatus")
    @Mapping(target = "transitionReason", source = "reason")
    @Mapping(target = "externalPaymentId", source = "payment.externalPaymentId")
    @Mapping(target = "createdBy", source = "createdBy")
    PaymentStateTransition createTransition(
            Payment payment,
            PaymentStatus oldStatus,
            PaymentStatus newStatus,
            PaymentTransitionReason reason,
            TransitionActor createdBy
    );

    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "newStatus", source = "newStatus")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "createdBy", source = "createdBy")
    PaymentStateTransitionRequest createRequest(
            Long paymentId,
            PaymentStatus newStatus,
            PaymentTransitionReason reason,
            TransitionActor createdBy
    );

    @Mapping(target = "paymentId", source = "cancelRequest.paymentId")
    @Mapping(target = "newStatus", constant = "CANCELED")
    @Mapping(target = "reason", source = "cancelRequest.transitionReason")
    @Mapping(target = "createdBy", source = "cancelRequest.transitionActor")
    PaymentStateTransitionRequest fromCancelRequest(PaymentCancelRequest cancelRequest);

    default PaymentStateTransitionRequest createFailureRequest(Long paymentId, PaymentTransitionReason reason) {
        return createRequest(
                paymentId,
                PaymentStatus.FAILED,
                reason,
                TransitionActor.builder()
                        .actorType(PaymentTransitionActor.PAYMENT_PROVIDER)
                        .build()
        );
    }

    default PaymentStateTransitionRequest createStatusUpdateRequest(Long paymentId, PaymentStatus newStatus, PaymentTransitionReason reason) {
        return createRequest(
                paymentId,
                newStatus,
                reason,
                TransitionActor.builder()
                        .actorType(PaymentTransitionActor.PAYMENT_PROVIDER)
                        .build()
        );
    }

    default PaymentStateTransitionRequest createRefundRequest(Long paymentId, PaymentTransitionReason reason) {
        return createRequest(
                paymentId,
                PaymentStatus.REFUNDED,
                reason,
                TransitionActor.builder()
                        .actorType(PaymentTransitionActor.ADMIN)
                        .build()
        );
    }
}