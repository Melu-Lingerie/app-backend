package ru.melulingerie.facade.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "UserUpdateFacadeResponseDto", description = "Ответ на обновление данных пользователя")
public record UserUpdateFacadeResponseDto(
        
        @Schema(description = "ID пользователя", example = "1")
        Long id,
        
        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,
        
        @Schema(description = "Отчество пользователя", example = "Иванович")
        String middleName,
        
        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName,
        
        @Schema(description = "Email пользователя", example = "ivan@example.com")
        String email,
        
        @Schema(description = "Номер телефона", example = "+79991234567")
        String phoneNumber,
        
        @Schema(description = "Дата рождения", example = "1990-01-15")
        LocalDate birthDate
) {
}

