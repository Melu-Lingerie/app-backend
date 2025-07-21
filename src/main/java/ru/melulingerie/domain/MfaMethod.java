package ru.melulingerie.domain;

public enum MfaMethod {
    TOTP("totp"),
    SMS("sms"),
    EMAIL("email");

    private final String value;

    MfaMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}