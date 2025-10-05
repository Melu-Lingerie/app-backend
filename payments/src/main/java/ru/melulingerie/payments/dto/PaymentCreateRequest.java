package ru.melulingerie.payments.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.melulingerie.payments.domain.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1000000.00")
    @Digits(integer = 8, fraction = 2, message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String description;

    private String returnUrl;

    private String confirmationUrl;

    @NotNull(message = "Idempotence key is required")
    private UUID idempotenceKey;
}