package ru.melulingerie.dto;

import lombok.Builder;
import lombok.Value;
import ru.melulingerie.domain.Media;

@Value
@Builder
public class MediaUploadResponse {
    Long mediaId;
    Long entityId;
    String s3Url;
    String mediaType;
    String fileName;
    long fileSize;
    boolean isPrimary;
    int sortOrder;

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