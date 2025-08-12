package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateService {
    
    private final ru.mellingerie.users.service.UserCreateService usersUserCreateService;
    private final UserFacadeMapper userFacadeMapper;
    private final CartCreateService cartCreateService;
    private final WishlistCreateService wishlistCreateService;
    
    @Transactional
    public UserCreateFacadeResponseDto createGuestUser(UserCreateFacadeRequestDto request, HttpServletRequest httpRequest) {
        log.info("Создание гостевого пользователя для sessionId: {}", request.getSessionId());
        
        // 1. Маппинг facade DTO в users DTO
        UserCreateRequestDto usersRequest = userFacadeMapper.facadeDtoToUsersDto(request);
        
        // 2. Добавляем IP адрес из HTTP запроса
        String ipAddress = extractIpAddress(httpRequest);
        usersRequest.setIpAddress(ipAddress);
        
        // 3. Создание пользователя через модуль users
        UserCreateResponseDto usersResponse = usersUserCreateService.createUser(usersRequest);
        
        // 4. Создание корзины для пользователя
        Long cartId = cartCreateService.createCart(usersResponse.getUserId());
        
        // 5. Создание списка желаний для пользователя
        Long wishlistId = wishlistCreateService.createWishlist(usersResponse.getUserId());
        
        // 6. Маппинг ответа обратно в facade DTO
        UserCreateFacadeResponseDto facadeResponse = userFacadeMapper.usersDtoToFacadeDto(usersResponse, cartId, wishlistId);
        
        log.info("Гостевой пользователь создан успешно. userId: {}, cartId: {}, wishlistId: {}", 
                facadeResponse.getUserId(), facadeResponse.getCartId(), facadeResponse.getWishlistId());
        
        return facadeResponse;
    }
    
    /**
     * Извлекает IP адрес из HTTP запроса
     */
    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
