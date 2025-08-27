package ru.melulingerie.facade.wishlist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.dto.WishlistGetResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;
import ru.melulingerie.facade.wishlist.mapper.WishlistMapper;
import ru.melulingerie.facade.wishlist.mocks.UserService;
import ru.melulingerie.facade.wishlist.service.WishlistGetFacadeService;
import ru.melulingerie.service.WishlistGetService;

/**
 * Фасадный сервис для получения wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistGetFacadeServiceImpl implements WishlistGetFacadeService {

    private final WishlistMapper wishlistMapper;
    private final WishlistGetService wishlistGetService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public WishlistGetFacadeResponseDto getWishlist(Long wishlistId) {
        WishlistGetResponseDto response = transactionTemplate.execute(status ->
                wishlistGetService.getWishlist(wishlistId));

        return wishlistMapper.toFacadeWishListResponseDto(response);
    }
}