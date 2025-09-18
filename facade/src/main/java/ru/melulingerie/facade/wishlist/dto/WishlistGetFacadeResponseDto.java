package ru.melulingerie.facade.wishlist.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.melulingerie.dto.WishlistItemGetResponseDto;

import java.util.List;

@Schema(
        name = "WishlistGetFacadeResponseDto",
        description = "Ответ на получение списка желаний: элементы и их количество"
)
public record WishlistGetFacadeResponseDto(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @ArraySchema(
                arraySchema = @Schema(
                        description = "Список элементов в списке желаний",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(
                        implementation = WishlistItemGetResponseDto.class,
                        description = "Элемент списка желаний"
                ),
                minItems = 0
        )
        List<WishlistItemGetResponseDto> items,

        @Schema(
                description = "Общее количество элементов в списке",
                example = "3",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0"
        )
        Integer itemsCount
) {}
