package ru.melulingerie.api.wishlist.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.wishlist.dto.GetWishlistListItemsResponseDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistRequestDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistResponseDto;

@RequestMapping("/api/v1/wishlist/{userId}")
public interface WishlistResource {

    @GetMapping
    ResponseEntity<GetWishlistListItemsResponseDto> getWishlist(@PathVariable("userId") Long userId);

    @PostMapping("/items")
    ResponseEntity<AddWishlistResponseDto> addItemToWishlist(@PathVariable("userId") Long userId, @RequestBody AddWishlistRequestDto request);

    @DeleteMapping("/items/{itemId}")
    ResponseEntity<Void> removeItemFromWishlist(@PathVariable("userId") Long userId, @PathVariable Long itemId);

    @DeleteMapping
    ResponseEntity<Void> clearWishlist(@PathVariable("userId") Long userId);
}

