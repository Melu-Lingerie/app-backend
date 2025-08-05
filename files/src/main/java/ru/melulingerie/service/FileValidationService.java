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

    @Value("${file.upload.max-size:10485760}") // 10MB по умолчанию
    private long maxFileSize;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/ogg", "video/quicktime"
    );
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(
            ".mp4", ".webm", ".ogg", ".mov"
    );

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
            if (ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) return MediaType.IMAGE;
            if (ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) return MediaType.VIDEO;
        }

        if (fileName != null) {
            String lowerFileName = fileName.toLowerCase();
            if (ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lowerFileName::endsWith)) return MediaType.IMAGE;
            if (ALLOWED_VIDEO_EXTENSIONS.stream().anyMatch(lowerFileName::endsWith)) return MediaType.VIDEO;
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