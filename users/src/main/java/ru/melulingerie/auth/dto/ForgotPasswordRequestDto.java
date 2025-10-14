package ru.melulingerie.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDto {
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;
}

