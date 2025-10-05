package ru.melulingerie.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.payments.api.AcquirerApi;
import ru.melulingerie.payments.dto.acquirer.AcquirerPaymentId;
import ru.melulingerie.payments.dto.AcquirerPaymentCancelResponse;
import ru.melulingerie.payments.dto.AcquirerPaymentCreateResponse;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.dto.acquirer.AcquirerApiPaymentResponse;
import ru.melulingerie.payments.mapper.AcquirerApiMapper;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsAcquirerServiceImpl implements PaymentsAcquirerService {
    private final AcquirerApi acquirerApi;
    private final AcquirerApiMapper acquirerApiMapper;

    @Override
    public AcquirerPaymentCreateResponse createPayment(PaymentCreateRequest request) {
        log.info("Creating payment for method: {}, order: {}", request.getPaymentMethod(), request.getOrderId());

        try {
            AcquirerApiPaymentResponse response = acquirerApi.createPayment(
                    request.getIdempotenceKey(),
                    acquirerApiMapper.toApiRequest(request)
            );

            if (Objects.isNull(response)) {
                return AcquirerPaymentCreateResponse.failure("No response from payment provider", "NO_RESPONSE");
            }

            AcquirerPaymentId acquirerPaymentId = AcquirerPaymentId.of(response.id());
            log.info("Successfully created payment with ID: {}", acquirerPaymentId);
            return acquirerApiMapper.toAcquirerPaymentCreateResponse(acquirerPaymentId, response);
        } catch (Exception e) {
            log.error("Failed to create payment for method: {}", request.getPaymentMethod(), e);
            return AcquirerPaymentCreateResponse.failure(e.getMessage(), "API_ERROR");
        }
    }

    @Override
    public AcquirerPaymentCancelResponse cancelPayment(UUID idempotenceKey, AcquirerPaymentId acquirerPaymentId) {
        try {
            acquirerApi.cancelPayment(idempotenceKey, acquirerPaymentId);
            log.info("Successfully canceled payment: {}", acquirerPaymentId);
            return AcquirerPaymentCancelResponse.success(acquirerPaymentId);
        } catch (Exception e) {
            log.error("Failed to cancel payment: {}", acquirerPaymentId, e);
            return AcquirerPaymentCancelResponse.failure(acquirerPaymentId, e.getMessage(), "API_ERROR");
        }
    }
}