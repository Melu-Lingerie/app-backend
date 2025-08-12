package ru.melulingerie.facade.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;

@Mapper(config = ru.melulingerie.facade.config.MapStructConfig.class)
public interface UserFacadeMapper {
    
    /**
     * Конвертирует фасадный DTO в DTO для модуля users
     */
    @Mapping(target = "ipAddress", ignore = true) // IP будет добавлен в сервисе
    UserCreateRequestDto facadeDtoToUsersDto(UserCreateFacadeRequestDto facadeDto);
    
    /**
     * Конвертирует DTO из модуля users в фасадный DTO
     */
    @Mapping(target = "cartId", source = "cartId")
    @Mapping(target = "wishlistId", source = "wishlistId")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "sessionStatus", expression = "java(ru.mellingerie.users.entity.SessionStatus.ACTIVE)")
    UserCreateFacadeResponseDto usersDtoToFacadeDto(UserCreateResponseDto usersResponse, Long cartId, Long wishlistId);
}
