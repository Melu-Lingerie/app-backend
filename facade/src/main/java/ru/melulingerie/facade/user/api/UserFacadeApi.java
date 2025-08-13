package ru.melulingerie.facade.user.api;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserFacadeApi {
    
    /**
     * Создает гостевого пользователя с автоматическим созданием сессии, устройства, корзины и списка желаний
     * 
     * @param request данные для создания гостевого пользователя
     * @param httpRequest HTTP запрос для извлечения IP адреса
     * @return информация о созданном пользователе и связанных сущностях
     */
    UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request, HttpServletRequest httpRequest);
}
