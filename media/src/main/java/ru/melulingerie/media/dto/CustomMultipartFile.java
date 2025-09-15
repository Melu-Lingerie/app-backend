package ru.melulingerie.media.dto;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record CustomMultipartFile(InputStream inputStream, String originalFilename, String contentType, long size, String name) {
}

