package ru.melulingerie.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.melulingerie.payments.domain.PaymentStatus;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.generated.model.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentsAcquirerMapper {

    @Mapping(target = "amount", source = ".", qualifiedByName = "mapMonetaryAmount")
    @Mapping(target = "confirmation", source = ".", qualifiedByName = "mapConfirmation")
    @Mapping(target = "paymentMethodData", source = ".", qualifiedByName = "mapPaymentMethodData")
    @Mapping(target = "capture", source = ".", qualifiedByName = "mapCapture")
    CreatePaymentRequest toRequest(PaymentCreateRequest request);

    @Named("mapMonetaryAmount")
    default MonetaryAmount mapMonetaryAmount(PaymentCreateRequest request) {
        return new MonetaryAmount()
                .currency(CurrencyCode.RUB)
                .value(request.getAmount().toString());
    }

    @Named("mapConfirmation")
    default CreatePaymentRequestConfirmation mapConfirmation(PaymentCreateRequest request) {
        return new CreatePaymentRequestConfirmation()
                .type(ConfirmationDataType.REDIRECT)
                .returnUrl(request.getReturnUrl());
    }

    @Named("mapPaymentMethodData")
    default CreatePaymentRequestPaymentMethodData mapPaymentMethodData(PaymentCreateRequest request) {
        CreatePaymentRequestPaymentMethodData.TypeEnum typeEnum = switch (request.getPaymentMethod()) {
            case SBP -> CreatePaymentRequestPaymentMethodData.TypeEnum.SBP;
            case CARD -> CreatePaymentRequestPaymentMethodData.TypeEnum.BANK_CARD;
        };

        return new CreatePaymentRequestPaymentMethodData()
                .type(typeEnum);
    }

    @Named("mapCapture")
    default Boolean mapCapture(PaymentCreateRequest request) {
        return switch (request.getPaymentMethod()) {
            case SBP -> null;
            case CARD -> true;
        };
    }

    default PaymentStatus mapStatus(ru.melulingerie.payments.generated.model.PaymentStatus acquirerStatus) {
        return switch (acquirerStatus) {
            case PENDING -> PaymentStatus.PENDING;
            case WAITING_FOR_CAPTURE -> PaymentStatus.WAITING_FOR_CAPTURE;
            case SUCCEEDED -> PaymentStatus.SUCCEEDED;
            case CANCELED -> PaymentStatus.CANCELED;
        };
    }

    default String extractConfirmationUrl(ru.melulingerie.payments.generated.model.Payment acquirerPayment) {
        PaymentConfirmation confirmation = acquirerPayment.getConfirmation();
        if (confirmation != null && confirmation.getType() == ConfirmationType.REDIRECT) {
            return confirmation.getConfirmationUrl();
        }

        return null;
    }
}