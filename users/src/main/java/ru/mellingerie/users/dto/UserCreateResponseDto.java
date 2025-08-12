package ru.mellingerie.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponseDto {
    
    private Long userId;
    private Long userSessionId;
    private Long userDeviceId;
}
