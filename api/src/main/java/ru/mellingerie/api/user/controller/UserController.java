package ru.mellingerie.api.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            String sessionId,
            UserCreateFacadeRequestDto request) {

        log.info("Получен запрос на создание гостевого пользователя с sessionId: {}", sessionId);
        UserCreateFacadeResponseDto response = userCreateFacadeService.createGuestUser(request, sessionId);
        return ResponseEntity.ok(response);
    }
}