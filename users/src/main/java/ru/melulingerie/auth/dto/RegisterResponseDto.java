package ru.melulingerie.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDto {
    private String message;
    private String email;
    private long codeExpiresInMinutes;
}
