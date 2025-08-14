package ru.melulingerie.facade.user.api;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

public interface UserFacadeApi {

    /**
     * Создает гостевого пользователя с автоматическим созданием сессии, устройства, корзины и списка желаний.
     *
     * @param request данные для создания гостевого пользователя (включая `ipAddress`)
     * @return информация о созданном пользователе и связанных сущностях
     */
    UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request);
}
