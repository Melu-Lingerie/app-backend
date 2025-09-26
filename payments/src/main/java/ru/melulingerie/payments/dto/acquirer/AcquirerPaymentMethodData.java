package ru.melulingerie.payments.dto.acquirer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AcquirerPaymentMethodData(
        @JsonProperty("type") String type
) {
}