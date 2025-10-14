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
    //TODO эндпоинт изменения информации юзера
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

    @PostMapping("/verify-email") 
    public ResponseEntity<VerifyEmailResponseDto> verifyEmail(@RequestBody @Valid VerifyEmailRequestDto dto) {
        log.info("Получен запрос на верификацию email: {}", dto.getEmail());
        VerifyEmailResponseDto response = authService.verifyEmailAndComplete(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.resendVerificationCode(email);
        return ResponseEntity.ok(Map.of("message", "Код отправлен повторно"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody @Valid LogoutRequestDto dto) {
        log.info("Получен запрос на logout");
        
        authService.logout(dto.getRefreshToken());
        
        return ResponseEntity.ok(Map.of("message", "Вы успешно вышли из системы"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutAll(
            @RequestBody @Valid LogoutRequestDto dto) {
        log.info("Получен запрос на logout со всех устройств");
        
        authService.logoutFromAllDevices(dto.getRefreshToken());
        
        return ResponseEntity.ok(Map.of("message", "Вы успешно вышли из системы на всех устройствах"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDto> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestDto dto) {
        log.info("Получен запрос на сброс пароля для email: {}", dto.getEmail());
        ForgotPasswordResponseDto response = authService.requestPasswordReset(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDto dto) {
        log.info("Получен запрос на установку нового пароля для email: {}", dto.getEmail());
        authService.resetPassword(dto);
        return ResponseEntity.ok(Map.of(
            "message", "Пароль успешно изменен. Войдите с новым паролем",
            "email", dto.getEmail()
        ));
    }
}
