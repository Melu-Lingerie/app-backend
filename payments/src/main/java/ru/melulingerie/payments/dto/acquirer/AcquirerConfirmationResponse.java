package ru.melulingerie.payments.dto.acquirer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AcquirerConfirmationResponse(
        @JsonProperty("type") String type,
        @JsonProperty("confirmation_url") String confirmationUrl
) {
}