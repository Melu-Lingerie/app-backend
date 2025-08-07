package ru.mellingerie.facade.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mellingerie.facade.user.dto.UserCreateRequestDto;
import ru.mellingerie.facade.user.dto.UserCreateResponseDto;
import ru.mellingerie.facade.user.service.UserCreateFacadeService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCreateFacadeService userCreateFacadeService;

    @PostMapping("/guest")
    public ResponseEntity<UserCreateResponseDto> createGuestUser(@Valid @RequestBody UserCreateRequestDto request) {
        log.info("Получен запрос на создание пользователя-гостя");

        try {
            UserCreateResponseDto response = userCreateFacadeService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя-гостя: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 