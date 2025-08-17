package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.repository.UserSessionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSessionQueryService {

    private final UserSessionRepository userSessionRepository;

    public Optional<UserSession> findBySessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }
}
