package ru.melulingerie.facade.user.service;

import org.springframework.security.core.Authentication;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeResponseDto;

/**
 * Фасадный сервис для обновления данных пользователя
 */
public interface UserUpdateFacadeService {
    
    /**
     * Обновление данных текущего авторизованного пользователя
     * 
     * @param authentication объект аутентификации
     * @param request данные для обновления
     * @return результат обновления
     */
    UserUpdateFacadeResponseDto updateCurrentUser(Authentication authentication, UserUpdateFacadeRequestDto request);
}

