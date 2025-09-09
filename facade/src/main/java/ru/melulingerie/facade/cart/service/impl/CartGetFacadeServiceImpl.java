package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.dto.response.CartGetResponseDto;
import ru.melulingerie.cart.dto.response.CartItemGetResponseDto;
import ru.melulingerie.facade.cart.dto.response.CartGetFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.response.CartItemWithPriceFacadeResponseDto;
import ru.melulingerie.facade.cart.service.CartGetFacadeService;
import ru.melulingerie.cart.service.CartGetService;
import ru.melulingerie.price.service.PriceService;
import ru.melulingerie.products.service.impl.ProductVariantService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartGetFacadeServiceImpl implements CartGetFacadeService {

    private final PriceService priceService;
    private final CartGetService cartGetService;
    private final ProductVariantService productVariantService;

    @Override
    public CartGetFacadeResponseDto getCart(Long cartId) {
        log.debug("Getting cart for cartId: {}", cartId);
        
        CartGetResponseDto domainResponse = cartGetService.getCart(cartId);
        
        List<CartItemWithPriceFacadeResponseDto> itemsWithPrices = domainResponse.items().stream()
                .map(this::enrichCartItemWithPrice)
                .toList();
        
        BigDecimal totalAmount = itemsWithPrices.stream()
                .map(CartItemWithPriceFacadeResponseDto::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new CartGetFacadeResponseDto(
                itemsWithPrices,
                domainResponse.itemsCount(),
                totalAmount
        );
    }
    
    private CartItemWithPriceFacadeResponseDto enrichCartItemWithPrice(CartItemGetResponseDto item) {
        log.debug("Enriching cart item with price: itemId={}, productId={}, variantId={}", 
                item.itemId(), item.productId(), item.variantId());
        
        Long priceId = productVariantService.getVariantById(item.variantId()).getPriceId();
        BigDecimal unitPrice = priceService.getCurrentPrices(Set.of(priceId))
                .get(priceId)
                .price();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.quantity()));
        
        return new CartItemWithPriceFacadeResponseDto(
                item.itemId(),
                item.productId(),
                item.variantId(),
                item.quantity(),
                unitPrice,
                totalPrice,
                item.addedAt()
        );
    }
}