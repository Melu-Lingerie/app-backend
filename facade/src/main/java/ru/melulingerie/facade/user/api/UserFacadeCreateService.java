package ru.melulingerie.facade.user.api;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

/**
 * Фасадный сервис-оркестратор для создания гостевого пользователя
 * (делегирует доменному модулю users создание пользователя/сессии/устройства
 * и оркестрирует создание корзины и wishlist).
 */
public interface UserFacadeCreateService {

    /**
     * Создает гостевого пользователя с корзиной и списком желаний.
     */
    UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request);
}


