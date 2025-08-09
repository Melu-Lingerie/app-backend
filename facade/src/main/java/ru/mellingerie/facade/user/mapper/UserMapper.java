package ru.mellingerie.facade.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.mellingerie.facade.config.MapStructConfig;
import ru.mellingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.mellingerie.users.entity.User;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
    @Mapping(target = "createdAt", source = "createdAt")
    UserCreateFacadeResponseDto toUserCreateResponseDto(User user);
    
    @Named("mapStatus")
    default String mapStatus(ru.mellingerie.users.entity.UserStatus status) {
        return status != null ? status.name() : null;
    }
    
    @Named("mapRole")
    default String mapRole(ru.mellingerie.users.entity.UserRole role) {
        return role != null ? role.name() : null;
    }
} 