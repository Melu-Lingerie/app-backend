package ru.melulingerie.api.cart.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.cart.dto.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartGetFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartUpdateQuantityFacadeRequestDto;

import java.util.List;

@Tag(name = "Cart", description = "Операции с корзиной покупок")
@RequestMapping("/api/v1/cart/{cartId}")
public interface CartResource {

    @Operation(
            summary = "Получить содержимое корзины",
            description = "Возвращает все товары в корзине с общей суммой и количеством товаров",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Содержимое корзины успешно получено",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CartGetFacadeResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Корзина не найдена"),
                    @ApiResponse(responseCode = "400", description = "Некорректный ID корзины")
            }
    )
    @GetMapping
    ResponseEntity<CartGetFacadeResponseDto> getCart(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор корзины",
                    example = "1001",
                    required = true
            )
            @PathVariable("cartId") @NotNull Long cartId
    );

    @Operation(
            summary = "Добавить товар в корзину",
            description = "Добавляет товар в корзину или увеличивает количество если товар уже есть в корзине",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Товар успешно добавлен в корзину",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CartAddFacadeResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
                    @ApiResponse(responseCode = "404", description = "Корзина или товар не найдены"),
                    @ApiResponse(responseCode = "409", description = "Корзина заполнена или превышен лимит количества")
            }
    )
    @PostMapping("/items")
    ResponseEntity<CartAddFacadeResponseDto> addItemToCart(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор корзины",
                    example = "1001",
                    required = true
            )
            @PathVariable("cartId") @NotNull Long cartId,
            
            @Parameter(
                    description = "Данные товара для добавления в корзину",
                    required = true
            )
            @RequestBody @Valid CartAddFacadeRequestDto request
    );

    @Operation(
            summary = "Обновить количество товара в корзине",
            description = "Изменяет количество конкретного товара в корзине",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Количество товара успешно обновлено"),
                    @ApiResponse(responseCode = "400", description = "Некорректное количество"),
                    @ApiResponse(responseCode = "404", description = "Корзина или товар не найдены")
            }
    )
    @PutMapping("/items/{itemId}/quantity")
    ResponseEntity<Void> updateItemQuantity(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор корзины",
                    example = "1001",
                    required = true
            )
            @PathVariable("cartId") @NotNull Long cartId,
            
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор товара в корзине",
                    example = "2001",
                    required = true
            )
            @PathVariable("itemId") @NotNull Long itemId,
            
            @Parameter(
                    description = "Новое количество товара",
                    required = true
            )
            @RequestBody @Valid CartUpdateQuantityFacadeRequestDto request
    );

    @Operation(
            summary = "Удалить товары из корзины",
            description = "Удаляет указанные товары из корзины по списку их идентификаторов",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Товары успешно удалены из корзины"),
                    @ApiResponse(responseCode = "400", description = "Некорректный список идентификаторов"),
                    @ApiResponse(responseCode = "404", description = "Корзина не найдена")
            }
    )
    @DeleteMapping("/items")
    ResponseEntity<Void> removeItemsFromCart(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор корзины",
                    example = "1001",
                    required = true
            )
            @PathVariable("cartId") @NotNull Long cartId,
            
            @Parameter(
                    description = "Список идентификаторов товаров для удаления",
                    required = true
            )
            @RequestBody @NotEmpty List<@NotNull Long> itemIds
    );

    @Operation(
            summary = "Очистить корзину",
            description = "Удаляет все товары из корзины",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Корзина успешно очищена. Возвращает количество удаленных товаров",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Integer.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Корзина не найдена")
            }
    )
    @DeleteMapping
    ResponseEntity<Integer> clearCart(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Идентификатор корзины",
                    example = "1001",
                    required = true
            )
            @PathVariable("cartId") @NotNull Long cartId
    );
}