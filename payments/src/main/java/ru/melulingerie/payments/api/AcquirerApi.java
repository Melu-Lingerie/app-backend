package ru.melulingerie.payments.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import ru.melulingerie.payments.dto.acquirer.AcquirerPaymentId;
import ru.melulingerie.payments.dto.acquirer.AcquirerCreatePaymentRequest;
import ru.melulingerie.payments.dto.acquirer.AcquirerApiPaymentResponse;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcquirerApi {
    private final RestClient yooKassaRestClient;

    public AcquirerApiPaymentResponse createPayment(UUID idempotenceKey, AcquirerCreatePaymentRequest request) {
        log.debug("Creating payment via acquirer API with idempotence key: {}", idempotenceKey);

        try {
            ResponseEntity<AcquirerApiPaymentResponse> responseEntity = yooKassaRestClient
                    .post()
                    .uri("/payments")
                    .header("Idempotence-Key", idempotenceKey.toString())
                    .body(request)
                    .retrieve()
                    .toEntity(AcquirerApiPaymentResponse.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                log.warn("Non-successful response from acquirer API: {}", responseEntity.getStatusCode());
                return null;
            }

            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error creating payment via acquirer API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating payment via acquirer API", e);
            throw e;
        }
    }

    public void cancelPayment(UUID idempotenceKey, AcquirerPaymentId acquirerPaymentId) {
        log.debug("Canceling payment {} via acquirer API with idempotence key: {}", acquirerPaymentId, idempotenceKey);

        try {
            ResponseEntity<Void> responseEntity = yooKassaRestClient
                    .post()
                    .uri("/payments/{paymentId}/cancel", acquirerPaymentId.value())
                    .header("Idempotence-Key", idempotenceKey.toString())
                    .retrieve()
                    .toBodilessEntity();

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                log.warn("Non-successful response from acquirer API for cancel: {}", responseEntity.getStatusCode());
                throw new RuntimeException("Cancel operation failed with status: " + responseEntity.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error canceling payment {} via acquirer API: {} - {}", acquirerPaymentId, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error canceling payment {} via acquirer API", acquirerPaymentId, e);
            throw e;
        }
    }

    public AcquirerApiPaymentResponse getPayment(AcquirerPaymentId acquirerPaymentId) {
        log.debug("Getting payment {} via acquirer API", acquirerPaymentId);

        try {
            ResponseEntity<AcquirerApiPaymentResponse> responseEntity = yooKassaRestClient
                    .get()
                    .uri("/payments/{paymentId}", acquirerPaymentId.value())
                    .retrieve()
                    .toEntity(AcquirerApiPaymentResponse.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                log.warn("Non-successful response from acquirer API for get: {}", responseEntity.getStatusCode());
                return null;
            }

            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error getting payment {} via acquirer API: {} - {}", acquirerPaymentId, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting payment {} via acquirer API", acquirerPaymentId, e);
            throw e;
        }
    }
}