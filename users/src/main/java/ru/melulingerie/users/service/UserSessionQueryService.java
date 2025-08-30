package ru.melulingerie.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.users.entity.UserSession;
import ru.melulingerie.users.repository.UserSessionRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionQueryService {

    private final UserSessionRepository userSessionRepository;

    public Optional<UserSession> findBySessionId(UUID sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }
}
