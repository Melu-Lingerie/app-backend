package ru.melulingerie.facade.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mellingerie.users.entity.SessionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateFacadeResponseDto {

    private Long userId;
    private Long userSessionId;
    private Long userDeviceId;
    private Long cartId;
    private Long wishlistId;
    private LocalDateTime createdAt;
    private SessionStatus sessionStatus;
    private LocalDateTime sessionExpiresAt;
}
