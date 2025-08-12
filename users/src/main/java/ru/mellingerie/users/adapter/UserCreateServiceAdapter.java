package ru.mellingerie.users.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;
import ru.mellingerie.users.service.UserCreateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateServiceAdapter {
    
    private final UserCreateService userCreateService;
    
    /**
     * Создает пользователя с сессией и устройством
     * Этот метод будет вызываться из модуля facade через Spring Context
     */
    public UserCreateResponseDto createUser(UserCreateRequestDto request) {
        log.info("Адаптер: создание пользователя для sessionId: {}", request.getSessionId());
        
        // Вызов внутреннего сервиса
        return userCreateService.createUser(request);
    }
}
