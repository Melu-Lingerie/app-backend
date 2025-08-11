package ru.melulingerie.facade.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.mellingerie.users.dto.UserDeviceRequestDto;


@Mapper(config = MapStructConfig.class, uses = {UserDeviceMapper.class})
public interface UserCreateRequestMapper {

    @Mapping(target = "id", source = "userDevice.id")
    @Mapping(target = "deviceType", source = "userDevice.deviceType", qualifiedByName = "mapDeviceType")
    @Mapping(target = "deviceUuid", source = "userDevice.deviceUuid")
    @Mapping(target = "deviceName", source = "userDevice.deviceName")
    @Mapping(target = "osVersion", source = "userDevice.osVersion")
    @Mapping(target = "browserName", source = "userDevice.browserName")
    @Mapping(target = "browserVersion", source = "userDevice.browserVersion")
    @Mapping(target = "screenWidth", source = "userDevice.screenWidth")
    @Mapping(target = "screenHeight", source = "userDevice.screenHeight")
    @Mapping(target = "screenDensity", source = "userDevice.screenDensity")
    @Mapping(target = "pushToken", source = "userDevice.pushToken")
    @Mapping(target = "lastSeenAt", source = "userDevice.lastSeenAt")
    @Mapping(target = "createdAt", source = "userDevice.createdAt")
    UserDeviceRequestDto toUserDeviceRequestDto(UserCreateFacadeRequestDto request);
} 