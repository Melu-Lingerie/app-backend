package ru.melulingerie.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.acquirer.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AcquirerApiMapper {

    @Mapping(target = "amount", source = ".", qualifiedByName = "mapAmount")
    @Mapping(target = "paymentMethodData", source = ".", qualifiedByName = "mapPaymentMethodData")
    @Mapping(target = "confirmation", source = ".", qualifiedByName = "mapConfirmation")
    @Mapping(target = "capture", source = ".", qualifiedByName = "mapCapture")
    @Mapping(target = "description", source = "description")
    AcquirerCreatePaymentRequest toAcquirerRequest(PaymentCreateRequest request);

    @Named("mapAmount")
    default AcquirerAmount mapAmount(PaymentCreateRequest request) {
        return AcquirerAmount.builder()
                .value(request.getAmount().toString())
                .currency("RUB")
                .build();
    }

    @Named("mapPaymentMethodData")
    default AcquirerPaymentMethodData mapPaymentMethodData(PaymentCreateRequest request) {
        String type = switch (request.getPaymentMethod()) {
            case SBP -> "sbp";
            case CARD -> "bank_card";
        };

        return AcquirerPaymentMethodData.builder()
                .type(type)
                .build();
    }

    @Named("mapConfirmation")
    default AcquirerConfirmation mapConfirmation(PaymentCreateRequest request) {
        return AcquirerConfirmation.builder()
                .type("redirect")
                .returnUrl(request.getReturnUrl())
                .build();
    }

    @Named("mapCapture")
    default Boolean mapCapture(PaymentCreateRequest request) {
        return switch (request.getPaymentMethod()) {
            case SBP -> null;
            case CARD -> true;
        };
    }

    default PaymentStatus mapStatus(String yooKassaStatus) {
        return switch (yooKassaStatus) {
            case "pending" -> PaymentStatus.PENDING;
            case "waiting_for_capture" -> PaymentStatus.WAITING_FOR_CAPTURE;
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "canceled" -> PaymentStatus.CANCELED;
            default -> PaymentStatus.PENDING;
        };
    }

    default String extractConfirmationUrl(AcquirerApiPaymentResponse response) {
        if (response.confirmation() != null && "redirect".equals(response.confirmation().type())) {
            return response.confirmation().confirmationUrl();
        }
        return null;
    }
}