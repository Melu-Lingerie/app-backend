package ru.melulingerie.facade.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.facade.user.api.UserFacadeApi;
import ru.melulingerie.facade.user.api.UserFacadeCreateService;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacadeApiImpl implements UserFacadeApi {
    
    private final UserFacadeCreateService userFacadeCreateService;
    
    @Override
    @Transactional
    public UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request, HttpServletRequest httpRequest) {
        log.info("Создание гостевого пользователя через фасад для sessionId: {}", request.getSessionId());
        
        try {
            // Делегируем создание пользователя сервису, который содержит всю бизнес-логику
            UserCreateFacadeResponseDto response = userFacadeCreateService.createUser(request, httpRequest);
            
            log.info("Гостевой пользователь успешно создан с ID: {}", response.getUserId());
            return response;
            
        } catch (Exception e) {
            log.error("Ошибка при создании гостевого пользователя: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать гостевого пользователя", e);
        }
    }
}
