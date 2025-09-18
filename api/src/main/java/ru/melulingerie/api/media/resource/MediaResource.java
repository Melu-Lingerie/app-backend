package ru.melulingerie.api.media.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;

import java.util.UUID;

@Tag(name = "Media", description = "Загрузка и управление медиафайлами")
@RequestMapping("/api/v1/media")
public interface MediaResource {

    @Operation(
            summary = "Загрузить медиафайл",
            description = "Принимает файл в формате multipart/form-data и возвращает метаданные загруженного ресурса. "
                    + "Заголовок X-Request-Id используется для трассировки запросов."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно загружен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UploadMediaResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса или отсутствует файл"),
            @ApiResponse(responseCode = "415", description = "Неподдерживаемый тип содержимого (ожидается multipart/form-data)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при загрузке файла")
    })
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UploadMediaResponseDto> uploadMedia(
            @Parameter(
                    description = "Загружаемый файл",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"),
                            encoding = @Encoding(name = "file", contentType = "application/octet-stream")
                    )
            )
            @RequestParam("file") MultipartFile file,

            @Parameter(
                    description = "Идентификатор запроса для трассировки (UUID). Если не задан, может быть сгенерирован на стороне сервера.",
                    required = false,
                    schema = @Schema(type = "string", format = "uuid"),
                    example = "77e1c83b-7bb0-437b-bc50-a7a58e5660ac"
            )
            @RequestHeader(name = "X-Request-Id", required = false) UUID requestId
    );
}
