package ru.melulingerie.facade.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;

public record UserCreateFacadeRequestDto(
    @Valid
    @NotNull
    DeviceInfoDto deviceInfo
) {

    public record DeviceInfoDto(
        @NotNull DeviceType deviceType,
        String ipAddress,
        @Size(max = 100) String deviceName,
        @Size(max = 50) String osVersion,
        @Size(max = 50) String browserName,
        @Size(max = 50) String browserVersion,
        @Positive Integer screenWidth,
        @Positive Integer screenHeight,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal screenDensity
    ) {}
}
