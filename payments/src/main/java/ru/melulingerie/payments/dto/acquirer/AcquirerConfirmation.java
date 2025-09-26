package ru.melulingerie.payments.dto.acquirer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AcquirerConfirmation(
        @JsonProperty("type") String type,
        @JsonProperty("return_url") String returnUrl
) {
}