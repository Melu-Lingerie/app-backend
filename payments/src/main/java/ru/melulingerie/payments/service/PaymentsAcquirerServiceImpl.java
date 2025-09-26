package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.melulingerie.payments.dto.ExternalPaymentCancelResponse;
import ru.melulingerie.payments.dto.ExternalPaymentResponse;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.generated.api.PaymentsApi;
import ru.melulingerie.payments.generated.model.CreatePaymentRequest;
import ru.melulingerie.payments.generated.model.Payment;
import ru.melulingerie.payments.mapper.PaymentsAcquirerMapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsAcquirerServiceImpl implements PaymentsAcquirerService {
    private final PaymentsApi paymentsApi;
    private final PaymentsAcquirerMapper paymentsAcquirerMapper;

    @Override
    public ExternalPaymentResponse createExternalPayment(PaymentCreateRequest request) {
        log.info("Creating external payment for method: {}, order: {}",
                request.getPaymentMethod(), request.getOrderId());

        try {
            CreatePaymentRequest apiRequest = paymentsAcquirerMapper.toRequest(request);

            ResponseEntity<Payment> response =
                    paymentsApi.paymentsPostWithHttpInfo(
                            request.getIdempotenceKey().toString(),
                            apiRequest
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return ExternalPaymentResponse.failure("Payment provider returned error: " + response.getStatusCode(), "HTTP_ERROR");
            }

            if (response.hasBody()) {
                Payment externalPayment = response.getBody();
                String externalId = externalPayment.getId();
                var status = paymentsAcquirerMapper.mapStatus(externalPayment.getStatus());
                String confirmationUrl = paymentsAcquirerMapper.extractConfirmationUrl(externalPayment);

                log.info("Successfully created external payment with ID: {}", externalId);

                return ExternalPaymentResponse.success(externalId, status, confirmationUrl);
            }

            return ExternalPaymentResponse.failure("No response from payment provider", "NO_RESPONSE");

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error creating external payment: {}", e.getStatusCode());
            return ExternalPaymentResponse.failure("Payment provider error", "HTTP_ERROR");
        } catch (Exception e) {
            log.error("Failed to create external payment for method: {}", request.getPaymentMethod(), e);
            return ExternalPaymentResponse.failure(e.getMessage(), "API_ERROR");
        }
    }

    @Override
    public ExternalPaymentCancelResponse cancelExternalPayment(String externalPaymentId, UUID idempotenceKey) {
        if (externalPaymentId == null) {
            return ExternalPaymentCancelResponse.invalidRequest("External payment ID is null");
        }

        try {
            paymentsApi.paymentsPaymentIdCancelPost(
                    externalPaymentId,
                    idempotenceKey.toString()
            );
            log.info("Successfully canceled external payment: {}", externalPaymentId);
            return ExternalPaymentCancelResponse.success(externalPaymentId);
        } catch (Exception e) {
            log.error("Failed to cancel external payment: {}", externalPaymentId, e);
            return ExternalPaymentCancelResponse.failure(externalPaymentId, e.getMessage(), "API_ERROR");
        }
    }
}