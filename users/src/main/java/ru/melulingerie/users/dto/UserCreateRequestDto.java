package ru.melulingerie.users.dto;

import jakarta.validation.constraints.NotNull;
import ru.melulingerie.users.entity.DeviceType;

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
