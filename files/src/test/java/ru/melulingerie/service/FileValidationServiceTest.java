package ru.melulingerie.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.melulingerie.domain.MediaType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class FileValidationServiceTest {

    @InjectMocks
    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileValidationService, "maxFileSize", 10 * 1024 * 1024L); // 10 MB
        ReflectionTestUtils.setField(fileValidationService, "allowedImageTypes", Set.of("image/jpeg", "image/png", "image/webp"));
        ReflectionTestUtils.setField(fileValidationService, "allowedImageExtensions", Set.of(".jpg", ".jpeg", ".png", ".webp"));
        ReflectionTestUtils.setField(fileValidationService, "allowedVideoTypes", Set.of("video/mp4", "video/webm", "video/quicktime"));
        ReflectionTestUtils.setField(fileValidationService, "allowedVideoExtensions", Set.of(".mp4", ".webm", ".mov"));
    }

    /**
     * Проверяет, что пустой или null файл не проходит валидацию.
     */
    @Test
    void validateFileNotEmpty_ShouldThrowException_WhenFileIsEmptyOrNull() {
        assertThatThrownBy(() -> fileValidationService.validateFileNotEmpty(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Файл не выбран или пустой");

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        assertThatThrownBy(() -> fileValidationService.validateFileNotEmpty(emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Файл не выбран или пустой");
    }

    /**
     * Проверяет, что файл, размер которого превышает максимально допустимый, не проходит валидацию.
     */
    @Test
    void validateFileSize_ShouldThrowException_WhenFileIsTooLarge() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11 MB
        MockMultipartFile largeFile = new MockMultipartFile("file", "large.jpg", "image/jpeg", largeContent);

        assertThatThrownBy(() -> fileValidationService.validateFileSize(largeFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Размер файла не должен превышать 10 МБ");
    }

    /**
     * Проверяет, что файл, размер которого не превышает максимально допустимый, проходит валидацию.
     */
    @Test
    void validateFileSize_ShouldNotThrowException_WhenFileSizeIsAcceptable() {
        byte[] content = new byte[5 * 1024 * 1024]; // 5 MB
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", content);
        assertDoesNotThrow(() -> fileValidationService.validateFileSize(file));
    }

    /**
     * Проверяет, что файл с неподдерживаемым типом контента не проходит валидацию.
     */
    @Test
    void validateMediaType_ShouldThrowException_WhenMediaTypeIsNotSupported() {
        MockMultipartFile unsupportedFile = new MockMultipartFile("file", "document.pdf", "application/pdf", "content".getBytes());
        assertThatThrownBy(() -> fileValidationService.validateMediaType(unsupportedFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Поддерживаются только изображения (JPEG, PNG, WebP) и видео (MP4, WebM, MOV)");
    }

    /**
     * Проверяет, что getMediaType корректно определяет тип IMAGE по типу контента.
     */
    @Test
    void getMediaType_ShouldReturnImage_ForAllowedImageContentTypes() {
        MockMultipartFile jpegFile = new MockMultipartFile("file", "photo.jpeg", "image/jpeg", "content".getBytes());
        MockMultipartFile pngFile = new MockMultipartFile("file", "logo.png", "image/png", "content".getBytes());

        assertThat(fileValidationService.getMediaType(jpegFile)).isEqualTo(MediaType.IMAGE);
        assertThat(fileValidationService.getMediaType(pngFile)).isEqualTo(MediaType.IMAGE);
    }

    /**
     * Проверяет, что getMediaType корректно определяет тип VIDEO по типу контента.
     */
    @Test
    void getMediaType_ShouldReturnVideo_ForAllowedVideoContentTypes() {
        MockMultipartFile mp4File = new MockMultipartFile("file", "clip.mp4", "video/mp4", "content".getBytes());
        MockMultipartFile movFile = new MockMultipartFile("file", "movie.mov", "video/quicktime", "content".getBytes());

        assertThat(fileValidationService.getMediaType(mp4File)).isEqualTo(MediaType.VIDEO);
        assertThat(fileValidationService.getMediaType(movFile)).isEqualTo(MediaType.VIDEO);
    }

    /**
     * Проверяет, что getMediaType корректно определяет тип IMAGE по расширению файла.
     */
    @Test
    void getMediaType_ShouldReturnImage_ForAllowedImageExtensions() {
        MockMultipartFile jpgFile = new MockMultipartFile("file", "image.jpg", "application/octet-stream", "content".getBytes());
        assertThat(fileValidationService.getMediaType(jpgFile)).isEqualTo(MediaType.IMAGE);
    }

    /**
     * Проверяет, что getMediaType корректно определяет тип VIDEO по расширению файла.
     */
    @Test
    void getMediaType_ShouldReturnVideo_ForAllowedVideoExtensions() {
        MockMultipartFile webmFile = new MockMultipartFile("file", "animation.webm", "application/octet-stream", "content".getBytes());
        assertThat(fileValidationService.getMediaType(webmFile)).isEqualTo(MediaType.VIDEO);
    }

    /**
     * Проверяет, что getMediaType возвращает null для неподдерживаемых файлов.
     */
    @Test
    void getMediaType_ShouldReturnNull_ForUnsupportedMedia() {
        MockMultipartFile textFile = new MockMultipartFile("file", "notes.txt", "text/plain", "content".getBytes());
        assertThat(fileValidationService.getMediaType(textFile)).isNull();
    }

    /**
     * Проверяет, что isSupportedMedia возвращает true для поддерживаемых медиатипов.
     */
    @Test
    void isSupportedMedia_ShouldReturnTrue_ForSupportedTypes() {
        MockMultipartFile imageFile = new MockMultipartFile("file", "image.png", "image/png", "content".getBytes());
        MockMultipartFile videoFile = new MockMultipartFile("file", "video.mp4", "video/mp4", "content".getBytes());

        assertThat(fileValidationService.isSupportedMedia(imageFile)).isTrue();
        assertThat(fileValidationService.isSupportedMedia(videoFile)).isTrue();
    }

    /**
     * Проверяет, что isSupportedMedia возвращает false для неподдерживаемых медиатипов.
     */
    @Test
    void isSupportedMedia_ShouldReturnFalse_ForUnsupportedTypes() {
        MockMultipartFile docFile = new MockMultipartFile("file", "archive.zip", "application/zip", "content".getBytes());
        assertThat(fileValidationService.isSupportedMedia(docFile)).isFalse();
    }

    /**
     * Комплексный тест: проверяет, что полностью валидный файл проходит все проверки.
     */
    @Test
    void validateSingleFile_ShouldPass_ForValidFile() {
        MockMultipartFile validImage = new MockMultipartFile("file", "valid.jpeg", "image/jpeg", new byte[1024]);
        assertDoesNotThrow(() -> fileValidationService.validateSingleFile(validImage));
    }

    /**
     * Комплексный тест: проверяет, что валидация прерывается на первом же сбое (проверка размера).
     */
    @Test
    void validateSingleFile_ShouldFail_OnFirstValidationError() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11 MB
        // Файл с валидным типом, но невалидным размером
        MockMultipartFile largeFile = new MockMultipartFile("file", "large-but-valid-type.png", "image/png", largeContent);

        // Ожидаем ошибку именно по размеру файла
        assertThatThrownBy(() -> fileValidationService.validateSingleFile(largeFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Размер файла не должен превышать 10 МБ");
    }
}