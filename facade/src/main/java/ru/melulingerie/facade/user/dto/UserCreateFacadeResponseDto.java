package ru.melulingerie.facade.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mellingerie.users.entity.SessionStatus;

import java.time.LocalDateTime;

@Data
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
}
