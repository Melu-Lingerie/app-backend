package ru.mellingerie.users.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserRole;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.entity.UserStatus;
import ru.mellingerie.users.repository.UserRepository;

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
        UserSession userSession = userSessionCreateService.createUserSession(request.sessionId(), request.deviceInfo());
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
}