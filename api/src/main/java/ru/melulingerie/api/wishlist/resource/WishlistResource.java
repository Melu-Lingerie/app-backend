package ru.melulingerie.api.wishlist.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;

import java.util.List;

@Tag(name = "Wishlist", description = "Операции со списком желаний")
@RequestMapping("/api/v1/wishlist/{wishlistId}")
public interface WishlistResource {

    @Operation(
            summary = "Получить список избранных товаров",
            description = "Возвращает список избранных товаров по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список избранных товаров успешно получен",
                            content = @Content(
                                    schema = @Schema(implementation = WishlistGetFacadeResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Список избранных товаров не найден"),
                    @ApiResponse(responseCode = "400", description = "Некорректный ID списка избранных товаров")
            }
    )
    @GetMapping
    ResponseEntity<WishlistGetFacadeResponseDto> getWishlist(
            @Parameter(
                    name = "wishlistId",
                    description = "Идентификатор списка избранных товаров",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable("wishlistId") Long wishlistId);

    @Operation(
            summary = "Добавить товар в список избранных товаров",
            description = "Добавляет товар в список избранных товаров пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Товар успешно добавлен в список избранных товаров",
                            content = @Content(
                                    schema = @Schema(implementation = WishlistAddFacadeResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
                    @ApiResponse(responseCode = "404", description = "Список избранных товаров или товар не найдены"),
                    @ApiResponse(responseCode = "409", description = "Товар уже есть в списке избранных или достигнут лимит")
            }
    )
    @PostMapping("/items")
    ResponseEntity<WishlistAddFacadeResponseDto> addItemToWishlist(
            @Parameter(
                    name = "wishlistId",
                    description = "Идентификатор списка избранных товаров",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable("wishlistId") Long wishlistId,
            @Parameter(
                    description = "Данные добавляемого товара",
                    required = true
            )
            @RequestBody WishlistAddFacadeRequestDto request);

    @Operation(
            summary = "Удалить товары из списка избранных товаров",
            description = "Удаляет один или несколько товаров из списка избранных товаров",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Товары успешно удалены из списка избранных товаров"),
                    @ApiResponse(responseCode = "400", description = "Некорректный список идентификаторов"),
                    @ApiResponse(responseCode = "404", description = "Список желаний не найден")
            }
    )
    @DeleteMapping("/items")
    ResponseEntity<Void> removeItemsFromWishlist(
            @Parameter(
                    name = "wishlistId",
                    description = "Идентификатор списка избранных товаров",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable("wishlistId") Long wishlistId,
            @Parameter(
                    description = "Список идентификаторов элементов для удаления",
                    required = true
            )
            @RequestBody List<Long> itemId);

    @Operation(
            summary = "Очистить список избранных товаров",
            description = "Удаляет все товары из списка избранных товаров",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список избранных товаров успешно очищен",
                            content = @Content(
                                    schema = @Schema(implementation = Integer.class, description = "Количество удаленных элементов")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Список избранных товаров не найден")
            }
    )
    @DeleteMapping
    ResponseEntity<Integer> clearWishlist(
            @Parameter(
                    name = "wishlistId",
                    description = "Идентификатор списка избранных товаров",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable("wishlistId") Long wishlistId);
}