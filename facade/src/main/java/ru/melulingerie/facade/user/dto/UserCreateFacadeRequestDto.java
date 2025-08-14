package ru.melulingerie.facade.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateFacadeRequestDto {

    @NotNull
    private UUID sessionId;

    @NotBlank
    @Size(max = 45)
    private String ipAddress;

    @Valid
    @NotNull
    private DeviceInfoDto deviceInfo;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfoDto {

        @NotNull
        private DeviceType deviceType;

        // UUID валидируется десериализатором; допускается null
        private UUID deviceUuid;

        @Size(max = 100)
        private String deviceName;

        @Size(max = 50)
        private String osVersion;

        @Size(max = 50)
        private String browserName;

        @Size(max = 50)
        private String browserVersion;

        @Positive
        private Integer screenWidth;

        @Positive
        private Integer screenHeight;

        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal screenDensity;
    }
}
