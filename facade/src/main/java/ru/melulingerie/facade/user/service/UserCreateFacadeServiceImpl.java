package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;
import ru.mellingerie.users.service.UserCreateService;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateFacadeServiceImpl implements UserCreateFacadeService {

    private final UserCreateService userCreateService;
    private final UserFacadeMapper userFacadeMapper;

    @Override
    @Transactional
    public UserCreateFacadeResponseDto createGuestUser(UserCreateFacadeRequestDto request, String sessionId) {
        log.info("Создание гостевого пользователя с sessionId: {}", sessionId);

        // 1. Маппинг facade DTO в users DTO
        UserCreateRequestDto usersRequest = userFacadeMapper.facadeDtoToUsersDto(request, sessionId);

        // 2. Создание пользователя через модуль users (доменный слой)
        UserCreateResponseDto usersResponse = userCreateService.createGuestUser(usersRequest);

        // 3-4. Корзина и wishlist не реализованы, ожидают создания данных модулей и будут создаваться при создании новой сессии
        //TODO реализовать создание корзины и вишлиста здесь или в userService
        Long cartId = null;
        Long wishlistId = null;

        // 5. Маппинг ответа обратно в facade DTO
        UserCreateFacadeResponseDto facadeResponse =
                userFacadeMapper.usersDtoToFacadeDto(usersResponse);

        log.info(
                "Гостевой пользователь создан успешно. userId: {}, cartId: {}, wishlistId: {}",
                facadeResponse.userId(),
                facadeResponse.cartId(),
                facadeResponse.wishlistId()
        );

        return facadeResponse;
    }
}
