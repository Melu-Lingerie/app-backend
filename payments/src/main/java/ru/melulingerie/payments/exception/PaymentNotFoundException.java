package ru.melulingerie.payments.exception;

public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(Long paymentId) {
        super(PaymentErrorCode.PAYMENT_NOT_FOUND,
              String.format("Payment with ID %d not found", paymentId));
    }

    public PaymentNotFoundException(String externalPaymentId) {
        super(PaymentErrorCode.PAYMENT_NOT_FOUND,
              String.format("Payment with external ID %s not found", externalPaymentId));
    }
}