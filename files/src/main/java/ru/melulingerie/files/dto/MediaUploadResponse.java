package ru.melulingerie.files.dto;

import lombok.Builder;
import ru.melulingerie.files.domain.Media;

@Builder
public record MediaUploadResponse(Long mediaId, Long entityId, String s3Url, String mediaType, String fileName,
                                  long fileSize, boolean isPrimary, int sortOrder) {
    public static MediaUploadResponse fromEntity(Media media) {
        return MediaUploadResponse.builder()
                .mediaId(media.getId())
                .entityId(media.getEntityId())
                .s3Url(media.getS3Url())
                .mediaType(media.getMediaType().name())
                .fileName(media.getFileName())
                .fileSize(media.getFileSize())
                .isPrimary(media.isPrimary())
                .sortOrder(media.getSortOrder())
                .build();
    }
}