package ru.melulingerie.auth.dto;

import lombok.*;
import ru.melulingerie.users.entity.UserRole;
import ru.melulingerie.users.entity.UserStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;

    private UserRole role;
    private UserStatus status;

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    private Long refreshTokenExpiresIn;
}