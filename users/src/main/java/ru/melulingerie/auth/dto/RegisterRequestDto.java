package ru.melulingerie.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;
    
    @NotBlank(message = "Имя обязательно")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    private String firstName;
    
    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 50, message = "Фамилия не должна превышать 50 символов") 
    private String middleName;

    @NotBlank(message = "Отчество обязательно")
    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    private String lastName;

    @Pattern(regexp = "\\+7\\d{10}", message = "Телефон должен быть в формате +7XXXXXXXXXX")
    private String phoneNumber;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private String password;

    @NotNull(message = "UserId обязателен")
    private Long userId;
}
