package ru.mellingerie.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {
    
    private UUID sessionId;
    private DeviceInfoDto deviceInfo;
    private String ipAddress;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfoDto {
        
        private DeviceType deviceType;
        private UUID deviceUuid;
        private String deviceName;
        private String osVersion;
        private String browserName;
        private String browserVersion;
        private Integer screenWidth;
        private Integer screenHeight;
        private BigDecimal screenDensity;
    }
}
