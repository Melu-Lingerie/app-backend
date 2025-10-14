package ru.melulingerie.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;
    
    @NotBlank(message = "Код подтверждения обязателен")
    @Pattern(regexp = "\\d{6}", message = "Код должен содержать 6 цифр")
    private String code;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String newPassword;
}

