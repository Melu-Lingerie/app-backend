package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCreateService {

    private final UserSessionRepository userSessionRepository;
    private final UserDeviceCreateService userDeviceCreateService;

    @Transactional
    public void createUserSession(String sessionId, User user, UserCreateRequestDto.DeviceInfoDto deviceInfo) {

        // Создать новое устройство для пользователя
        UserDevice userDevice = null;
        if (deviceInfo != null) {
            log.debug("Создание нового устройства для пользователя {}", user.getId());
            userDevice = userDeviceCreateService.createUserDevice(deviceInfo, user.getId());
        }

        UserSession userSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .userDevice(userDevice)
                .status(SessionStatus.ACTIVE)
                .build();

        if (Objects.nonNull(deviceInfo) && StringUtils.isNotBlank(deviceInfo.ipAddress())) {
            try {
                InetAddress inetAddress = InetAddress.getByName(deviceInfo.ipAddress());
                userSession.setIpAddress(inetAddress);
            } catch (UnknownHostException e) {
                log.warn("Не удалось распарсить IP-адрес '{}': {}", deviceInfo.ipAddress(), e.getMessage());
            }
        }

        UserSession savedSession = userSessionRepository.save(userSession);
        log.info("Создана сессия пользователя с ID: {} для userId: {}", savedSession.getId(), user.getId());
    }
}