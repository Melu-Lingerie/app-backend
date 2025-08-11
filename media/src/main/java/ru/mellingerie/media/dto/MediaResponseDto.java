package ru.mellingerie.media.dto;

import lombok.Builder;
import ru.mellingerie.domain.Media;

@Builder
public record MediaResponseDto(Long mediaId, String s3Url, String mediaType, String fileName,
                               long fileSize, boolean isPrimary, int sortOrder) {
    public static MediaResponseDto fromEntity(Media media) {
        return MediaResponseDto.builder()
                .mediaId(media.getId())
                .s3Url(media.getS3Url())
                .mediaType(media.getMediaType().name())
                .fileName(media.getFileName())
                .fileSize(media.getFileSize())
                .isPrimary(media.isPrimary())
                .sortOrder(media.getSortOrder())
                .build();
    }
}