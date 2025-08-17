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
    @Mapping(target = "sessionId", source = "sessionId")
    UserCreateRequestDto facadeDtoToUsersDto(UserCreateFacadeRequestDto facadeDto, String sessionId);
    
    /**
     * Конвертирует DTO из модуля users в фасадный DTO
     */
    UserCreateFacadeResponseDto usersDtoToFacadeDto(UserCreateResponseDto usersResponse);
}
