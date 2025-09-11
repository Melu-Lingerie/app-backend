package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.facade.wishlist.service.WishlistCreateFacadeService;
import ru.melulingerie.users.dto.UserCreateRequestDto;
import ru.melulingerie.users.dto.UserCreateResponseDto;
import ru.melulingerie.users.service.UserCreateService;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateFacadeServiceImpl implements UserCreateFacadeService {

    private final UserCreateService userCreateService;
    private final UserFacadeMapper userFacadeMapper;
    private final WishlistCreateFacadeService wishlistCreateFacadeService;

    @Override
    @Transactional
    public UserCreateFacadeResponseDto createGuestUser(UserCreateFacadeRequestDto request) {
        log.info("Создание гостевого пользователя с sessionId: {}", request.sessionId());
        
        UserCreateRequestDto usersRequest = userFacadeMapper.facadeDtoToUsersDto(request);

        Long userId = userCreateService.createGuestUser(usersRequest);

        //TODO вызвать методы создания-получения корзины и вишлиста
        Long cartId = 123L;
        Long wishlistId = wishlistCreateFacadeService.createWishlistForUser(userId);

        UserCreateResponseDto usersResponse = new UserCreateResponseDto(userId, cartId, wishlistId);

        return userFacadeMapper.usersDtoToFacadeDto(usersResponse);
    }
}
