package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserRole;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.entity.UserStatus;
import ru.mellingerie.users.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;
    private final UserSessionQueryService userSessionQueryService;
    private final UserDeviceQueryService userDeviceQueryService;
    private final UserSessionCreateService userSessionCreateService;

    @Transactional
    public UserCreateResponseDto createUser(UserCreateRequestDto request) {
        log.info("Создание пользователя с sessionId: {}", request.getSessionId());

        // 1. Проверить, существует ли сессия
        Optional<UserSession> existingSession = userSessionQueryService.findBySessionId(request.getSessionId());

        if (existingSession.isPresent()) {
            log.debug("Сессия найдена для пользователя: {}", existingSession.get().getUser().getId());
            return handleExistingSession(existingSession.get(), request);
        }

//        // 2. Проверить, существует ли устройство
//        Optional<UserDevice> existingDevice = userDeviceQueryService.findByDeviceUuid(request.getDeviceInfo().getDeviceUuid());
//
//        if (existingDevice.isPresent()) {
//            log.debug("Устройство найдено для пользователя: {}", existingDevice.get().getUser().getId());
//            return handleExistingDevice(existingDevice.get(), request);
//        }

        // 3. Создать совершенно нового пользователя, сессию и устройство
        log.debug("Создание нового пользователя");
        return createNewUserWithSessionAndDevice(request);
    }

    private UserCreateResponseDto handleExistingSession(UserSession existingSession, UserCreateRequestDto request) {
        User user = existingSession.getUser();

        // Создать новую сессию для существующего пользователя
        UserSession newSession = userSessionCreateService.createUserSession(request.getSessionId(), user, request.getDeviceInfo());

        userSessionCreateService.saveUserSession(newSession);

        return UserCreateResponseDto.builder()
                .userId(user.getId())
                .userSessionId(newSession.getId())
                .userDeviceId(newSession.getUserDevice().getId())
                .build();
    }

//    private UserCreateResponseDto handleExistingDevice(UserDevice existingDevice, UserCreateRequestDto request) {
//        User user = existingDevice.getUser();
//
//        // Создать новую сессию для существующего пользователя
//        UserSession newSession = userSessionCreateService.createUserSession(request.getSessionId(), user.getId(), request.getDeviceInfo());
//        newSession.setUserDevice(existingDevice);
//        userSessionCreateService.saveUserSession(newSession);
//
//        return UserCreateResponseDto.builder()
//                .userId(user.getId())
//                .userSessionId(newSession.getId())
//                .userDeviceId(existingDevice.getId())
//                .build();
//    }

    private UserCreateResponseDto createNewUserWithSessionAndDevice(UserCreateRequestDto request) {
        // Создать пользователя
        User newUser = User.builder()
                .role(UserRole.GUEST)
                .status(UserStatus.UNREGISTERED)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Создан новый пользователь с ID: {}", savedUser.getId());

        // Создать пользовательскую сессию
        UserSession userSession = userSessionCreateService.createUserSession(request.getSessionId(), savedUser, request.getDeviceInfo());

        userSessionCreateService.saveUserSession(userSession);

        return UserCreateResponseDto.builder()
                .userId(savedUser.getId())
                .userSessionId(userSession.getId())
                .userDeviceId(userSession.getUserDevice().getId())
                .build();
    }
}