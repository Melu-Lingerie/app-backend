package ru.mellingerie.api.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mellingerie.api.user.resource.UserResource;
import ru.melulingerie.facade.user.api.UserFacadeApi;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserResource {
    
    private final UserFacadeApi userFacadeApi;
    
    @Override
    @PostMapping("/guests")
    public ResponseEntity<UserCreateFacadeResponseDto> createUser(
            @Valid @RequestBody UserCreateFacadeRequestDto request,
            HttpServletRequest httpRequest) {
        
        log.info("Получен запрос на создание гостевого пользователя с sessionId: {}", request.getSessionId());
        
        try {
            // Создание гостевого пользователя через фасад
            UserCreateFacadeResponseDto response = userFacadeApi.createUser(request, httpRequest);

            log.info("Гостевой пользователь успешно создан с ID: {}", response.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при создании гостевого пользователя: sessionId{}, и ошибкой: {}",request.getSessionId(), e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


}