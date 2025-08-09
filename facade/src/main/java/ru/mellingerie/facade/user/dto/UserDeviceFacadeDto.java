package ru.mellingerie.facade.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceFacadeDto {
    
    private Long id;
    private DeviceTypeDto deviceType;
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
    private LocalDateTime createdAt;
    
    public enum DeviceTypeDto {
        IOS,
        ANDROID,
        WEB
    }
} 