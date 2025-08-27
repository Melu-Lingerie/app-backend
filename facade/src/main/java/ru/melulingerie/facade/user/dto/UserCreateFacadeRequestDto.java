package ru.melulingerie.facade.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;
import java.util.UUID;

public record UserCreateFacadeRequestDto(
        UUID sessionId,
        @Valid
        @NotNull
        DeviceInfoDto deviceInfo
) {

    public record DeviceInfoDto(
            @NotNull DeviceType deviceType,
            @Pattern(
                    regexp = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$",
                    message = "Некорректный IPv4-адрес"
            ) String ipAddress,
            @Size(max = 100) String deviceName,
            @Size(max = 50) String osVersion,
            @Size(max = 50) String browserName,
            @Size(max = 50) String browserVersion,
            @Positive Integer screenWidth,
            @Positive Integer screenHeight,
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal screenDensity
    ) {
    }
}
