package ru.mellingerie.facade.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {
    
    @NotNull(message = "Session ID не может быть пустым")
    private UUID sessionId;
    
    @NotNull(message = "IP адрес не может быть пустым")
    private String ipAddress;
    
    @NotNull(message = "Данные устройства не могут быть пустыми")
    @Valid
    private UserDeviceDto userDevice;
}
