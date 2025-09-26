package ru.melulingerie.payments.exception;

public class PaymentOperationException extends PaymentException {

    public PaymentOperationException(PaymentErrorCode errorCode, Long paymentId, String operation, String reason, Throwable cause) {
        super(errorCode,
              String.format("Failed to %s payment %d: %s", operation, paymentId, reason),
              cause);
    }

    public PaymentOperationException(PaymentErrorCode errorCode, Long paymentId, String operation, String reason) {
        super(errorCode,
              String.format("Failed to %s payment %d: %s", operation, paymentId, reason));
    }

    public static PaymentOperationException cancellation(Long paymentId, String reason, Throwable cause) {
        return new PaymentOperationException(PaymentErrorCode.PAYMENT_CANCELLATION_FAILED,
                                           paymentId, "cancel", reason, cause);
    }

    public static PaymentOperationException cancellation(Long paymentId, String reason) {
        return new PaymentOperationException(PaymentErrorCode.PAYMENT_CANCELLATION_FAILED,
                                           paymentId, "cancel", reason);
    }

    public static PaymentOperationException refund(Long paymentId, String reason, Throwable cause) {
        return new PaymentOperationException(PaymentErrorCode.PAYMENT_REFUND_FAILED,
                                           paymentId, "refund", reason, cause);
    }

    public static PaymentOperationException refund(Long paymentId, String reason) {
        return new PaymentOperationException(PaymentErrorCode.PAYMENT_REFUND_FAILED,
                                           paymentId, "refund", reason);
    }
}