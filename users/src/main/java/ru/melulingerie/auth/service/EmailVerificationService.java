package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.auth.entity.VerificationCode;
import ru.melulingerie.auth.entity.VerificationCode.VerificationStatus;
import ru.melulingerie.auth.repository.EmailVerificationRepository;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;

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
    
    @Value("${app.verification.tolerance-minutes:10}")
    private int toleranceMinutes;

    @Transactional
    @SneakyThrows
    public void sendVerificationCode(String email, UserCredentials userCredentials) {
        // Проверяем, не слишком ли часто запрашивается код
        checkResendCooldown(email);
        
        // Отмечаем старые активные коды как замененные (вместо удаления)
        Long userId = userCredentials.getUser().getId();
        repository.updateStatusByUserIdAndCurrentStatus(userId, VerificationStatus.ACTIVE, VerificationStatus.SUPERSEDED);
        log.info("Отмечены старые активные коды как SUPERSEDED для пользователя: {}", userId);

        String code = OtpGenerator.sixDigits();

        VerificationCode verification = VerificationCode.builder()
                .userCredentials(userCredentials)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(codeTtlMinutes))
                // TODO ТЕХ.ДОЛГ
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(verification);

        log.info("Отправка кода верификации на email: {} для пользователя: {}", email, userId);

        emailSender.sendVerificationCode(email, code);
    }

    /**
     * Гибридная валидация кода: простота + tolerance + понятные ошибки
     */
    @Transactional
    public VerificationCode validateCode(String email, String code) {
        log.info("Валидация кода для email: {}", email);
        
        // 1. Ищем последний код (самый приоритетный)
        VerificationCode latestCode = repository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("Код не найден"));
        
        // 2. Пробуем последний код
        if (isValidCode(latestCode, code)) {
            return markAsUsedAndReturn(latestCode);
        }
        
        // 3. Если не подошел, ищем среди недавних SUPERSEDED (tolerance)
        VerificationCode tolerantCode = findToleranceCode(email, code);
        if (tolerantCode != null) {
            log.warn("Пользователь использует старый код для email: {}", email);
            return markAsUsedAndReturn(tolerantCode);
        }
        
        // 4. Код не найден - показываем понятную ошибку
        throw new IllegalArgumentException(buildErrorMessage(latestCode, code, email));
    }

    /**
     * Простая проверка валидности кода
     */
    private boolean isValidCode(VerificationCode verification, String code) {
        return verification.getCode().equals(code) 
               && !verification.isExpired()
               && verification.getStatus() != VerificationStatus.USED
               && verification.getAttempts() < maxAttempts;
    }

    /**
     * Поиск кода с tolerance (опционально)
     */
    private VerificationCode findToleranceCode(String email, String code) {
        if (toleranceMinutes <= 0) return null;
        
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(toleranceMinutes);
        return repository.findRecentByEmail(email, cutoff)
                .stream()
                .filter(v -> v.getCode().equals(code))
                .filter(v -> v.getStatus() == VerificationStatus.SUPERSEDED)
                .filter(v -> !v.isExpired())
                .findFirst()
                .orElse(null);
    }

    /**
     * Маркировка как использованный
     */
    private VerificationCode markAsUsedAndReturn(VerificationCode verification) {
        verification.setStatus(VerificationStatus.USED);
        repository.save(verification);
        log.info("Код успешно подтвержден для VerificationCode[id={}]", verification.getId());
        return verification;
    }
    //TODO нужно ли добавить в exceptionHnadler
    /**
     * Понятные сообщения об ошибках
     */
    private String buildErrorMessage(VerificationCode latest, String code, String email) {
        if (latest.isExpired()) {
            return "Код истек. Запросите новый код";
        }
        
        if (latest.getStatus() == VerificationStatus.USED) {
            return "Код уже использован";
        }
        
        if (latest.getAttempts() >= maxAttempts) {
            return "Превышено количество попыток. Запросите новый код";
        }
        
        // Проверяем, не старый ли код пытается ввести пользователь
        boolean isOldCode = repository.findRecentByEmail(email, LocalDateTime.now().minusHours(1))
                .stream()
                .anyMatch(old -> old.getCode().equals(code));
        
        if (isOldCode) {
            return "Используйте код из последнего письма";
        }
        
        return "Неверный код подтверждения";
    }

    /**
     * Упрощенная очистка - просто удаляем коды старше 24 часов
     */
    @Transactional
    public void cleanupExpiredCodes() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        repository.deleteOldCodes(cutoffTime);
        log.debug("Удалены коды старше 24 часов");
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

    /**
     * Отправить код для сброса пароля (переиспользует основную логику верификации)
     */
    @Transactional
    @SneakyThrows
    public void sendPasswordResetCode(String email, UserCredentials userCredentials) {
        // Проверяем cooldown (защита от спама)
        checkResendCooldown(email);
        
        // Отмечаем старые активные коды как замененные (вместо удаления)
        Long userId = userCredentials.getUser().getId();
        repository.updateStatusByUserIdAndCurrentStatus(userId, VerificationStatus.ACTIVE, VerificationStatus.SUPERSEDED);
        log.info("Отмечены старые активные коды как SUPERSEDED для пользователя: {} (password reset)", userId);

        String code = OtpGenerator.sixDigits();

        VerificationCode verification = VerificationCode.builder()
                .userCredentials(userCredentials)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(codeTtlMinutes))
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(verification);

        log.info("Отправка кода сброса пароля на email: {} для пользователя: {}", email, userId);

        // Используем специальный метод для отправки кода сброса пароля
        emailSender.sendPasswordResetCode(email, code);
    }
}
