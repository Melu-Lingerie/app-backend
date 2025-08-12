package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.entity.UserRole;
import ru.mellingerie.users.entity.UserStatus;
import ru.mellingerie.users.repository.UserRepository;

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
        log.info("Создание пользователя для sessionId: {}", request.getSessionId());
        
        // 1. Проверить, существует ли сессия
        var existingSession = userSessionQueryService.findBySessionId(request.getSessionId());
        
        if (existingSession.isPresent()) {
            log.debug("Сессия найдена для пользователя: {}", existingSession.get().getUser().getId());
            return handleExistingSession(existingSession.get(), request);
        }
        
        // 2. Проверить, существует ли устройство
        var existingDevice = userDeviceQueryService.findByDeviceUuid(request.getDeviceInfo().getDeviceUuid());
        
        if (existingDevice.isPresent()) {
            log.debug("Устройство найдено для пользователя: {}", existingDevice.get().getUser().getId());
            return handleExistingDevice(existingDevice.get(), request);
        }
        
        // 3. Создать совершенно нового пользователя, сессию и устройство
        log.debug("Создание нового пользователя");
        return createNewUserWithSessionAndDevice(request);
    }
    
    private UserCreateResponseDto handleExistingSession(UserSession existingSession, UserCreateRequestDto request) {
        User user = existingSession.getUser();
        
        // Создать новую сессию для существующего пользователя
        var newSession = userSessionCreateService.createUserSession(request.getSessionId(), user.getId());
        
        // Проверить, отличается ли устройство и создать новое устройство при необходимости
        var existingDevice = userDeviceQueryService.findByUserIdAndDeviceUuid(user.getId(), request.getDeviceInfo().getDeviceUuid());
        
        UserDevice userDevice;
        if (existingDevice.isEmpty()) {
            log.debug("Создание нового устройства для существующего пользователя");
            userDevice = userDeviceCreateService.createUserDevice(request.getDeviceInfo(), user.getId());
        } else {
            userDevice = existingDevice.get();
        }
        
        // Связать сессию с устройством
        newSession.setUserDevice(userDevice);
        userSessionCreateService.saveUserSession(newSession);
        
        return UserCreateResponseDto.builder()
                .userId(user.getId())
                .userSessionId(newSession.getId())
                .userDeviceId(userDevice.getId())
                .build();
    }
    
    private UserCreateResponseDto handleExistingDevice(UserDevice existingDevice, UserCreateRequestDto request) {
        User user = existingDevice.getUser();
        
        // Создать новую сессию для существующего пользователя
        var newSession = userSessionCreateService.createUserSession(request.getSessionId(), user.getId());
        newSession.setUserDevice(existingDevice);
        userSessionCreateService.saveUserSession(newSession);
        
        return UserCreateResponseDto.builder()
                .userId(user.getId())
                .userSessionId(newSession.getId())
                .userDeviceId(existingDevice.getId())
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
        var userSession = userSessionCreateService.createUserSession(request.getSessionId(), savedUser.getId());
        
        // Создать пользовательское устройство
        var userDevice = userDeviceCreateService.createUserDevice(request.getDeviceInfo(), savedUser.getId());
        
        // Связать сессию с устройством
        userSession.setUserDevice(userDevice);
        userSessionCreateService.saveUserSession(userSession);
        
        return UserCreateResponseDto.builder()
                .userId(savedUser.getId())
                .userSessionId(userSession.getId())
                .userDeviceId(userDevice.getId())
                .build();
    }
} 