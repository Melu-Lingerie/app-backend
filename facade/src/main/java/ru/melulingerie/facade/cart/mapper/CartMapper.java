package ru.melulingerie.facade.cart.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.dto.CartAddItemRequestDto;
import ru.melulingerie.dto.CartAddItemResponseDto;
import ru.melulingerie.dto.CartGetResponseDto;
import ru.melulingerie.dto.CartUpdateQuantityRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartGetFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartUpdateQuantityFacadeRequestDto;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartAddItemRequestDto toModuleRequestDto(CartAddFacadeRequestDto facadeRequest);

    CartAddFacadeResponseDto toFacadeResponseDto(CartAddItemResponseDto domainResponse);

    CartGetFacadeResponseDto toFacadeCartResponseDto(CartGetResponseDto domainResponse);

    CartUpdateQuantityRequestDto toUpdateQuantityRequestDto(CartUpdateQuantityFacadeRequestDto facadeRequest);
}