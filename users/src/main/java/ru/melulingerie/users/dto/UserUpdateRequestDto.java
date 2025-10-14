package ru.melulingerie.users.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserUpdateRequestDto(
        
        @NotBlank(message = "Имя обязательно")
        @Size(max = 50, message = "Имя не должно превышать 50 символов")
        String firstName,
        
        @Size(max = 50, message = "Отчество не должно превышать 50 символов")
        String middleName,
        
        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
        String lastName,
        
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,
        
        @Pattern(regexp = "\\+7\\d{10}", message = "Телефон должен быть в формате +7XXXXXXXXXX")
        String phoneNumber,
        
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate
) {
}

