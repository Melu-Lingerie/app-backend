package ru.mellingerie.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponseDto {

    private Long userId;
    private Long userSessionId;
    private Long userDeviceId;
    private LocalDateTime sessionExpiresAt;
}
