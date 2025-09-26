package ru.melulingerie.media.dto;

import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Builder
public record CustomMultipartFile(byte[] content, String originalFilename, String contentType, long size, String name) {

    public InputStream inputStream() {
        return new ByteArrayInputStream(content);
    }
}

