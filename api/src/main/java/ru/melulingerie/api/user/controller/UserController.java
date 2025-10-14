package ru.melulingerie.api.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.user.resource.UserResource;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.dto.UserInfoResponseDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeResponseDto;
import ru.melulingerie.facade.user.service.UserCreateFacadeService;
import ru.melulingerie.facade.user.service.UserInfoFacadeService;
import ru.melulingerie.facade.user.service.UserUpdateFacadeService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserResource {

    private final UserCreateFacadeService userCreateFacadeService;
    private final UserInfoFacadeService userInfoFacadeService;
    private final UserUpdateFacadeService userUpdateFacadeService;

    @Override
    public ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(UUID sessionId, UserCreateFacadeRequestDto request) {
        log.info("Получен запрос на создание гостевого пользователя с sessionId: {}", sessionId);
        UserCreateFacadeRequestDto requestDto = new UserCreateFacadeRequestDto(sessionId, request.deviceInfo());
        return ResponseEntity.ok(userCreateFacadeService.createGuestUser(requestDto));
    }

    @Override
    public ResponseEntity<UserInfoResponseDto> getCurrentUserInfo(Authentication authentication) {
        log.info("Получен запрос на получение информации о пользователе");
        return ResponseEntity.ok(userInfoFacadeService.getUserInfo(authentication));
    }

    @Override
    public ResponseEntity<UserUpdateFacadeResponseDto> updateCurrentUser(Authentication authentication, UserUpdateFacadeRequestDto request) {
        log.info("Получен запрос на обновление данных пользователя");
        return ResponseEntity.ok(userUpdateFacadeService.updateCurrentUser(authentication, request));
    }

    @GetMapping("test")
    public String test() {
        return "Auth test successfully =)";
    }
}