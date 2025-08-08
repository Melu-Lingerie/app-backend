package ru.mellingerie.api.wishlist.resource;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mellingerie.facade.wishlist.dto.*;

@RequestMapping("/api/v1/wishlist")
public interface WishlistResource {

    @GetMapping
    ResponseEntity<WishlistResponseDto> list(@RequestHeader("X-User-Id") Long userId);

    @PostMapping("/items")
    ResponseEntity<AddToWishlistResponseDto> add(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddToWishlistRequestDto request);

    @DeleteMapping("/items/{id}")
    ResponseEntity<Void> remove(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id);

    @DeleteMapping
    ResponseEntity<Void> clear(@RequestHeader("X-User-Id") Long userId);
}


