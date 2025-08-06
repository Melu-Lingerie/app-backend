package ru.melulingerie.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.domain.MediaType;

import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileValidationService {

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
     * Полная валидация одного файла
     */
    public void validateSingleFile(MultipartFile file) {
        validateFileNotEmpty(file);
        validateMediaType(file);
        validateFileSize(file);
    }

    /**
     * Проверка, что файл является разрешенным медиа-типом (фото или видео)
     */
    public void validateMediaType(MultipartFile file) {
        if (!isSupportedMedia(file)) {
            throw new IllegalArgumentException(
                    "Поддерживаются только изображения (JPEG, PNG, WebP) и видео (MP4, WebM, MOV)"
            );
        }
    }

    /**
     * Определяет, является ли файл изображением или видео
     */
    public boolean isSupportedMedia(MultipartFile file) {
        return getMediaType(file) != null;
    }

    /**
     * Получает тип медиа (IMAGE или VIDEO) на основе контента и расширения.
     * Возвращает null, если тип не поддерживается.
     */
    public MediaType getMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

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

    public void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не выбран или пустой");
        }
    }

    public void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("Размер файла не должен превышать %d МБ", maxFileSize / (1024 * 1024))
            );
        }
    }
}