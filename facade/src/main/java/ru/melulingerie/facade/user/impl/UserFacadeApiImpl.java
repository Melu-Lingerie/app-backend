package ru.melulingerie.facade.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.facade.user.api.UserFacadeApi;
import ru.melulingerie.facade.user.api.UserCreateService;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;
import ru.melulingerie.facade.user.service.CartCreateService;
import ru.melulingerie.facade.user.service.WishlistCreateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacadeApiImpl implements UserFacadeApi {
    
    private final UserFacadeMapper userFacadeMapper;
    private final UserCreateService userCreateService;
    
    // Заглушки для сервисов - в реальной реализации будут инжектироваться из соответствующих модулей
    private final CartCreateService cartCreateService;
    private final WishlistCreateService wishlistCreateService;
    
    @Override
    @Transactional
    public UserCreateFacadeResponseDto createGuestUser(UserCreateFacadeRequestDto request) {
        log.info("Создание гостевого пользователя через фасад для sessionId: {}", request.getSessionId());
        
        try {
            // 1. Маппинг фасадного DTO во внутренний DTO
            var internalRequest = userFacadeMapper.facadeDtoToInternalDto(request);
            
            // 2. Создание пользователя через основной сервис
            var userResponse = userCreateService.createUser(internalRequest);
            
            // 3. Создание корзины для пользователя
            Long cartId = cartCreateService.createCart(userResponse.getUserId());
            
            // 4. Создание списка желаний для пользователя
            Long wishlistId = wishlistCreateService.createWishlist(userResponse.getUserId());
            
            // 5. Построение полного ответа
            UserCreateFacadeResponseDto response = UserCreateFacadeResponseDto.builder()
                    .userId(userResponse.getUserId())
                    .userSessionId(userResponse.getUserSessionId())
                    .userDeviceId(userResponse.getUserDeviceId())
                    .cartId(cartId)
                    .wishlistId(wishlistId)
                    .createdAt(java.time.LocalDateTime.now())
                    .sessionStatus("ACTIVE")
                    .build();
            
            log.info("Гостевой пользователь успешно создан с ID: {}", response.getUserId());
            return response;
            
        } catch (Exception e) {
            log.error("Ошибка при создании гостевого пользователя: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать гостевого пользователя", e);
        }
    }
}
