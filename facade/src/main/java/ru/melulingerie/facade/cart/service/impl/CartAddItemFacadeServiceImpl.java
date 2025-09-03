package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.dto.CartAddItemRequestDto;
import ru.melulingerie.dto.CartAddItemResponseDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartAddItemFacadeService;
import ru.melulingerie.facade.wishlist.mocks.ProductService;
import ru.melulingerie.service.CartAddItemService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartAddItemFacadeServiceImpl implements CartAddItemFacadeService {

    private final ProductService productService;
    private final CartMapper cartMapper;
    private final CartAddItemService cartAddItemService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public CartAddFacadeResponseDto addItemToCart(Long cartId, CartAddFacadeRequestDto request) {
        validateProductExists(request.productId());
        validateProductVariantExists(request.variantId());
        validateQuantity(request.quantity());
        
        CartAddItemRequestDto domainRequest = cartMapper.toModuleRequestDto(request);
        
        CartAddItemResponseDto response = transactionTemplate.execute(status ->
                cartAddItemService.addCartItem(cartId, domainRequest)
        );
        
        return cartMapper.toFacadeResponseDto(response);
    }

    private void validateProductExists(Long productId) {
        productService.getProductById(productId).orElseThrow(() -> {
            log.warn("Product not found for productId: {}", productId);
            return new IllegalArgumentException("Product not found with id: " + productId);
        });
    }

    private void validateProductVariantExists(Long variantId) {
        productService.getProductVariantById(variantId).orElseThrow(() -> {
            log.warn("Product variant not found for variantId: {}", variantId);
            return new IllegalArgumentException("Product variant not found with id: " + variantId);
        });
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be positive number");
        }
    }
}