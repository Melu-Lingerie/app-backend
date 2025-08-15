package ru.mellingerie.api.user.resource;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

@RequestMapping("/api/v1/users")
public interface UserResource {

    @PostMapping("/guests")
    ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @CookieValue(name = "sessionId", required = false) String sessionIdCookie,
            @Valid @RequestBody UserCreateFacadeRequestDto request);
}