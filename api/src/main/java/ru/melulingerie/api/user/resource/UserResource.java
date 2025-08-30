package ru.melulingerie.api.user.resource;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

import java.util.UUID;

@RequestMapping("/api/v1/users")
public interface UserResource {

    @PostMapping("/guests")
    ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @CookieValue(name = "sessionId")
            UUID sessionId,
            @Valid @RequestBody UserCreateFacadeRequestDto request);
}