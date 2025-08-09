package ru.mellingerie.facade.media.dto;

import lombok.Builder;

@Builder
public record CustomMultipartFileFacadeDto(
        byte[] content,
        String originalFilename,
        String contentType,
        long size,
        String name
) {}
