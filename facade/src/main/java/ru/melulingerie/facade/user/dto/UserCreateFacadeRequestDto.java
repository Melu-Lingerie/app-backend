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
import java.net.Inet4Address;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateFacadeRequestDto {

    @Size(max = 100)
    private String sessionId;

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

        private String ipAddress;

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
        //TODO  inclusive?
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal screenDensity;
    }
}
