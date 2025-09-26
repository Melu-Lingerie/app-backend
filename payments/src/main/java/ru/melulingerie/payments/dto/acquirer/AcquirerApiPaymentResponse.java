package ru.melulingerie.payments.dto.acquirer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AcquirerApiPaymentResponse(
        @JsonProperty("id") String id,
        @JsonProperty("status") String status,
        @JsonProperty("amount") AcquirerAmount amount,
        @JsonProperty("confirmation") AcquirerConfirmationResponse confirmation,
        @JsonProperty("description") String description
) {
}