package ru.melulingerie.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.melulingerie.auth.service.EmailVerificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingConfig {

    private final EmailVerificationService emailVerificationService;

    @Scheduled(fixedRate = 3600000) // каждый час
    public void cleanupExpiredVerificationCodes() {
        log.debug("Запуск очистки истекших кодов верификации");
        emailVerificationService.cleanupExpiredCodes();
    }
}
