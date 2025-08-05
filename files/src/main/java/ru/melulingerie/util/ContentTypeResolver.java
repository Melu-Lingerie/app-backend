package ru.melulingerie.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ContentTypeResolver {
    private static final Map<String, String> TYPES = Map.of(
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".png", "image/png",
            ".webp", "image/webp",
            ".gif", "image/gif",
            ".bmp", "image/bmp"
    );

    /**
     * Determines the content type of a file based on its extension.
     *
     * @param name The file name.
     * @return The IANA media type string.
     */
    public String resolve(final String name) {
        final int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "application/octet-stream";
        }
        return TYPES.getOrDefault(
                name.substring(dot).toLowerCase(),
                "application/octet-stream"
        );
    }
}