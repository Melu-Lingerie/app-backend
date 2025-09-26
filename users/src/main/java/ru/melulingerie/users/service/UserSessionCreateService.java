package ru.melulingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.users.dto.UserCreateRequestDto;
import ru.melulingerie.users.entity.SessionStatus;
import ru.melulingerie.users.entity.User; 
import ru.melulingerie.users.entity.UserDevice;
import ru.melulingerie.users.entity.UserSession;
import ru.melulingerie.users.repository.UserSessionRepository;

import java.net.InetAddress;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCreateService {

    private final UserSessionRepository userSessionRepository;
    private final UserDeviceCreateService userDeviceCreateService;

    @Transactional
    public UserSession createUserSession(UUID sessionId, User user, UserCreateRequestDto.DeviceInfoDto deviceInfo) {

        // Создать новое устройство для пользователя
        UserDevice userDevice = null;
        if (deviceInfo != null) {
            userDevice = userDeviceCreateService.createUserDevice(deviceInfo);
        }

        // Безопасное получение IP-адреса
        InetAddress ipAddress = null;
        if (deviceInfo != null) {
            try {
                ipAddress = InetAddress.getByName(deviceInfo.ipAddress().trim());
            } catch (Exception e) {
                log.warn("Некорректный IP-адрес: '{}'. Сессия будет создана без IP.", deviceInfo.ipAddress(), e);
            }
        }

        UserSession userSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .userDevice(userDevice)
                .status(SessionStatus.ACTIVE)
                .ipAddress(ipAddress)
                .build();
        userSession.addDevice(userDevice);

        UserSession savedSession = userSessionRepository.save(userSession);
        log.info("Создана сессия пользователя с ID: {}", savedSession.getId());
        return savedSession;
    }
}