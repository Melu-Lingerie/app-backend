package ru.melulingerie.media.dto;

import lombok.Builder;
import ru.melulingerie.domain.Media;

@Builder
public record MediaResponseDto(Long mediaId, String s3Url, String mediaType, String fileName,
                               long fileSize) {
    public static MediaResponseDto fromEntity(Media media) {
        return MediaResponseDto.builder()
                .mediaId(media.getId())
                .s3Url(media.getS3Url())
                .mediaType(media.getMediaType().name())
                .fileName(media.getFileName())
                .fileSize(media.getFileSize())
                .build();
    }
}