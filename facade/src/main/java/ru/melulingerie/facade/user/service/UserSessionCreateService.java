package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCreateService {
    
    public Long createUserSession(java.util.UUID sessionId, Long userId) {
        // Заглушка - в реальной реализации будет создаваться сессия в модуле users
        log.info("Создание сессии для пользователя: {} с sessionId: {}", userId, sessionId);
        return 100L + userId;
    }
}
