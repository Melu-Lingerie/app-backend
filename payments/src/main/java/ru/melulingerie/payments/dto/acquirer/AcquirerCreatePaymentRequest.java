package ru.melulingerie.payments.dto.acquirer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AcquirerCreatePaymentRequest(
        @JsonProperty("amount") AcquirerAmount amount,
        @JsonProperty("payment_method_data") AcquirerPaymentMethodData paymentMethodData,
        @JsonProperty("confirmation") AcquirerConfirmation confirmation,
        @JsonProperty("capture") Boolean capture,
        @JsonProperty("description") String description
) {
}