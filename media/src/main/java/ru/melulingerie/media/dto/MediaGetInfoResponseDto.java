package ru.melulingerie.media.dto;

import lombok.Builder;
import ru.melulingerie.domain.Media;

@Builder
public record MediaGetInfoResponseDto(
        Long id,
        String fileName,
        String s3Url
) {

    public static MediaGetInfoResponseDto fromEntity(Media media) {
        return MediaGetInfoResponseDto.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .s3Url(media.getS3Url())
                .build();
    }
}
