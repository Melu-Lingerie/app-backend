package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.auth.entity.EmailVerification;
import ru.melulingerie.auth.repository.EmailVerificationRepository;
import ru.melulingerie.users.entity.User;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository repository;
    private final EmailSenderService emailSender;

    @Value("${app.verification.code-ttl-minutes:15}")
    private int codeTtlMinutes;

    @Value("${app.verification.max-attempts:3}")
    private int maxAttempts;

    @Value("${app.verification.resend-cooldown-minutes:1}")
    private int resendCooldownMinutes;

    @Transactional
    @SneakyThrows
    public void sendVerificationCode(String email, User user) {
        // Проверяем, не слишком ли часто запрашивается код
        checkResendCooldown(email);

        // Деактивируем старые коды для этого пользователя
        repository.deleteByUserId(user.getId());

        String code = OtpGenerator.sixDigits();

        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(codeTtlMinutes))
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(verification);

        log.info("Отправка кода верификации на email: {} для пользователя: {}", email, user.getId());

        emailSender.sendVerificationCode(email, code);
    }

    @Transactional
    public EmailVerification validateCode(String email, String code) {
        EmailVerification verification = repository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("Код верификации не найден"));

        if (verification.isExpired()) {
            log.warn("Попытка использования истекшего кода для email: {}", email);
            throw new IllegalStateException("Код верификации истек");
        }

        if (verification.getAttempts() >= maxAttempts) {
            log.warn("Превышено количество попыток для email: {}", email);
            throw new IllegalStateException("Превышено количество попыток ввода кода");
        }

        // Увеличиваем счетчик попыток
        verification.setAttempts(verification.getAttempts() + 1);
        repository.save(verification);

        if (!verification.getCode().equals(code)) {
            log.warn("Неверный код для email: {}, попытка: {}", email, verification.getAttempts());
            throw new IllegalArgumentException("Неверный код подтверждения");
        }

        log.info("Код успешно подтвержден для email: {}", email);
        return verification;
    }

    @Transactional
    public void cleanupExpiredCodes() {
        repository.deleteExpired(LocalDateTime.now());
    }

    private void checkResendCooldown(String email) {
        repository.findTopByEmailOrderByIdDesc(email)
                .ifPresent(lastVerification -> {
                    LocalDateTime cooldownEnd = lastVerification.getCreatedAt().plusMinutes(resendCooldownMinutes);
                    if (LocalDateTime.now().isBefore(cooldownEnd)) {
                        throw new IllegalStateException(
                                String.format("Повторная отправка кода возможна через %d минут", resendCooldownMinutes)
                        );
                    }
                });
    }
}
