package ru.melulingerie.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto {
    @NotBlank(message = "Refresh token обязателен")
    private String refreshToken;
}

