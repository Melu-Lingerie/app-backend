package ru.melulingerie.facade.user.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.users.dto.UserCreateRequestDto;
import ru.melulingerie.users.dto.UserCreateResponseDto;
import ru.melulingerie.users.dto.UserUpdateRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.dto.UserInfoResponseDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeResponseDto;
import ru.melulingerie.users.entity.User;

@Mapper(config = ru.melulingerie.facade.config.MapStructConfig.class)
public interface UserFacadeMapper {

    UserCreateRequestDto facadeDtoToUsersDto(UserCreateFacadeRequestDto facadeDto);

    UserCreateFacadeResponseDto usersDtoToFacadeDto(UserCreateResponseDto usersResponse);

    UserInfoResponseDto toUserInfoResponseDto(User user);
    
    UserUpdateRequestDto toUserUpdateRequestDto(UserUpdateFacadeRequestDto facadeDto);
    
    UserUpdateFacadeResponseDto toUserUpdateFacadeResponseDto(User user);
}
