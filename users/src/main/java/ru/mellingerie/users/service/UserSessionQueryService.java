package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.repository.UserSessionRepository;

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
