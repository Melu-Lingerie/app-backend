package ru.melulingerie.users.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.users.dto.UserCreateRequestDto;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserRole;
import ru.melulingerie.users.entity.UserSession;
import ru.melulingerie.users.entity.UserStatus;
import ru.melulingerie.users.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;
    private final UserSessionQueryService userSessionQueryService;
    private final UserSessionCreateService userSessionCreateService;

    @Transactional
    public Long createGuestUser(@NonNull UserCreateRequestDto request) {
        try {
            return userSessionQueryService.findBySessionId(request.sessionId())
                    .map(this::handleExistingSession)
                    .orElseGet(() -> createNewUserWithSessionAndDevice(request));
        } catch (DataIntegrityViolationException e) {
            log.info("Session {} уже создана другим процессом, используем существующую", request.sessionId());
            return userSessionQueryService.findBySessionId(request.sessionId())
                    .map(session -> session.getUser().getId())
                    .orElseThrow(() -> new IllegalStateException("Сессия должна существовать"));
        }
    }

    private Long handleExistingSession(UserSession existingSession) {
        //TODO проверить живая ли сессия
        User user = existingSession.getUser();
        log.info("У пользователя userId: {}, уже существует сессия с id: {}", user.getId(), existingSession.getId());
        return user.getId();
    }

    private Long createNewUserWithSessionAndDevice(UserCreateRequestDto request) {
        User newUser = createAndSaveUser();
        UserSession userSession = userSessionCreateService.createUserSession(request.sessionId(), newUser, request.deviceInfo());
        newUser.addUserSession(userSession);
        log.info("Создание нового юзера с ID: {}", newUser.getId());
        return newUser.getId();
    }

    private User createAndSaveUser() {
        User newUser = User.builder()
                .role(UserRole.GUEST)
                .status(UserStatus.UNREGISTERED)
                .build();

        return userRepository.save(newUser);
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.empty();
    }
}