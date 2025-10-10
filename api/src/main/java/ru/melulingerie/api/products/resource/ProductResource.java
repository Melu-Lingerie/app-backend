package ru.melulingerie.api.products.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;

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
                    description = "Фильтры каталога и параметры пагинации. Параметры разворачиваются в query автоматически.",
                    required = false
            )
            @ParameterObject ProductCatalogRequestDto productCatalogRequestDto,
            @Parameter (
                    in = ParameterIn.QUERY,
                    description = "Пагинация",
                    required = false
            )
            @ParameterObject Pageable pageable
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
            @PathVariable Long productId
    );
}

