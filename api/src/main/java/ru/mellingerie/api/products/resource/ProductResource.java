package ru.mellingerie.api.products.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;

import java.math.BigDecimal;
import java.util.Set;

@RequestMapping("/api/v1/products")
public interface ProductResource {

    @GetMapping("/catalog")
    Page<ProductCatalogResponseDto> getCatalog(
            @RequestPart(required = false) BigDecimal minPrice,
            @RequestPart(required = false) BigDecimal maxPrice,
            @RequestPart(required = false) Set<Long> categories,//todo прокидывать ли enum здесь? или достаточно строки
            @RequestPart(required = false) Set<String> size,
            @RequestPart(required = false) Set<String> sizeOfBraWithCups,
            @RequestPart(required = false) Set<String> color,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @GetMapping("/{productId}")
    ProductCardResponseDto getProductCardInfo(@PathVariable Long productId);

}
