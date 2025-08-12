package ru.mellingerie.api.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.user.api.UserFacadeApi;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements ru.mellingerie.api.user.resource.UserResource {
    
    private final UserFacadeApi userFacadeApi;
    
    @Override
    @PostMapping("/guests")
    public ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @Valid @RequestBody UserCreateFacadeRequestDto request,
            HttpServletRequest httpRequest) {
        
        log.info("Получен запрос на создание гостевого пользователя для sessionId: {}", request.getSessionId());
        
        try {
            // Извлечение IP адреса из запроса
            String ipAddress = getClientIpAddress(httpRequest);
            log.debug("IP адрес клиента: {}", ipAddress);
            
            // Создание гостевого пользователя через фасад
            UserCreateFacadeResponseDto response = userFacadeApi.createGuestUser(request);
            
            log.info("Гостевой пользователь успешно создан с ID: {}", response.getUserId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при создании гостевого пользователя: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(UserCreateFacadeResponseDto.builder()
                            .userId(null)
                            .userSessionId(null)
                            .userDeviceId(null)
                            .cartId(null)
                            .wishlistId(null)
                            .createdAt(null)
                            .sessionStatus(null)
                            .build());
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}