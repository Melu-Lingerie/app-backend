package ru.melulingerie.api.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.cart.resource.CartResource;
import ru.melulingerie.facade.cart.dto.request.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.response.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.response.CartGetFacadeResponseDto;
import ru.melulingerie.facade.cart.service.CartAddItemFacadeService;
import ru.melulingerie.facade.cart.service.CartClearFacadeService;
import ru.melulingerie.facade.cart.service.CartGetFacadeService;
import ru.melulingerie.facade.cart.service.CartRemoveItemFacadeService;
import ru.melulingerie.facade.cart.service.CartUpdateQuantityFacadeService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController implements CartResource {

    private final CartGetFacadeService cartGetFacadeService;
    private final CartClearFacadeService cartClearFacadeService;
    private final CartAddItemFacadeService cartAddItemFacadeService;
    private final CartRemoveItemFacadeService cartRemoveItemFacadeService;
    private final CartUpdateQuantityFacadeService cartUpdateQuantityFacadeService;

    @Override
    public ResponseEntity<CartGetFacadeResponseDto> getCart(Long cartId) {
        CartGetFacadeResponseDto response = cartGetFacadeService.getCart(cartId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CartAddFacadeResponseDto> addItemToCart(Long cartId, CartAddFacadeRequestDto request) {
        CartAddFacadeResponseDto response = cartAddItemFacadeService.addItemToCart(cartId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> updateItemQuantity(Long cartId, Long itemId, Integer quantity) {
        cartUpdateQuantityFacadeService.updateItemQuantity(cartId, itemId, quantity);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeItemsFromCart(Long cartId, List<Long> itemId) {
        cartRemoveItemFacadeService.removeItemsFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Integer> clearCart(Long cartId) {
        return ResponseEntity.ok(cartClearFacadeService.clearCart(cartId));
    }
}