package ru.melulingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.AddItemToWishlistRequestDto;
import ru.melulingerie.dto.AddItemToWishlistResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiResponseDto;
import ru.melulingerie.facade.wishlist.mapper.WishlistMapper;
import ru.melulingerie.facade.wishlist.mocks.ProductService;
import ru.melulingerie.facade.wishlist.mocks.UserService;
import ru.melulingerie.service.WishlistItemAddDomainService;

/**
 * Фасадный сервис для добавления элементов в wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistItemAddFacadeService {

    private final WishlistItemAddDomainService wishlistItemAddDomainService;
    private final WishlistMapper wishlistMapper;
    private final UserService userService;
    private final ProductService productService;

    public WishlistApiResponseDto addItemToWishlist(Long userId, WishlistApiRequestDto request) {
        // Междоменная валидация
        validateUserExists(userId);
        validateProductExists(request.productId());
        validateProductVariantExists(request.variantId());
        
        // Маппинг в доменный DTO
        AddItemToWishlistRequestDto domainRequest = wishlistMapper.toModuleRequestDto(request);
        
        // Делегация в доменный сервис
        AddItemToWishlistResponseDto domainResponse = wishlistItemAddDomainService.addWishlistItemToWishlist(userId, domainRequest);
        
        // Маппинг в API DTO
        return wishlistMapper.toFacadeResponseDto(domainResponse);
    }

    private void validateUserExists(Long userId) {
        userService.getUserById(userId).orElseThrow(() -> {
            log.warn("User not found for userId: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });
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
