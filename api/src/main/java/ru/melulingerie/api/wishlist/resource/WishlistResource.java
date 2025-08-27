package ru.melulingerie.api.wishlist.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;

import java.util.List;

@RequestMapping("/api/v1/wishlist/{wishlistId}")
public interface WishlistResource {

    @GetMapping
    ResponseEntity<WishlistGetFacadeResponseDto> getWishlist(@PathVariable("wishlistId") Long wishlistId);

    @PostMapping("/items")
    ResponseEntity<WishlistAddFacadeResponseDto> addItemToWishlist(@PathVariable("wishlistId") Long wishlistId, @RequestBody WishlistAddFacadeRequestDto request);

    @DeleteMapping("/items")
    ResponseEntity<Void> removeItemsFromWishlist(@PathVariable("wishlistId") Long wishlistId, @RequestBody List<Long> itemId);

    @DeleteMapping
    ResponseEntity<Integer> clearWishlist(@PathVariable("wishlistId") Long wishlistId);
}