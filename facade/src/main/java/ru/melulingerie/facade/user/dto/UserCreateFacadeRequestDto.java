package ru.melulingerie.facade.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.melulingerie.users.entity.DeviceType;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "UserCreateFacadeRequestDto", description = "Запрос на создание гостевого пользователя")
public record UserCreateFacadeRequestDto(

        @Schema(
                description = "Идентификатор клиентской сессии (если передаётся из клиента), формат UUID",
                example = "77e1c83b-7bb0-437b-bc50-a7a58e5660ac",
                nullable = true
        )
        UUID sessionId,

        @Schema(
                description = "Информация об устройстве клиента",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull
        DeviceInfoDto deviceInfo

) {

    @Schema(name = "DeviceInfoDto", description = "Параметры устройства клиента")
    public record DeviceInfoDto(

            @Schema(
                    description = "Тип устройства клиента",
                    implementation = DeviceType.class,
                    example = "MOBILE",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull DeviceType deviceType,

            @Schema(
                    description = "IPv4-адрес клиента",
                    example = "192.168.1.10",
                    pattern = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$"
            )
            @Pattern(
                    regexp = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$",
                    message = "Некорректный IPv4-адрес"
            )
            String ipAddress,

            @Schema(
                    description = "Имя устройства",
                    example = "iPhone 13 Pro",
                    maxLength = 100
            )
            @Size(max = 100) String deviceName,

            @Schema(
                    description = "Версия ОС",
                    example = "iOS 17.5",
                    maxLength = 50
            )
            @Size(max = 50) String osVersion,

            @Schema(
                    description = "Название браузера",
                    example = "Safari",
                    maxLength = 50
            )
            @Size(max = 50) String browserName,

            @Schema(
                    description = "Версия браузера",
                    example = "17.1",
                    maxLength = 50
            )
            @Size(max = 50) String browserVersion,

            @Schema(
                    description = "Ширина экрана в пикселях",
                    example = "1170",
                    minimum = "1"
            )
            @Positive Integer screenWidth,

            @Schema(
                    description = "Высота экрана в пикселях",
                    example = "2532",
                    minimum = "1"
            )
            @Positive Integer screenHeight,

            @Schema(
                    description = "Плотность экрана (DPR, device pixel ratio)",
                    example = "3.0",
                    minimum = "0",
                    exclusiveMinimum = true
            )
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal screenDensity

    ) {}
}
