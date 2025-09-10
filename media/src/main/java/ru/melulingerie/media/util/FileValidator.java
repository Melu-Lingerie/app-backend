package ru.melulingerie.media.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.melulingerie.domain.MediaType;
import ru.melulingerie.media.dto.CustomMultipartFile;
import ru.melulingerie.media.exception.FileValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FileValidator {

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${file.upload.allowed-image-types}")
    private Set<String> allowedImageTypes;

    @Value("${file.upload.allowed-image-extensions}")
    private Set<String> allowedImageExtensions;

    @Value("${file.upload.allowed-video-types}")
    private Set<String> allowedVideoTypes;

    @Value("${file.upload.allowed-video-extensions}")
    private Set<String> allowedVideoExtensions;

    /**
     * Полная валидация одного файла: собирает все ошибки и выбрасывает одно исключение с их списком.
     */
    public void validateFile(CustomMultipartFile file) {
        List<String> errors = new ArrayList<>();

        if (file == null || file.inputStream() == null || file.size() == 0) {
            errors.add("Файл не выбран или пустой");
        }

        MediaType mediaType;
        if (file != null) {
            mediaType = getMediaType(file);
            if (mediaType == null) {
                errors.add("Поддерживаются только изображения (JPEG, PNG, WebP) и видео (MP4, WebM, MOV)");
            }
        }

        if (file != null && file.size() > maxFileSize) {
            errors.add(String.format("Размер файла не должен превышать %d МБ", maxFileSize / (1024 * 1024)));
        }

        if (!errors.isEmpty()) {
            throw new FileValidationException(errors);
        }
    }

    /**
     * Получает тип медиа (IMAGE или VIDEO) на основе контента и расширения.
     * Возвращает null, если тип не поддерживается.
     */
    public MediaType getMediaType(CustomMultipartFile file) {
        String contentType = file.contentType();
        String fileName = file.originalFilename();

        if (contentType != null) {
            if (allowedImageTypes.contains(contentType.toLowerCase())) return MediaType.IMAGE;
            if (allowedVideoTypes.contains(contentType.toLowerCase())) return MediaType.VIDEO;
        }

        if (fileName != null) {
            String lowerFileName = fileName.toLowerCase();
            if (allowedImageExtensions.stream().anyMatch(lowerFileName::endsWith)) return MediaType.IMAGE;
            if (allowedVideoExtensions.stream().anyMatch(lowerFileName::endsWith)) return MediaType.VIDEO;
        }

        return null;
    }
}