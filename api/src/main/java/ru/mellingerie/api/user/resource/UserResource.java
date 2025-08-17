package ru.mellingerie.api.user.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

@RequestMapping("/api/v1/users")
public interface UserResource {

    @PostMapping("/guests")
    ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @CookieValue(name = "sessionId")
            @NotBlank(message = "Session ID не может быть пустым")
            @Size(max = 100, message = "Session ID не может превышать 100 символов")
            @Pattern(regexp = "^[a-fA-F0-9-]{36}$", message = "Session ID должен быть в формате UUID")
            String sessionId,
            @Valid @RequestBody UserCreateFacadeRequestDto request);
}