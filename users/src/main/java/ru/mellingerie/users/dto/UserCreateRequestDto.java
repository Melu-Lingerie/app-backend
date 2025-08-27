package ru.mellingerie.users.dto;

import jakarta.validation.constraints.NotNull;
import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;
import java.util.UUID;

public record UserCreateRequestDto(
        @NotNull
        UUID sessionId,
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
