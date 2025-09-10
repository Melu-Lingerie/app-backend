package ru.melulingerie.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequestDto {
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;
    
    @NotBlank(message = "Код подтверждения обязателен")
    @Pattern(regexp = "\\d{6}", message = "Код должен состоять из 6 цифр")
    private String code;
}
