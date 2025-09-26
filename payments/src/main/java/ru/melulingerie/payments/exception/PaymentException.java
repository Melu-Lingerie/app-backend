package ru.melulingerie.payments.exception;

import lombok.Getter;

@Getter
public abstract class PaymentException extends RuntimeException {
    private final PaymentErrorCode errorCode;
    private final String userMessage;

    protected PaymentException(PaymentErrorCode errorCode, String userMessage, Throwable cause) {
        super(String.format("[%s] %s", errorCode.getCode(), userMessage), cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    protected PaymentException(PaymentErrorCode errorCode, String userMessage) {
        this(errorCode, userMessage, null);
    }

    protected PaymentException(PaymentErrorCode errorCode, Throwable cause) {
        this(errorCode, errorCode.getDefaultMessage(), cause);
    }

    protected PaymentException(PaymentErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage(), null);
    }
}