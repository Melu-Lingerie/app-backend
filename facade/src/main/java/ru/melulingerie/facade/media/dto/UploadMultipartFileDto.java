package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record UploadMultipartFileDto(
        InputStream inputStream,
        String originalFilename,
        String contentType,
        long size,
        String name
) {}
