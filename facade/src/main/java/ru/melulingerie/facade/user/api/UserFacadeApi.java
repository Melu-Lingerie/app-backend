package ru.melulingerie.facade.user.api;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserFacadeApi {
    
    /**
     * Создает гостевого пользователя с автоматическим созданием сессии, устройства, корзины и списка желаний.
     *
     * @param request данные для создания гостевого пользователя (включая `ipAddress`)
     * @param httpRequest HTTP запрос (может использоваться для служебных проверок, например ограничение размера)
     * @return информация о созданном пользователе и связанных сущностях
     */
    UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request, HttpServletRequest httpRequest);
}
