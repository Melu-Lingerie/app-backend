package ru.melulingerie.facade.products.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.products.enums.ProductStatus;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    @Mapping(target = "productStatus", source = "productStatus", qualifiedByName = "enumToString")
    ProductCatalogFilterRequestDto toProductCatalogFilterRequestDto(ProductCatalogRequestDto productCatalogRequestDto);

    @Named("enumToString")
    default String enumToString(ProductStatus status) {
        return status == null ? null : status.name();
    }

}
