package ru.melulingerie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.dto.CartAddItemRequestDto;
import ru.melulingerie.dto.CartAddItemResponseDto;
import ru.melulingerie.exception.CartExceptions;
import ru.melulingerie.service.CartAddItemService;
import ru.melulingerie.util.CartValidator;
import ru.melulingerie.util.ProductPriceService;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Сервис для добавления товаров в корзину.
 * Поддерживает обновление количества существующих товаров и добавление новых товаров.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartAddItemServiceImpl implements CartAddItemService {

    @Value("${cart.max-items:100}")
    private int maxItemsPerCart;

    @Value("${cart.max-quantity-per-item:99}")
    private int maxQuantityPerItem;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductPriceService productPriceService;
    private final CartValidator cartValidator;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CartAddItemResponseDto addCartItem(Long cartId, CartAddItemRequestDto request) {
        log.debug("Adding item to cart: cartId={}, productId={}, variantId={}, quantity={}", 
                  cartId, request.productId(), request.variantId(), request.quantity());

        // Валидация входных данных
        cartValidator.validateCartId(cartId);
        cartValidator.validateAddItemRequest(request);

        // Поиск корзины с активными элементами
        Cart cart = cartRepository.findByIdWithActiveItems(cartId)
                .orElseThrow(() -> new CartExceptions.CartNotFoundException(cartId));

        // Поиск существующего элемента с тем же продуктом
        Optional<CartItem> existingItem = cartItemRepository
                .findActiveByCartAndProduct(cartId, request.productId(), request.variantId());

        return existingItem.map(cartItem -> updateExistingItem(cartItem, request))
                .orElseGet(() -> addNewItem(cart, request));
    }

    /**
     * Обновление количества существующего товара в корзине
     */
    private CartAddItemResponseDto updateExistingItem(CartItem existingItem, CartAddItemRequestDto request) {
        log.debug("Updating existing cart item: itemId={}, currentQuantity={}, additionalQuantity={}", 
                  existingItem.getId(), existingItem.getQuantity(), request.quantity());

        int newQuantity = existingItem.getQuantity() + request.quantity();
        
        if (newQuantity > maxQuantityPerItem) {
            throw new CartExceptions.InvalidQuantityException(newQuantity);
        }

        BigDecimal currentPrice = productPriceService.getCurrentPrice(
                existingItem.getProductId(), existingItem.getVariantId());
        
        existingItem.updateUnitPrice(currentPrice);
        existingItem.updateQuantity(newQuantity);

        cartItemRepository.save(existingItem);
        
        updateCartTotalAmount(existingItem.getCart());

        return new CartAddItemResponseDto(existingItem.getId(), "Quantity updated in cart");
    }

    /**
     * Добавление нового товара в корзину
     */
    private CartAddItemResponseDto addNewItem(Cart cart, CartAddItemRequestDto request) {
        log.debug("Adding new item to cart: cartId={}, activeItemsCount={}", 
                  cart.getId(), cart.getItemsCount());

        // Проверка ограничения на количество товаров в корзине
        if (cart.getItemsCount() >= maxItemsPerCart) {
            throw new CartExceptions.CartFullException(maxItemsPerCart);
        }

        // Проверка максимального количества товара
        if (request.quantity() > maxQuantityPerItem) {
            throw new CartExceptions.InvalidQuantityException(request.quantity());
        }

        // Получение текущей цены товара
        BigDecimal unitPrice = productPriceService.getCurrentPrice(
                request.productId(), request.variantId());

        // Создание нового элемента корзины
        CartItem newItem = new CartItem(cart, request.productId(), request.variantId(), 
                                       request.quantity(), unitPrice);

        try {
            CartItem savedItem = cartItemRepository.save(newItem);
            
            // Пересчет общей суммы корзины
            updateCartTotalAmount(cart);
            
            log.info("Successfully added new item to cart: cartId={}, itemId={}, productId={}", 
                     cart.getId(), savedItem.getId(), request.productId());

            return new CartAddItemResponseDto(savedItem.getId(), "Added to cart");
            
        } catch (DataIntegrityViolationException ex) {
            log.warn("Concurrent attempt to add duplicate item to cart: cartId={}, productId={}, variantId={}", 
                     cart.getId(), request.productId(), request.variantId());
            throw new IllegalArgumentException("Item was already added to cart by another process", ex);
        }
    }

    /**
     * Обновление общей суммы корзины
     */
    private void updateCartTotalAmount(Cart cart) {
        cart.recalculateTotalAmount();
        cartRepository.save(cart);
        
        log.debug("Updated cart total amount: cartId={}, totalAmount={}", 
                  cart.getId(), cart.getTotalAmount());
    }
}