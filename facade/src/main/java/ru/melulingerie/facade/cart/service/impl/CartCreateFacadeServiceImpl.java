package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.dto.response.CartCreateResponseDto;
import ru.melulingerie.cart.service.CartCreateService;
import ru.melulingerie.facade.cart.dto.CartCreateFacadeResponseDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartCreateFacadeService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCreateFacadeServiceImpl implements CartCreateFacadeService {

    private final CartMapper cartMapper;
    private final CartCreateService cartCreateService;

    @Override
    public CartCreateFacadeResponseDto createCart(Long userId) {
        log.debug("Creating or getting existing cart for user: {}", userId);
        CartCreateResponseDto response = cartCreateService.createCart(userId);

        log.debug("Created cart: {}", response);
        return cartMapper.toCreateFacadeResponseDto(response);
    }
}