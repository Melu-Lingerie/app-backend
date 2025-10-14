package ru.melulingerie.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
    
    // SessionId устанавливается в контроллере из cookie
    private UUID sessionId;
}