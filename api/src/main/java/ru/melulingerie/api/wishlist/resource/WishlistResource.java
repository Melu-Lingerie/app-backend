package ru.melulingerie.api.wishlist.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.wishlist.dto.WishlistApiListItemsResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiResponseDto;

@RequestMapping("/api/v1/wishlist")
public interface WishlistResource {

    @GetMapping("/{userId}")
    ResponseEntity<WishlistApiListItemsResponseDto> getWishlist(@PathVariable Long userId);

    @PostMapping("/{userId}/items")
    ResponseEntity<WishlistApiResponseDto> addItemToWishlist(@PathVariable Long userId, @RequestBody WishlistApiRequestDto request);

    @DeleteMapping("/{userId}/items/{itemId}")
    ResponseEntity<Void> removeItemFromWishlist(@PathVariable Long userId, @PathVariable Long itemId);

    @DeleteMapping("/{userId}")
    ResponseEntity<Void> clearWishlist(@PathVariable Long userId);
}

