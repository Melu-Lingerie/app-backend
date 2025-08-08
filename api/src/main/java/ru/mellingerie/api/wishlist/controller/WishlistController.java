package ru.mellingerie.api.wishlist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mellingerie.api.wishlist.resource.WishlistResource;
import ru.mellingerie.facade.wishlist.dto.*;
import ru.mellingerie.facade.wishlist.service.WishlistFacadeService;

@RestController
@RequiredArgsConstructor
public class WishlistController implements WishlistResource {

    private final WishlistFacadeService wishlistFacadeService;

    @Override
    public ResponseEntity<WishlistResponseDto> list(Long userId) {
        return ResponseEntity.ok(wishlistFacadeService.getWishlist(userId));
    }

    @Override
    public ResponseEntity<AddToWishlistResponseDto> add(Long userId, AddToWishlistRequestDto request) {
        return ResponseEntity.ok(wishlistFacadeService.add(userId, request));
    }

    @Override
    public ResponseEntity<Void> remove(Long userId, Long id) {
        wishlistFacadeService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> clear(Long userId) {
        wishlistFacadeService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}


