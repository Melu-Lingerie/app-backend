package ru.melulingerie.facade.products.mapper;

import org.mapstruct.Mapper;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.products.dto.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.ProductCatalogResponseDto;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    ProductFilterRequestDto toProductFilterRequestDto(ProductCatalogRequestDto productCatalogRequestDto);

    ProductCatalogResponseDto toProductCatalogResponseDto(ProductItemResponseDto productItemResponseDto);

}
