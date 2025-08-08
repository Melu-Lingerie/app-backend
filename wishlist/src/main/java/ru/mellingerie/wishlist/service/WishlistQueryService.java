package ru.mellingerie.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.wishlist.entity.WishlistItem;
import ru.mellingerie.wishlist.model.WishlistItemModel;
import ru.mellingerie.wishlist.model.WishlistModel;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistQueryService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService validationService;

    public WishlistModel getWishlist(Long userId) {
        validationService.validatePositiveIdOrThrow(userId);

        Long wishlistId = wishlistRepository.findByUserId(userId)
                .map(ru.mellingerie.wishlist.entity.Wishlist::getId)
                .orElse(null);

        if (wishlistId == null) {
            return new WishlistModel(List.of(), 0);
        }

        List<WishlistItemModel> items = wishlistItemRepository.findAllByWishlistId(wishlistId)
                .stream()
                .map(this::toModel)
                .toList();
        return new WishlistModel(items, items.size());
    }

    private WishlistItemModel toModel(WishlistItem entity) {
        return new WishlistItemModel(entity.getId(), entity.getProductId(), entity.getVariantId(), entity.getAddedAt());
    }
}


