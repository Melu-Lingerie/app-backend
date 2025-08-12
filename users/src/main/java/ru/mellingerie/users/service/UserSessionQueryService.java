package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.repository.UserSessionRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionQueryService {
    
    private final UserSessionRepository userSessionRepository;
    
    public Optional<UserSession> findBySessionId(UUID sessionId) {
        log.debug("Поиск сессии по sessionId: {}", sessionId);
        return userSessionRepository.findBySessionId(sessionId);
    }
}
