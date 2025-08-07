package ru.mellingerie.facade.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.mellingerie.facade.config.MapStructConfig;
import ru.mellingerie.facade.user.dto.UserDeviceFacadeDto;
import ru.mellingerie.users.dto.UserDeviceRequestDto;

@Mapper(config = MapStructConfig.class)
public interface UserDeviceMapper {
    
    @Mapping(target = "deviceType", source = "deviceType", qualifiedByName = "mapDeviceType")
    UserDeviceRequestDto toUserDeviceRequestDto(UserDeviceFacadeDto userDeviceFacadeDto);
    
    @Named("mapDeviceType")
    default UserDeviceRequestDto.DeviceTypeRequestDto mapDeviceType(UserDeviceFacadeDto.DeviceTypeDto deviceTypeDto) {
        if (deviceTypeDto == null) {
            return null;
        }
        
        return switch (deviceTypeDto) {
            case IOS -> UserDeviceRequestDto.DeviceTypeRequestDto.IOS;
            case ANDROID -> UserDeviceRequestDto.DeviceTypeRequestDto.ANDROID;
            case WEB -> UserDeviceRequestDto.DeviceTypeRequestDto.WEB;
        };
    }
} 