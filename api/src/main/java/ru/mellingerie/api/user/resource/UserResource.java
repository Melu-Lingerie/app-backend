package ru.mellingerie.api.user.resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

@RequestMapping("/api/v1/users")
public interface UserResource {

    @PostMapping("/guests")
    ResponseEntity<UserCreateFacadeResponseDto> createUser(
            @Valid @RequestBody UserCreateFacadeRequestDto request,
            HttpServletRequest httpRequest);
}