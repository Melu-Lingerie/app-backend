package ru.melulingerie.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.melulingerie.payments.domain.Payment;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.PaymentResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "status", constant = "PENDING")
    Payment toEntity(PaymentCreateRequest request);

    @Mapping(target = "currency", constant = "RUB")
    PaymentResponse toResponse(Payment payment);
}