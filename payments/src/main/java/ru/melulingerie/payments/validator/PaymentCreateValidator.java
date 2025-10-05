package ru.melulingerie.payments.validator;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import ru.melulingerie.payments.domain.PaymentMethod;
import ru.melulingerie.payments.dto.PaymentCreateRequest;
import ru.melulingerie.payments.exception.PaymentValidationException;

/**
 * Validates payment-method-specific business rules that cannot be expressed with Bean Validation annotations.
 * Basic field validation (nulls, ranges, formats) is handled by annotations on PaymentCreateRequest.
 */
@Validated
@Component
@RequiredArgsConstructor
public class PaymentCreateValidator {

    public void validate(@NotNull PaymentCreateRequest request) {
        validateReturnUrlRequired(request);
    }

    private void validateReturnUrlRequired(PaymentCreateRequest request) {
        if (requiresReturnUrl(request.getPaymentMethod()) && !StringUtils.hasText(request.getReturnUrl())) {
            throw new PaymentValidationException("Return URL is required for " + request.getPaymentMethod() + " payments");
        }
    }

    private boolean requiresReturnUrl(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.SBP || paymentMethod == PaymentMethod.CARD;
    }
}