package ru.melulingerie.facade.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mellingerie.users.entity.DeviceType;

import java.math.BigDecimal;
import java.util.UUID;
//TODO data заменить на getter и setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateFacadeRequestDto {
    
    private UUID sessionId;
    private DeviceInfoDto deviceInfo;
    
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
