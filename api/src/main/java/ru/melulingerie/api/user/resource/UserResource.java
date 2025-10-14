package ru.melulingerie.api.user.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.dto.UserInfoResponseDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeResponseDto;

import java.util.UUID;

@Tag(name = "User Management", description = "API для управления пользователями")
@RequestMapping("/api/v1/users")
public interface UserResource {

    @Operation(summary = "Создать гостевого пользователя", description = "Создает нового гостевого пользователя с уникальной сессией")
    @PostMapping("/guests")
    ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @CookieValue(name = "sessionId")
            UUID sessionId,
            @Valid @RequestBody UserCreateFacadeRequestDto request);

    @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает данные авторизованного пользователя")
    @GetMapping("/info")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<UserInfoResponseDto> getCurrentUserInfo(Authentication authentication);

    @Operation(summary = "Обновить данные текущего пользователя", description = "Обновляет личные данные авторизованного пользователя")
    @PutMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<UserUpdateFacadeResponseDto> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserUpdateFacadeRequestDto request);
}