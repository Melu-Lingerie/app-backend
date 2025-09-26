package ru.melulingerie.payments.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.melulingerie.payments.config.properties.PaymentsApiProperties;
import ru.melulingerie.payments.generated.api.PaymentsApi;
import ru.melulingerie.payments.generated.client.ApiClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PaymentsApiProperties.class)
public class PaymentsApiConfiguration {

    @Bean
    public ApiClient apiClient(PaymentsApiProperties properties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(properties.api().baseUrl());

        String basic = properties.api().storeId() + ":" + properties.api().secretKey();
        String auth = "Basic %s".formatted(Base64.getEncoder().encodeToString(basic.getBytes(StandardCharsets.UTF_8)));

        apiClient.addDefaultHeader("Authorization", auth);

        return apiClient;
    }

    @Bean
    public PaymentsApi paymentsApi(ApiClient apiClient) {
        return new PaymentsApi(apiClient);
    }
}