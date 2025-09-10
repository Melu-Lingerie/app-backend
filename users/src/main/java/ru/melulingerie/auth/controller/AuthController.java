package ru.melulingerie.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.auth.dto.*;
import ru.melulingerie.auth.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        LoginResponseDto res = authService.login(dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
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
    public ResponseEntity<LoginResponseDto> verifyEmail(@RequestBody @Valid VerifyEmailRequestDto dto) {
        LoginResponseDto response = authService.verifyEmailAndComplete(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.resendVerificationCode(email);
        return ResponseEntity.ok(Map.of("message", "Код отправлен повторно"));
    }
}
