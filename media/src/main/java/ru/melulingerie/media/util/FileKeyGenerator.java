package ru.melulingerie.media.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class FileKeyGenerator {
    /**
     * Generates a unique and sanitized file name.
     *
     * @param originalName The original name of the file.
     * @return A unique file name.
     */
    public String generate(final String originalName) {
        final String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
        );
        final String uuid = UUID.randomUUID().toString().substring(0, 8);
        String baseName = "file";
        String extension = "";
        if (originalName != null && !originalName.isEmpty()) {
            final String clean = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            final int dot = clean.lastIndexOf('.');
            if (dot != -1) {
                baseName = clean.substring(0, dot);
                extension = clean.substring(dot);
            } else {
                baseName = clean;
            }
        }
        return String.format("%s_%s_%s%s", baseName, timestamp, uuid, extension);
    }
}