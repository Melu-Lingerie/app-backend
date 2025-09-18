package ru.melulingerie.facade.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(
        name = "WishlistItemGetFacadeResponseDto",
        description = "Элемент списка желаний"
)
public record WishlistItemGetFacadeResponseDto(

        @Schema(description = "Идентификатор элемента списка желаний", example = "98765")
        Long id,

        @Schema(description = "Идентификатор товара", example = "12345")
        Long productId,

        @Schema(
                description = "Дата и время добавления в список желаний (ISO-8601)",
                example = "2025-09-18T10:05:30"
        )
        LocalDateTime addedAt

) {}
