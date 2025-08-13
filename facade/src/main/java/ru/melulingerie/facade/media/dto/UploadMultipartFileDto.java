package ru.melulingerie.facade.media.dto;

import lombok.Builder;

@Builder
public record UploadMultipartFileDto(
        byte[] content,
        String originalFilename,
        String contentType,
        long size,
        String name
) {}
