package ru.melulingerie.auth.service;

public class OtpGenerator {
    private static final java.security.SecureRandom RND = new java.security.SecureRandom();
    private OtpGenerator() {}
    public static String sixDigits() {
        return String.format("%06d", RND.nextInt(1_000_000));
    }
}
