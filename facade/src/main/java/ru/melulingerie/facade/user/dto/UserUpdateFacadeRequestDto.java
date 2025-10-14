package ru.melulingerie.facade.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(name = "UserUpdateFacadeRequestDto", description = "Запрос на обновление данных пользователя")
public record UserUpdateFacadeRequestDto(
        
        @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Имя обязательно")
        @Size(max = 50, message = "Имя не должно превышать 50 символов")
        String firstName,
        
        @Schema(description = "Отчество пользователя", example = "Иванович", nullable = true)
        @Size(max = 50, message = "Отчество не должно превышать 50 символов")
        String middleName,
        
        @Schema(description = "Фамилия пользователя", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
        String lastName,
        
        @Schema(description = "Email пользователя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,
        
        @Schema(description = "Номер телефона", example = "+79991234567", nullable = true)
        @Pattern(regexp = "\\+7\\d{10}", message = "Телефон должен быть в формате +7XXXXXXXXXX")
        String phoneNumber,
        
        @Schema(description = "Дата рождения", example = "1990-01-15", nullable = true)
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate
) {
}

