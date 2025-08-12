package ru.melulingerie.facade.user.api;

import ru.melulingerie.facade.user.mapper.UserFacadeMapper.UserCreateInternalDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper.UserCreateInternalResponseDto;

public interface UserCreateService {
    
    /**
     * Создает пользователя с сессией и устройством
     * 
     * @param request данные для создания пользователя
     * @return информация о созданном пользователе
     */
    UserCreateInternalResponseDto createUser(UserCreateInternalDto request);
}
