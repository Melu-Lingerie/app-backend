package ru.mellingerie.wishlist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.wishlist.entity.Wishlist;
import ru.mellingerie.wishlist.entity.WishlistItem;
import ru.mellingerie.wishlist.exception.WishlistExceptions.InvalidIdException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistCapacityExceededException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemDuplicateException;
import ru.mellingerie.wishlist.model.AddToWishlistModel;
import ru.mellingerie.wishlist.model.AddToWishlistResponseModel;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WishlistItemAddService {

    private static final int MAX_ITEMS = 200;

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService validationService;

    @Transactional
    public AddToWishlistResponseModel add(Long userId, AddToWishlistModel request) {
        validationService.validatePositiveIdOrThrow(userId);

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidIdException(userId));
        Long wishlistId = wishlist.getId();

        int currentCount = wishlistItemRepository.findAllByWishlistId(wishlistId).size();
        validationService.validateCapacityNotExceeded(currentCount, MAX_ITEMS);

        Long productId = request.productId();
        Long variantId = request.variantId();

        boolean duplicateExists = wishlistItemRepository.findDuplicate(wishlistId, productId, variantId).isPresent();
        validationService.validateDuplicateAbsent(duplicateExists, productId, variantId);

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProductId(productId);
        item.setVariantId(variantId);
        item.setAddedAt(LocalDateTime.now());

        var saved = wishlistItemRepository.save(item);
        return new AddToWishlistResponseModel(saved.getId(), "Added to wishlist");
    }
}


