package ru.melulingerie.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordResponseDto {
    private String message;
    private String email;
    private Integer codeExpiresInMinutes;
}

