package ru.mellingerie.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mellingerie.cart.dto.*;
import ru.mellingerie.cart.service.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartCreateService cartCreateService;
    private final CartItemAddService cartItemAddService;
    private final CartQueryService cartQueryService;
    private final CartItemQuantityUpdateService cartItemQuantityUpdateService;
    private final CartItemRemoveService cartItemRemoveService;
    private final CartClearService cartClearService;

    @PostMapping
    public ResponseEntity<CreateCartResponse> createCart(@RequestHeader("X-User-Id") Long userId) {
        CreateCartResponse response = cartCreateService.createCart(userId);
        return ResponseEntity.status(response.isNewCart() ? 201 : 200).body(response);
    }

    @PostMapping("/items")
    public ResponseEntity<AddToCartResponse> addCartItemToCart(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddToCartRequest request) {
        AddToCartResponse response = cartItemAddService.addCartItemToCart(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("id") Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        cartItemQuantityUpdateService.updateCartItemQuantity(userId, cartItemId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("id") Long cartItemId) {
        cartItemRemoveService.removeCartItem(userId, cartItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CartView> getCart(@RequestHeader("X-User-Id") Long userId) {
        return cartQueryService.getCart(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<ClearCartResponse> clearCart(@RequestHeader("X-User-Id") Long userId) {
        ClearCartResponse response = cartClearService.clearCart(userId);
        return ResponseEntity.ok(response);
    }
}