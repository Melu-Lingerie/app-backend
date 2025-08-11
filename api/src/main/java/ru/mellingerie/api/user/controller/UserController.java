package ru.mellingerie.api.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.service.UserCreateFacadeService;
import ru.mellingerie.api.user.resource.UserResource;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserResource {

    private final UserCreateFacadeService userCreateFacadeService;

    @Override
    public ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(UserCreateFacadeRequestDto request) {
        log.info("Получен запрос на создание пользователя-гостя с сессией {}", request.getSessionId());
        try {
            UserCreateFacadeResponseDto response = userCreateFacadeService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя-гостя: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}