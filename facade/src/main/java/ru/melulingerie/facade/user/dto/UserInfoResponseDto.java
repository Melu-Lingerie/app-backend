package ru.melulingerie.facade.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "UserInfoResponseDto", description = "Информация о пользователе")
public record UserInfoResponseDto(
        
        @Schema(description = "Имя", example = "Анна")
        String firstName,
        
        @Schema(description = "Отчество", example = "Сергеевна")
        String middleName,
        
        @Schema(description = "Фамилия", example = "Иванова")
        String lastName,
        
        @Schema(description = "Email", example = "anna@example.com")
        String email,
        
        @Schema(description = "Номер телефона", example = "+79991234567")
        String phoneNumber,
        
        @Schema(description = "Дата рождения", example = "1990-01-15")
        LocalDate birthDate
) {}

