package ru.melulingerie.payments.dto.acquirer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AcquirerAmount(
        @JsonProperty("value") String value,
        @JsonProperty("currency") String currency
) {
}