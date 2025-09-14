package ru.melulingerie.facade.products.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    ProductCatalogFilterRequestDto toProductCatalogFilterRequestDto(ProductCatalogRequestDto productCatalogRequestDto);

}
