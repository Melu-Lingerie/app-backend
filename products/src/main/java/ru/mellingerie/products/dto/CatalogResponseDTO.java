package ru.mellingerie.products.dto;

import java.util.List;

/**
 * Ответ каталога товаров с пагинацией
 */
public record CatalogResponseDTO(
    List<PublicProductDTO> items,
    String nextCursor,
    Boolean hasMore,
    Long totalVisible
) {} 