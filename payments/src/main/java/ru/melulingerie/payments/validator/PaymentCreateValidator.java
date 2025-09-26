package ru.melulingerie.payments.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.melulingerie.payments.dto.PaymentCreateRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentCreateValidator {
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1.00");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000.00");

    public void validate(PaymentCreateRequest request) {
        List<String> errors = new ArrayList<>();

        validateOrderId(request, errors);
        validateAmount(request, errors);
        validatePaymentMethod(request, errors);
        validateMethodSpecificRules(request, errors);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Payment validation failed: " + String.join(", ", errors));
        }
    }

    private void validateOrderId(PaymentCreateRequest request, List<String> errors) {
        if (request.getOrderId() == null) {
            errors.add("Order ID is required");
        }
    }

    private void validateAmount(PaymentCreateRequest request, List<String> errors) {
        if (request.getAmount() == null) {
            errors.add("Amount is required");
            return;
        }

        if (request.getAmount().compareTo(MIN_AMOUNT) < 0) {
            errors.add("Amount must be at least " + MIN_AMOUNT);
        }

        if (request.getAmount().compareTo(MAX_AMOUNT) > 0) {
            errors.add("Amount must not exceed " + MAX_AMOUNT);
        }

        if (request.getAmount().scale() > 2) {
            errors.add("Amount must have at most 2 decimal places");
        }
    }

    private void validatePaymentMethod(PaymentCreateRequest request, List<String> errors) {
        if (request.getPaymentMethod() == null) {
            errors.add("Payment method is required");
        }
    }

    private void validateMethodSpecificRules(PaymentCreateRequest request, List<String> errors) {
        if (request.getPaymentMethod() == null) {
            return;
        }

        switch (request.getPaymentMethod()) {
            case SBP -> validateSbpRules(request, errors);
            case CARD -> validateCardRules(request, errors);
        }
    }

    private void validateSbpRules(PaymentCreateRequest request, List<String> errors) {
        if (!StringUtils.hasText(request.getReturnUrl())) {
            errors.add("Return URL is required for SBP payments");
        }
    }

    private void validateCardRules(PaymentCreateRequest request, List<String> errors) {
        if (!StringUtils.hasText(request.getReturnUrl())) {
            errors.add("Return URL is required for card payments");
        }
    }
}