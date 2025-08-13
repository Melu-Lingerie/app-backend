package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.entity.SessionStatus;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.repository.UserSessionRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCreateService {
    
    private final UserSessionRepository userSessionRepository;
    private final UserDeviceCreateService userDeviceCreateService;
    private final UserDeviceQueryService userDeviceQueryService;

    @Transactional
    public UserSession createUserSession(UUID sessionId, User user, UserCreateRequestDto.DeviceInfoDto deviceInfo, String ipAddressStr) {

        // Проверить, отличается ли устройство и создать новое устройство при необходимости
        Optional<UserDevice> existingDevice = userDeviceQueryService.findByUserIdAndDeviceUuid(user.getId(), deviceInfo.getDeviceUuid());
        UserDevice userDevice;
        if (existingDevice.isEmpty()) {
            log.debug("Создание нового устройства для существующего пользователя");
            userDevice = userDeviceCreateService.createUserDevice(deviceInfo, user.getId());
        } else {
            userDevice = existingDevice.get();
        }

        UserSession userSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .userDevice(userDevice)
                .status(SessionStatus.ACTIVE)
                .build();

        if (ipAddressStr != null && !ipAddressStr.isBlank()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddressStr);
                userSession.setIpAddress(inetAddress);
            } catch (UnknownHostException e) {
                log.warn("Не удалось распарсить IP-адрес '{}': {}", ipAddressStr, e.getMessage());
            }
        }
        
        UserSession savedSession = userSessionRepository.save(userSession);
        log.info("Создана сессия пользователя с ID: {} для userId: {}", savedSession.getId(), user.getId());
        return savedSession;
    }
    
    @Transactional
    public UserSession saveUserSession(UserSession userSession) {
        UserSession savedSession = userSessionRepository.save(userSession);
        log.debug("Сохранена сессия пользователя с ID: {}", savedSession.getId());
        return savedSession;
    }
}