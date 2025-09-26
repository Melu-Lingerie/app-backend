package ru.melulingerie.payments.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import ru.melulingerie.payments.config.properties.PaymentsApiProperties;

import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class YooKassaRestClientConfiguration {
    private final PaymentsApiProperties paymentsApiProperties;

    @Bean
    public RestClient yooKassaRestClient() {
        String credentials = paymentsApiProperties.api().storeId() + ":" + paymentsApiProperties.api().secretKey();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        return RestClient.builder()
                .baseUrl("https://api.yookassa.ru/v3")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}