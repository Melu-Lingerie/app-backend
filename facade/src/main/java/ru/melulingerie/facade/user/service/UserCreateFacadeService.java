package ru.melulingerie.facade.user.service;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

/**
 * Фасадный сервис-оркестратор для создания гостевого пользователя
 * (делегирует доменному модулю users создание пользователя/сессии/устройства
 * и оркестрирует создание корзины и wishlist).
 */
public interface UserCreateFacadeService {

    UserCreateFacadeResponseDto createGuestUser(UserCreateFacadeRequestDto request);
}
