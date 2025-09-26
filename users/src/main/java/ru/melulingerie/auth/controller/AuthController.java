package ru.melulingerie.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.auth.dto.*;
import ru.melulingerie.auth.service.AuthService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
//TODO эедпоинт logout , эндпоинт изменения информации юзера
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @CookieValue(name = "sessionId") UUID sessionId,
            @RequestBody @Valid LoginRequestDto dto) {
        log.info("Получен запрос на логин с sessionId из cookie: {}", sessionId);
        dto.setSessionId(sessionId);
        LoginResponseDto res = authService.login(dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshResponseDto> refresh(@RequestBody @Valid RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.refreshAccessToken(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto dto) {
        authService.registerUser(dto);
        RegisterResponseDto response = RegisterResponseDto.builder()
                .message("Код подтверждения отправлен на email: " + dto.getEmail())
                .email(dto.getEmail())
                .codeExpiresInMinutes(15)
                .build();
        return ResponseEntity.ok(response);
    }

    //TODO подумать над тем что бы пользователя сразу пускать на сайт после подтверждения емэила
    @PostMapping("/verify-email") 
    public ResponseEntity<LoginResponseDto> verifyEmail(
            @CookieValue(name = "sessionId") UUID sessionId,
            @RequestBody @Valid VerifyEmailRequestDto dto) {
        log.info("Получен запрос на верификацию email с sessionId из cookie: {}", sessionId);
        dto.setSessionId(sessionId);
        LoginResponseDto response = authService.verifyEmailAndComplete(dto);
        return ResponseEntity.ok(response);
    }
    //TODO проверить на работоспособность
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.resendVerificationCode(email);
        return ResponseEntity.ok(Map.of("message", "Код отправлен повторно"));
    }
}
