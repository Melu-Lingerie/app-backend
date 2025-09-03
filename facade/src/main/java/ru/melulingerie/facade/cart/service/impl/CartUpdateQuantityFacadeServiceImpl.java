package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.dto.CartUpdateQuantityRequestDto;
import ru.melulingerie.facade.cart.dto.CartUpdateQuantityFacadeRequestDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartUpdateQuantityFacadeService;
import ru.melulingerie.service.CartUpdateQuantityService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartUpdateQuantityFacadeServiceImpl implements CartUpdateQuantityFacadeService {

    private final CartMapper cartMapper;
    private final CartUpdateQuantityService cartUpdateQuantityService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityFacadeRequestDto request) {
        validateQuantity(request.quantity());
        
        log.debug("Updating quantity for cartId: {}, itemId: {}, quantity: {}", 
                  cartId, itemId, request.quantity());
        
        CartUpdateQuantityRequestDto domainRequest = cartMapper.toUpdateQuantityRequestDto(request);
        
        transactionTemplate.executeWithoutResult(status ->
                cartUpdateQuantityService.updateItemQuantity(cartId, itemId, domainRequest)
        );
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be positive number");
        }
    }
}