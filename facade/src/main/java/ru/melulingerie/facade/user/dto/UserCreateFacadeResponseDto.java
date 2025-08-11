package ru.melulingerie.facade.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateFacadeResponseDto {
    
    private Long userId;
    private UUID sessionId;
    private String status;
    private String role;
    private LocalDateTime createdAt;
}
