package ru.melulingerie.facade.mainpage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "BannerMainPageFacadeDto",
        description = "Баннер на главной странице",
        requiredProperties = {"id", "title", "url", "mediaId", "order"}
)
public record BannerMainPageFacadeDto(

        @Schema(
                description = "ID баннера",
                example = "101",
                accessMode = Schema.AccessMode.READ_ONLY,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Long id,

        @Schema(
                description = "Заголовок баннера",
                example = "Осенняя коллекция",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String title,

        @Schema(
                description = "URL перехода по клику",
                example = "https://melu.ru/catalog/new",
                format = "uri",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String url,

        @Schema(
                description = "URL медиа (основное изображение баннера)",
                example = "https://***",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String mediaUrl,

        @Schema(
                description = "Порядок отображения (чем меньше — тем выше)",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Long order
) {}
