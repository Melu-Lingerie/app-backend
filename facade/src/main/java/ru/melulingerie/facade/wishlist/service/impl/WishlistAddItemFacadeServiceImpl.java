package ru.melulingerie.facade.wishlist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.mapper.WishlistMapper;
import ru.melulingerie.facade.wishlist.service.WishlistAddItemFacadeService;
import ru.melulingerie.products.service.ProductService;
import ru.melulingerie.service.WishlistAddItemService;
import ru.melulingerie.service.WishlistCreateService;

/**
 * Фасадный сервис для добавления элементов в wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistAddItemFacadeServiceImpl implements WishlistAddItemFacadeService {

    private final ProductService productService;
    private final WishlistMapper wishlistMapper;
    private final WishlistAddItemService wishlistAddItemService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public WishlistAddFacadeResponseDto addItemToWishlist(Long wishlistId, WishlistAddFacadeRequestDto request) {
        validateProductExists(request.productId());
        validateProductVariantExists(request.variantId());
        
        WishlistAddItemRequestDto domainRequest = wishlistMapper.toModuleRequestDto(request);
        
        WishlistAddItemResponseDto response = transactionTemplate.execute(status ->
                wishlistAddItemService.addWishlistItem(wishlistId, domainRequest)
        );
        
        return wishlistMapper.toFacadeResponseDto(response);
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
}