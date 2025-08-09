package ru.mellingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.exceptions.wishlist.WishlistExceptions;
import ru.mellingerie.facade.wishlist.dto.AddToWishlistRequestDto;
import ru.mellingerie.facade.wishlist.dto.AddToWishlistResponseDto;
import ru.mellingerie.facade.wishlist.dto.WishlistResponseDto;
import ru.mellingerie.facade.wishlist.mapper.WishlistFacadeMapper;
import ru.mellingerie.wishlist.service.WishlistClearService;
import ru.mellingerie.wishlist.service.WishlistItemAddService;
import ru.mellingerie.wishlist.service.WishlistItemRemoveService;
import ru.mellingerie.wishlist.service.WishlistQueryService;

@Service
@RequiredArgsConstructor
public class WishlistFacadeServiceImpl implements WishlistFacadeService {
    private final WishlistQueryService wishlistQueryService;
    private final WishlistItemAddService wishlistItemAddService;
    private final WishlistItemRemoveService wishlistItemRemoveService;
    private final WishlistClearService wishlistClearService;
    private final WishlistFacadeMapper mapper;

    @Override
    public WishlistResponseDto getWishlist(Long userId) {
        try {
            return mapper.toDto(wishlistQueryService.getWishlist(userId));
        } catch (WishlistExceptions.WishListInvalidIdException e) {
            throw new WishlistExceptions.WishListInvalidIdException(userId);
        }
    }

    @Override
    public AddToWishlistResponseDto add(Long userId, AddToWishlistRequestDto request) {
        try {
            return mapper.toDto(wishlistItemAddService.add(userId, mapper.toCore(request)));
        } catch (WishlistExceptions.WishListInvalidIdException e) {
            throw new WishlistExceptions.WishListInvalidIdException(userId);
        } catch (WishlistExceptions.WishlistItemDuplicateException e) {
            throw new ru.mellingerie.exceptions.wishlist.WishlistExceptions.WishlistItemDuplicateException(
                    request.productId(), request.variantId()
            );
        } catch (WishlistExceptions.WishlistCapacityExceededException e) {
            throw new WishlistExceptions.WishlistCapacityExceededException(200);
        }
    }

    @Override
    public void remove(Long userId, Long itemId) {
        try {
            wishlistItemRemoveService.remove(userId, itemId);
        } catch (WishlistExceptions.WishListInvalidIdException e) {
            throw new WishlistExceptions.WishListInvalidIdException(itemId);
        } catch (WishlistExceptions.WishlistItemNotFoundException e) {
            throw new ru.mellingerie.exceptions.wishlist.WishlistExceptions.WishlistItemNotFoundException(itemId);
        }
    }

    @Override
    public void clear(Long userId) {
        try {
            wishlistClearService.clear(userId);
        } catch (WishlistExceptions.WishListInvalidIdException e) {
            throw new WishlistExceptions.WishListInvalidIdException(userId);
        }
    }
}


