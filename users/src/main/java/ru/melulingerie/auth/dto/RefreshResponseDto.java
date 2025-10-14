package ru.melulingerie.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponseDto {
    private String accessToken;
    private Long accessTokenExpiresIn; // сек
    private String refreshToken;
    private Long refreshTokenExpiresIn;
}
