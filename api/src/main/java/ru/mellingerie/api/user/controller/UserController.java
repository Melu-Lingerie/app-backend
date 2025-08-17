package ru.mellingerie.api.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mellingerie.api.user.resource.UserResource;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.service.UserCreateFacadeService;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserResource {

    private final UserCreateFacadeService userCreateFacadeService;

    @Override
    @PostMapping("/guests")
    public ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @CookieValue(name = "sessionId") String sessionId,
            @Valid @RequestBody UserCreateFacadeRequestDto request) {

        log.info("Получен запрос на создание гостевого пользователя с sessionId: {}", sessionId);
        //TODO убрать try и сделать эксепшен хэндлер
        try {
            UserCreateFacadeResponseDto response = userCreateFacadeService.createGuestUser(request, sessionId);
            log.info("Гостевой пользователь успешно создан с ID: {}", response.userId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Некорректные данные запроса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Ошибка при создании гостевого пользователя: sessionId {}, ошибка: {}", sessionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}