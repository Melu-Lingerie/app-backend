package ru.mellingerie.api.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<UserCreateFacadeResponseDto> createUser(@Valid @RequestBody UserCreateFacadeRequestDto request) {
        log.info("Получен запрос на создание гостевого пользователя с sessionId: {}", request.getSessionId());
        try {
            UserCreateFacadeResponseDto response = userFacadeApi.createUser(request);
            log.info("Гостевой пользователь успешно создан с ID: {}", response.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Некорректные данные запроса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Ошибка при создании гостевого пользователя: sessionId {}, ошибка: {}", request.getSessionId(), e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}