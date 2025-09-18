package ru.melulingerie.api.user.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // если нужен кастомный description у тела
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;
import java.util.UUID;

@Tag(name = "Users", description = "API для управления пользователями")
@RequestMapping("/api/v1/users")
public interface UserResource {

    @Operation(
            summary = "Создать гостевого пользователя",
            description = "Создаёт гостевого пользователя, связывая его с текущей клиентской сессией (cookie sessionId). "
                    + "Если пользователь уже существует для данной сессии, возвращает существующую запись."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Гостевой пользователь создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreateFacadeResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Гостевой пользователь уже существует (идемпотентный результат)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreateFacadeResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Недостаточно прав или некорректная сессия"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping(
            value = "/guests",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserCreateFacadeResponseDto> createGuestUser(
            @Parameter(
                    description = "Идентификатор клиентской сессии (cookie)",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid"),
                    example = "77e1c83b-7bb0-437b-bc50-a7a58e5660ac"
            )
            @CookieValue(name = "sessionId") UUID sessionId,

            @RequestBody(
                    description = "Данные для создания гостевого пользователя",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreateFacadeRequestDto.class)
                    )
            )
            @Valid UserCreateFacadeRequestDto request
    );
}
