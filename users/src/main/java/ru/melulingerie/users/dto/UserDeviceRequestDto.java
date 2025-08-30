package ru.melulingerie.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserDeviceRequestDto {

    private DeviceTypeRequestDto deviceType;
    private UUID deviceUuid;
    private String deviceName;
    private String osVersion;
    private String browserName;
    private String browserVersion;
    private Integer screenWidth;
    private Integer screenHeight;
    private BigDecimal screenDensity;
    private String pushToken;
    private LocalDateTime lastSeenAt;

    public enum DeviceTypeRequestDto {
        IOS,
        ANDROID,
        WEB,
        TABLET,
        OTHER
    }
}