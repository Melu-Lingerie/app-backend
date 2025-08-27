package ru.mellingerie.api.products.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;

import java.math.BigDecimal;
import java.util.Set;

@Tag(name = "Products", description = "Операции каталога и карточки товара")
@RequestMapping("/api/v1/products")
public interface ProductResource {

    @Operation(
            summary = "Получить каталог товаров",
            description = "Возвращает страницу элементов каталога с фильтрами по цене, категориям, размерам и цветам; поддерживает пагинацию и сортировку.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Страница элементов каталога",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductCatalogResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @GetMapping(value = "/catalog")
    Page<ProductCatalogResponseDto> getCatalog(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Минимальная цена фильтра",
                    example = "990.00",
                    schema = @Schema(type = "number", format = "bigdecimal")
            )
            @RequestPart(name = "minPrice", required = false) @PositiveOrZero BigDecimal minPrice,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Максимальная цена фильтра",
                    example = "9990.00",
                    schema = @Schema(type = "number", format = "bigdecimal")
            )
            @RequestPart(name = "maxPrice", required = false) @Positive BigDecimal maxPrice,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Идентификаторы категорий для фильтрации",
                    array = @ArraySchema(schema = @Schema(type = "integer", format = "int64", example = "12"))
            )
            @RequestPart(name = "categories", required = false) Set<Long> categories,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Размеры без чашек (множественный параметр)",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "M"))
            )
            @RequestPart(name = "size", required = false) Set<String> size,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Размеры бюстгальтеров с чашками (множественный параметр)",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "75B"))
            )
            @RequestPart(name = "sizeOfBraWithCups", required = false) Set<String> sizeOfBraWithCups,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Цвета для фильтрации (множественный параметр)",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "Black"))
            )
            @RequestPart(name = "color", required = false) Set<String> color,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Параметры пагинации и сортировки: page (0..N), size, sort. Пример: sort=createdAt,desc",
                    schema = @Schema(implementation = Pageable.class)
            )
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(
            summary = "Получить карточку товара",
            description = "Возвращает детальную информацию по карточке товара по идентификатору.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Карточка товара",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductCardResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Товар не найден"),
                    @ApiResponse(responseCode = "400", description = "Некорректный идентификатор")
            }
    )
    @GetMapping("/{productId}")
    ProductCardResponseDto getProductCardInfo(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор товара",
                    example = "1001"
            )
            @PathVariable @NotNull Long productId
    );
}

