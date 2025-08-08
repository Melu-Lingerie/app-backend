package ru.mellingerie.wishlist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.wishlist.exception.WishlistExceptions.InvalidIdException;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

@Service
@RequiredArgsConstructor
public class WishlistClearService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService validationService;

    @Transactional
    public void clear(Long userId) {
        validationService.validatePositiveIdOrThrow(userId);
        Long wishlistId = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidIdException(userId))
                .getId();
        wishlistItemRepository.deleteAllByWishlistId(wishlistId);
    }
}


