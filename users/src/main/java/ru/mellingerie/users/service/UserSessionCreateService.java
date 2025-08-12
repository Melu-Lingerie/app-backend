package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserDeviceRequestDto;
import ru.mellingerie.users.entity.SessionStatus;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.repository.UserSessionRepository;

import java.net.InetAddress;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCreateService {
    
    private final UserSessionRepository userSessionRepository;
    private final UserDeviceCreateService userDeviceCreateService;
    
    @Transactional
    public UserSession createUserSession(User user, UserDeviceRequestDto deviceDto, UUID sessionId, String ipAddress) {
        try {
            // Создаем устройство пользователя
            UserDevice userDevice = userDeviceCreateService.createUserDevice(user, deviceDto);
            
            // Создаем сессию пользователя
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            
            UserSession userSession = UserSession.builder()
                    .sessionId(sessionId)
                    .user(user)
                    .userDevice(userDevice)
                    .ipAddress(inetAddress)
                    .status(SessionStatus.ACTIVE)
                    .build();
            
            UserSession savedSession = userSessionRepository.save(userSession);
            log.info("Создана сессия пользователя с ID: {}", savedSession.getId());
            return savedSession;
        } catch (Exception e) {
            log.error("Ошибка при создании сессии пользователя: {}", e.getMessage());
            throw new RuntimeException("Не удалось создать сессию пользователя", e);
        }
    }
    
    @Transactional
    public UserSession createUserSession(UUID sessionId, Long userId) {
        User user = new User();
        user.setId(userId);
        
        UserSession userSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .status(SessionStatus.ACTIVE)
                .build();
        
        UserSession savedSession = userSessionRepository.save(userSession);
        log.info("Создана сессия пользователя с ID: {} для userId: {}", savedSession.getId(), userId);
        return savedSession;
    }
    
    @Transactional
    public UserSession saveUserSession(UserSession userSession) {
        UserSession savedSession = userSessionRepository.save(userSession);
        log.debug("Сохранена сессия пользователя с ID: {}", savedSession.getId());
        return savedSession;
    }
} 