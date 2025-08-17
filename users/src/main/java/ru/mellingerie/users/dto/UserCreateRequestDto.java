package ru.mellingerie.users.dto;

import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;

public record UserCreateRequestDto(
        String sessionId,
        DeviceInfoDto deviceInfo
) {

    public record DeviceInfoDto(
            String ipAddress,
            DeviceType deviceType,
            String deviceName,
            String osVersion,
            String browserName,
            String browserVersion,
            Integer screenWidth,
            Integer screenHeight,
            BigDecimal screenDensity
    ) {
    }
}
