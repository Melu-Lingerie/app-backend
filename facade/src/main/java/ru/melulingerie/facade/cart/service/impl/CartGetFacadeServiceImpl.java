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
// TODO: Uncomment when price service is ready
// import ru.melulingerie.price.service.PriceService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartGetFacadeServiceImpl implements CartGetFacadeService {

    private final CartGetService cartGetService;
    // TODO: Uncomment when price service is ready
    // private final PriceService priceService;

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
        
        // TODO: Uncomment when price service is ready
        // BigDecimal unitPrice = priceService.getVariantPrice(item.productId(), item.variantId());

        // Temporary mock price calculation - remove when price service is implemented
        BigDecimal unitPrice = BigDecimal.valueOf(100.00 + (item.productId() * 10) + item.variantId());
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