package ru.melulingerie.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailResponseDto {
    private Long userId;
    private boolean isVerified;
}
