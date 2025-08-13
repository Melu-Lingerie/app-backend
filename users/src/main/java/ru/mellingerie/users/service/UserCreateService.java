package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;
import ru.mellingerie.users.entity.*;
import ru.mellingerie.users.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;
    private final UserSessionQueryService userSessionQueryService;
    private final UserDeviceQueryService userDeviceQueryService;
    private final UserSessionCreateService userSessionCreateService;
    private final UserDeviceCreateService userDeviceCreateService;

    @Transactional
    public UserCreateResponseDto createUser(UserCreateRequestDto request) {
        log.info("Создание пользователя с sessionId: {}", request.getSessionId());

        // 1. Проверить, существует ли сессия
        Optional<UserSession> existingSession = userSessionQueryService.findBySessionId(request.getSessionId());

        if (existingSession.isPresent()) {
            log.debug("Сессия найдена для пользователя: {}", existingSession.get().getUser().getId());
            return handleExistingSession(existingSession.get(), request);
        }

        // 2. Проверить, существует ли устройство по deviceUuid (известное устройство)
        if (request.getDeviceInfo() != null && request.getDeviceInfo().getDeviceUuid() != null) {
            Optional<UserDevice> existingDevice = userDeviceQueryService.findByDeviceUuid(request.getDeviceInfo().getDeviceUuid());
            if (existingDevice.isPresent()) {
                User user = existingDevice.get().getUser();
                log.debug("Найдено устройство {}, переиспользуем user {}", existingDevice.get().getId(), user.getId());

                // Создать новую сессию для существующего пользователя и связать с найденным устройством
                UserSession session = userSessionCreateService.createUserSession(
                        request.getSessionId(),
                        user,
                        request.getDeviceInfo(),
                        request.getIpAddress()
                );
                session.setUserDevice(existingDevice.get());
                userSessionCreateService.saveUserSession(session);

                return UserCreateResponseDto.builder()
                        .userId(user.getId())
                        .userSessionId(session.getId())
                        .userDeviceId(existingDevice.get().getId())
                        .sessionExpiresAt(session.getExpiresAt())
                        .build();
            }
        }

        // 3. Создать совершенно нового пользователя, сессию и устройство
        log.debug("Создание нового пользователя");
        return createNewUserWithSessionAndDevice(request);
    }

    private UserCreateResponseDto handleExistingSession(UserSession existingSession, UserCreateRequestDto request) {
        // Если пришёл новый deviceUuid, проверить/создать устройство и привязать к сессии
        if (request.getDeviceInfo() != null && request.getDeviceInfo().getDeviceUuid() != null) {
            UUID deviceUuid = request.getDeviceInfo().getDeviceUuid();
            Long userId = existingSession.getUser().getId();
            Optional<UserDevice> existingDeviceForUser = userDeviceQueryService.findByUserIdAndDeviceUuid(userId, deviceUuid);
            if (existingDeviceForUser.isEmpty()) {
                log.debug("Для существующей сессии {} привязываем новое устройство с UUID {}", existingSession.getId(), deviceUuid);
                UserDevice newDevice = userDeviceCreateService.createUserDevice(request.getDeviceInfo(), userId);
                existingSession.setUserDevice(newDevice);
                userSessionCreateService.saveUserSession(existingSession);
            } else if (existingSession.getUserDevice() == null ||
                    !existingSession.getUserDevice().getId().equals(existingDeviceForUser.get().getId())) {
                existingSession.setUserDevice(existingDeviceForUser.get());
                userSessionCreateService.saveUserSession(existingSession);
            }
        }

        return UserCreateResponseDto.builder()
                .userId(existingSession.getUser().getId())
                .userSessionId(existingSession.getId())
                .userDeviceId(existingSession.getUserDevice() != null ? existingSession.getUserDevice().getId() : null)
                .sessionExpiresAt(existingSession.getExpiresAt())
                .build();
    }

    private UserCreateResponseDto createNewUserWithSessionAndDevice(UserCreateRequestDto request) {
        // Создать пользователя
        User newUser = User.builder()
                .role(UserRole.GUEST)
                .status(UserStatus.UNREGISTERED)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Создан новый пользователь с ID: {}", savedUser.getId());

        // Создать пользовательскую сессию
        UserSession userSession = userSessionCreateService.createUserSession(
                request.getSessionId(),
                savedUser,
                request.getDeviceInfo(),
                request.getIpAddress()
        );

        userSessionCreateService.saveUserSession(userSession);

        return UserCreateResponseDto.builder()
                .userId(savedUser.getId())
                .userSessionId(userSession.getId())
                .userDeviceId(userSession.getUserDevice().getId())
                .sessionExpiresAt(userSession.getExpiresAt())
                .build();
    }
}