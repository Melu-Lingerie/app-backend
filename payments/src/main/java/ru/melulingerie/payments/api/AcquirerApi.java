package ru.melulingerie.payments.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.melulingerie.payments.domain.AcquirerPaymentId;
import ru.melulingerie.payments.dto.acquirer.AcquirerCreatePaymentRequest;
import ru.melulingerie.payments.dto.acquirer.AcquirerApiPaymentResponse;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcquirerApi {
    private final RestClient yooKassaRestClient;

    public AcquirerApiPaymentResponse createPayment(AcquirerCreatePaymentRequest request, UUID idempotenceKey) {
        log.debug("Creating payment via acquirer API with idempotence key: {}", idempotenceKey);

        return yooKassaRestClient
                .post()
                .uri("/payments")
                .header("Idempotence-Key", idempotenceKey.toString())
                .body(request)
                .retrieve()
                .body(AcquirerApiPaymentResponse.class);
    }

    public void cancelPayment(AcquirerPaymentId acquirerPaymentId, UUID idempotenceKey) {
        log.debug("Canceling payment {} via acquirer API with idempotence key: {}", acquirerPaymentId, idempotenceKey);

        yooKassaRestClient
                .post()
                .uri("/payments/{paymentId}/cancel", acquirerPaymentId.value())
                .header("Idempotence-Key", idempotenceKey.toString())
                .retrieve()
                .toBodilessEntity();
    }

    public AcquirerApiPaymentResponse getPayment(AcquirerPaymentId acquirerPaymentId) {
        log.debug("Getting payment {} via acquirer API", acquirerPaymentId);

        return yooKassaRestClient
                .get()
                .uri("/payments/{paymentId}", acquirerPaymentId.value())
                .retrieve()
                .body(AcquirerApiPaymentResponse.class);
    }
}