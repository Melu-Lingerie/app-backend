package ru.mellingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.facade.wishlist.dto.*;
import ru.mellingerie.facade.wishlist.mapper.WishlistFacadeMapper;
import ru.mellingerie.facade.wishlist.exception.WishlistFacadeExceptions;
import ru.mellingerie.wishlist.exception.WishlistExceptions.InvalidIdException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemDuplicateException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemNotFoundException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistCapacityExceededException;
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
        } catch (InvalidIdException e) {
            throw new WishlistFacadeExceptions.InvalidIdException(userId);
        }
    }

    @Override
    public AddToWishlistResponseDto add(Long userId, AddToWishlistRequestDto request) {
        try {
            return mapper.toDto(wishlistItemAddService.add(userId, mapper.toCore(request)));
        } catch (InvalidIdException e) {
            throw new WishlistFacadeExceptions.InvalidIdException(userId);
        } catch (WishlistItemDuplicateException e) {
            throw new WishlistItemDuplicateException(request.productId(), request.variantId());
        } catch (WishlistCapacityExceededException e) {
            throw new WishlistFacadeExceptions.WishlistCapacityExceededException();
        }
    }

    @Override
    public void remove(Long userId, Long itemId) {
        try {
            wishlistItemRemoveService.remove(userId, itemId);
        } catch (InvalidIdException e) {
            throw new WishlistFacadeExceptions.InvalidIdException(itemId);
        } catch (WishlistItemNotFoundException e) {
            throw new WishlistFacadeExceptions.WishlistItemNotFoundException(itemId);
        }
    }

    @Override
    public void clear(Long userId) {
        try {
            wishlistClearService.clear(userId);
        } catch (InvalidIdException e) {
            throw new WishlistFacadeExceptions.InvalidIdException(userId);
        }
    }
}


