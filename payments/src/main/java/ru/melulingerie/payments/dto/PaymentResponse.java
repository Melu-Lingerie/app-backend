package ru.melulingerie.payments.dto;

import lombok.*;
import ru.melulingerie.payments.domain.PaymentMethod;
import ru.melulingerie.payments.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String externalPaymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String description;
    private String returnUrl;
    private String confirmationUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
}