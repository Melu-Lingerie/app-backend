package ru.melulingerie.facade.user.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.users.dto.UserCreateRequestDto;
import ru.melulingerie.users.dto.UserCreateResponseDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

@Mapper(config = ru.melulingerie.facade.config.MapStructConfig.class)
public interface UserFacadeMapper {

    UserCreateRequestDto facadeDtoToUsersDto(UserCreateFacadeRequestDto facadeDto);

    UserCreateFacadeResponseDto usersDtoToFacadeDto(UserCreateResponseDto usersResponse);
}
