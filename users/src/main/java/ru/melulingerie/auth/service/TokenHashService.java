package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Сервис для хэширования и проверки refresh токенов
 * Обеспечивает безопасное хранение токенов в БД
 * 
 * Использует двойное хеширование:
 * 1. SHA-256 для сокращения длины токена до 44 символов (обход ограничения BCrypt в 72 байта)
 * 2. BCrypt для безопасного хранения с солью
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenHashService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Хэширует токен для безопасного хранения в БД
     * 
     * @param token исходный токен
     * @return хэш токена
     */
    public String hashToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Токен не может быть пустым");
        }
        
        // Предварительное хеширование SHA-256 для обхода ограничения BCrypt в 72 байта
        String preparedToken = prepareTokenForBCrypt(token);
        String hash = passwordEncoder.encode(preparedToken);
        log.debug("Токен успешно хэширован");
        return hash;
    }

    /**
     * Проверяет соответствие токена его хэшу
     * 
     * @param token исходный токен
     * @param hashedToken хэш токена из БД
     * @return true если токен соответствует хэшу
     */
    public boolean matches(String token, String hashedToken) {
        if (token == null || hashedToken == null) {
            return false;
        }
        
        // Предварительное хеширование SHA-256 перед проверкой BCrypt
        String preparedToken = prepareTokenForBCrypt(token);
        boolean matches = passwordEncoder.matches(preparedToken, hashedToken);
        log.debug("Проверка токена: {}", matches ? "успешна" : "неуспешна");
        return matches;
    }

    /**
     * Предварительное хеширование токена с помощью SHA-256
     * для обхода ограничения BCrypt в 72 байта
     * 
     * SHA-256 создает 32-байтный хеш, который в Base64 занимает 44 символа
     * 
     * @param token исходный токен любой длины
     * @return Base64-кодированный SHA-256 хеш (44 символа)
     */
    private String prepareTokenForBCrypt(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
