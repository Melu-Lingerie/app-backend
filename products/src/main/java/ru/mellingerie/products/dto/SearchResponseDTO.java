package ru.mellingerie.products.dto;

import java.util.List;

/**
 * Ответ поиска товаров
 */
public record SearchResponseDTO(
    List<ProductSearchResultDTO> items,
    Long totalFound,
    Boolean hasMore,
    Integer page,
    SearchMetaDTO searchMeta
) {} 