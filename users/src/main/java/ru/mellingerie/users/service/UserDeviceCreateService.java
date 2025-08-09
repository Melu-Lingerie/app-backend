package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserDeviceRequestDto;
import ru.mellingerie.users.entity.DeviceType;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.repository.UserDeviceRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceCreateService {
    
    private final UserDeviceRepository userDeviceRepository;
    
    @Transactional
    public UserDevice createUserDevice(User user, UserDeviceRequestDto deviceDto) {
        DeviceType deviceType = mapDeviceType(deviceDto.getDeviceType());
        
        UserDevice userDevice = UserDevice.builder()
                .user(user)
                .deviceType(deviceType)
                .deviceUuid(deviceDto.getDeviceUuid())
                .deviceName(deviceDto.getDeviceName())
                .osVersion(deviceDto.getOsVersion())
                .browserName(deviceDto.getBrowserName())
                .browserVersion(deviceDto.getBrowserVersion())
                .screenWidth(deviceDto.getScreenWidth())
                .screenHeight(deviceDto.getScreenHeight())
                .screenDensity(deviceDto.getScreenDensity())
                .pushToken(deviceDto.getPushToken())
                .lastSeenAt(deviceDto.getLastSeenAt())
                .build();
        
        UserDevice savedDevice = userDeviceRepository.save(userDevice);
        log.info("Создано устройство пользователя с ID: {}", savedDevice.getId());
        return savedDevice;
    }
    
    private DeviceType mapDeviceType(UserDeviceRequestDto.DeviceTypeRequestDto deviceTypeDto) {
        if (deviceTypeDto == null) {
            return DeviceType.WEB; // По умолчанию WEB
        }
        
        return switch (deviceTypeDto) {
            case IOS -> DeviceType.IOS;
            case ANDROID -> DeviceType.ANDROID;
            case WEB -> DeviceType.WEB;
        };
    }
} 