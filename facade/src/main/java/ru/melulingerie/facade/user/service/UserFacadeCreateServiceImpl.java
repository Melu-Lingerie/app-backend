package ru.melulingerie.facade.user.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.service.UserCreateService;
import ru.melulingerie.facade.user.api.UserFacadeCreateService;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacadeCreateServiceImpl implements UserFacadeCreateService {

    private final UserCreateService userCreateService;
    private final UserFacadeMapper userFacadeMapper;

    @Override
    @Transactional
    public UserCreateFacadeResponseDto createUser(
            UserCreateFacadeRequestDto request,
            HttpServletRequest httpRequest
    ) {
        log.info("Создание гостевого пользователя с sessionId: {}", request.getSessionId());

        // 1. Маппинг facade DTO в users DTO
        UserCreateRequestDto usersRequest = userFacadeMapper.facadeDtoToUsersDto(request);

        // 2. Добавляем IP адрес из HTTP запроса
        String ipAddress = extractIpAddress(httpRequest);
        usersRequest.setIpAddress(ipAddress);

        // 3. Создание пользователя через модуль users (доменный слой)
        UserCreateResponseDto usersResponse = userCreateService.createUser(usersRequest);

        // 4-5. Корзина и wishlist пока не создаются (заглушки удалены)
        Long cartId = null;
        Long wishlistId = null;

        // 6. Маппинг ответа обратно в facade DTO
        UserCreateFacadeResponseDto facadeResponse =
                userFacadeMapper.usersDtoToFacadeDto(usersResponse, cartId, wishlistId);

        log.info(
                "Гостевой пользователь создан успешно. userId: {}, cartId: {}, wishlistId: {}",
                facadeResponse.getUserId(),
                facadeResponse.getCartId(),
                facadeResponse.getWishlistId()
        );

        return facadeResponse;
    }
    //TODO проаналитить
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
