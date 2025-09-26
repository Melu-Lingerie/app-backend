package ru.melulingerie.facade.payments.dto.response;

import lombok.*;
import ru.melulingerie.facade.payments.dto.PaymentMethodDto;
import ru.melulingerie.facade.payments.dto.PaymentStatusDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFacadeResponseDto {

    private Long id;
    private String externalPaymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethodDto paymentMethod;
    private PaymentStatusDto status;
    private String description;
    private String returnUrl;
    private String confirmationUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
}