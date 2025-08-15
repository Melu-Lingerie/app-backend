package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.entity.DeviceType;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.repository.UserDeviceRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceCreateService {

    private final UserDeviceRepository userDeviceRepository;

    @Transactional
    public UserDevice createUserDevice(UserCreateRequestDto.DeviceInfoDto deviceInfo, Long userId) {
        User user = new User();
        user.setId(userId);

        UserDevice userDevice = UserDevice.builder()
                .user(user)
                .deviceType(mapDeviceInfoToDeviceType(deviceInfo.getDeviceType()))
                .deviceName(deviceInfo.getDeviceName())
                .osVersion(deviceInfo.getOsVersion())
                .browserName(deviceInfo.getBrowserName())
                .browserVersion(deviceInfo.getBrowserVersion())
                .screenWidth(deviceInfo.getScreenWidth())
                .screenHeight(deviceInfo.getScreenHeight())
                .screenDensity(deviceInfo.getScreenDensity())
                .lastSeenAt(LocalDateTime.now())
                .build();

        UserDevice savedDevice = userDeviceRepository.save(userDevice);
        log.info("Создано устройство пользователя с ID: {} для userId: {}", savedDevice.getId(), userId);
        return savedDevice;
    }

    private DeviceType mapDeviceInfoToDeviceType(DeviceType deviceType) {
        if (deviceType == null) {
            throw new IllegalArgumentException("deviceType is required");
        }
        return deviceType;
    }
}
