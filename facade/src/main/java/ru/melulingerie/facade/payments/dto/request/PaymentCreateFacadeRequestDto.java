package ru.melulingerie.facade.payments.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.melulingerie.facade.payments.dto.PaymentMethodDto;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateFacadeRequestDto {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethodDto paymentMethod;

    private String description;

    private String returnUrl;

    private String confirmationUrl;

    @NotNull(message = "Idempotence key is required")
    private UUID idempotenceKey;
}