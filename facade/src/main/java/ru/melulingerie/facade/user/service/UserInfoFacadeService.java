package ru.melulingerie.facade.user.service;

import org.springframework.security.core.Authentication;
import ru.melulingerie.facade.user.dto.UserInfoResponseDto;

/**
 * Фасадный сервис для получения информации о пользователе
 */
public interface UserInfoFacadeService {
    
    /**
     * Получение информации о текущем авторизованном пользователе
     * @param authentication объект аутентификации
     * @return информация о пользователе
     */
    UserInfoResponseDto getUserInfo(Authentication authentication);
}

