package ru.mellingerie.api.user.resource;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mellingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.mellingerie.facade.user.dto.UserCreateFacadeResponseDto;

@RequestMapping("/api/users")
public interface UserResource {

    @PostMapping("/guest")
    ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(@Valid @RequestBody UserCreateFacadeRequestDto request);
}