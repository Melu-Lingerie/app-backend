package ru.mellingerie.api.products.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.melulingerie.facade.products.dto.ProductCatalogResponseDto;

import java.math.BigDecimal;
import java.util.Set;

@RequestMapping("/api/v1/products")
public interface ProductResource {

    @GetMapping("/catalog")
    Page<ProductCatalogResponseDto> getCatalog(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Set<Long> categories,//todo прокидывать ли enum здесь? или достаточно строки
            @RequestParam(required = false) Set<String> size,
            @RequestParam(required = false) Set<String> sizeOfBraWithCups,
            @RequestParam(required = false) Set<String> color,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

}
