package ru.melulingerie.payments.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

@ConfigurationProperties(prefix = "integration.payments")
public record PaymentsApiProperties(
    @NotNull @Valid ApiConfig api,
    @NotNull @Valid WebhookConfig webhook
) {
    public record ApiConfig(
        @NotBlank String storeId,
        @NotBlank String secretKey,
        @NotBlank String baseUrl,
        @NotBlank String returnUrl
    ) {}

    public record WebhookConfig(
        @NotBlank String secret
    ) {}
}