package ru.melulingerie.facade.media.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

@Schema(name = "UploadMediaResponseDto", description = "Результат загрузки медиафайла")
@Builder
public record UploadMediaResponseDto(

        @Schema(description = "Идентификатор загруженного файла", example = "77e1c83b-7bb0-437b-bc50-a7a58e5660ac")
        UUID fileId,

        @Schema(description = "Публичный URL для доступа к файлу", example = "https://cdn.example.com/media/77e1c83b-7bb0-437b-bc50-a7a58e5660ac.jpg")
        String url,

        @Schema(description = "Сообщение/статус операции", example = "Файл успешно загружен")
        String message
) {}
