package ru.melulingerie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.service.WishlistCreateService;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.domain.Wishlist;
import ru.melulingerie.repository.WishlistRepository;

/**
 * Доменный сервис для создания wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistCreateServiceImpl implements WishlistCreateService {

    private final WishlistRepository wishlistRepository;
    private final WishlistValidator validationService;

    @Override
    public Long createWishlistForUser(Long userId) {
        log.info("Starting create wishlist for userId: {}", userId);

        validationService.validatePositiveIdOrThrow(userId);

        return wishlistRepository.findByUserId(userId)
                .map(existingWishlist -> {
                    log.debug("Wishlist already exists for userId: {}, wishlistId: {}", userId, existingWishlist.getId());
                    return existingWishlist.getId();
                })
                .orElseGet(() -> {
                    Wishlist newWishlist = createNewWishlist(userId);
                    log.info("New wishlist created for userId: {}, wishlistId: {}", userId, newWishlist.getId());
                    return newWishlist.getId();
                });
    }

    private Wishlist createNewWishlist(Long userId) {
        log.debug("Creating new wishlist for userId: {}", userId);

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);

        return wishlistRepository.save(wishlist);
    }
}