package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.entity.DeviceType;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.repository.UserDeviceRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceCreateService {

    private final UserDeviceRepository userDeviceRepository;

    @Transactional
    public UserDevice createUserDevice(UserCreateRequestDto.DeviceInfoDto deviceInfo) {

        UserDevice userDevice = UserDevice.builder()
                .deviceType(mapDeviceInfoToDeviceType(deviceInfo.deviceType()))
                .deviceName(deviceInfo.deviceName())
                .osVersion(deviceInfo.osVersion())
                .browserName(deviceInfo.browserName())
                .browserVersion(deviceInfo.browserVersion())
                .screenWidth(deviceInfo.screenWidth())
                .screenHeight(deviceInfo.screenHeight())
                .screenDensity(deviceInfo.screenDensity())
                .lastSeenAt(LocalDateTime.now())
                .build();

        UserDevice savedDevice = userDeviceRepository.save(userDevice);
        log.info("Создано устройство пользователя с ID: {}", savedDevice.getId());
        return savedDevice;
    }

    private DeviceType mapDeviceInfoToDeviceType(DeviceType deviceType) {
        if (deviceType == null) {
            throw new IllegalArgumentException("deviceType is required");
        }
        return deviceType;
    }
}
