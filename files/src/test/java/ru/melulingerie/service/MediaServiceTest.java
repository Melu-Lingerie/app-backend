package ru.melulingerie.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.melulingerie.domain.EntityType;
import ru.melulingerie.domain.Media;
import ru.melulingerie.domain.MediaType;
import ru.melulingerie.dto.MediaUploadResponse;
import ru.melulingerie.dto.UploadRequest;
import ru.melulingerie.repository.MediaRepository;
import ru.melulingerie.util.FileKeyGenerator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MediaRepository mediaRepository;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        FileKeyGenerator fileKeyGenerator = new FileKeyGenerator();

        FileValidationService fileValidationService = new FileValidationService();
        ReflectionTestUtils.setField(fileValidationService, "maxFileSize", 10485760L); // 10MB
        ReflectionTestUtils.setField(fileValidationService, "allowedImageTypes",
                Set.of("image/jpeg", "image/png", "image/webp"));
        ReflectionTestUtils.setField(fileValidationService, "allowedImageExtensions",
                Set.of(".jpg", ".jpeg", ".png", ".webp"));
        ReflectionTestUtils.setField(fileValidationService, "allowedVideoTypes",
                Set.of("video/mp4", "video/webm", "video/quicktime"));
        ReflectionTestUtils.setField(fileValidationService, "allowedVideoExtensions",
                Set.of(".mp4", ".webm", ".mov"));

        FileUploadService fileUploadService = new FileUploadService(s3Client, fileKeyGenerator);
        ReflectionTestUtils.setField(fileUploadService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        mediaService = new MediaService(fileUploadService, mediaRepository, fileValidationService);
    }

    /**
     * Проверяет, что если медиафайл с таким же хешем и для той же сущности уже существует,
     * сервис возвращает существующую запись Media, не выполняя новую загрузку в S3.
     */
    @Test
    void uploadAndSaveMedia_ShouldReturnExistingMedia_WhenDuplicateFound() {
        MockMultipartFile file = createValidJpegFile("duplicate.jpg");
        UploadRequest request = createUploadRequest(file, 100L, EntityType.PRODUCT);

        Media existingMedia = createMediaEntity(1L, "existing-hash", "existing/s3/key.jpg");
        existingMedia.setEntityId(100L);
        existingMedia.setEntityType(EntityType.PRODUCT);
        existingMedia.setMediaType(MediaType.IMAGE);

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(100L), eq(EntityType.PRODUCT)))
                .thenReturn(Optional.of(existingMedia));

        MediaUploadResponse response = mediaService.uploadAndSaveMedia(request);

        assertThat(response.getMediaId()).isEqualTo(1L);

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaRepository, never()).save(any());
        verify(mediaRepository, times(1)).findByFileHashAndEntityIdAndEntityType(anyString(), eq(100L), eq(EntityType.PRODUCT));
    }

    /**
     * Проверяет, что если файл с таким же хешем уже существует в S3 (но связан с другой сущностью),
     * сервис повторно использует существующий S3-объект, создавая новую запись в Media.
     */
    @Test
    void uploadAndSaveMedia_ShouldReuseS3File_WhenFileWithSameHashExists() {
        MockMultipartFile file = createValidJpegFile("reuse.jpg");
        UploadRequest request = createUploadRequest(file, 200L, EntityType.COLLECTION);

        Media existingMedia = createMediaEntity(10L, "same-hash", "reusable/s3/key.jpg");
        existingMedia.setS3Bucket("test-bucket");
        existingMedia.setS3Url("https://test-bucket.storage.yandexcloud.net/reusable/s3/key.jpg");

        Media savedMedia = createMediaEntity(20L, "same-hash", "reusable/s3/key.jpg");
        savedMedia.setEntityId(200L);
        savedMedia.setEntityType(EntityType.COLLECTION);
        savedMedia.setMediaType(MediaType.IMAGE);

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(200L), eq(EntityType.COLLECTION)))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(anyString()))
                .thenReturn(Optional.of(existingMedia));
        when(mediaRepository.save(any(Media.class)))
                .thenReturn(savedMedia);

        MediaUploadResponse response = mediaService.uploadAndSaveMedia(request);

        assertThat(response.getMediaId()).isEqualTo(20L);
        assertThat(response.getEntityId()).isEqualTo(200L);

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    /**
     * Проверяет, что если идентичный файл отсутствует в системе,
     * сервис корректно загружает новый файл в S3, создает новую запись Media в базе данных.
     */
    @Test
    void uploadAndSaveMedia_ShouldUploadNewFile_WhenFileDoesNotExist() {
        MockMultipartFile file = createValidJpegFile("new-file.jpg");
        UploadRequest request = createUploadRequest(file, 300L, EntityType.PRODUCT);

        Media savedMedia = createMediaEntity(30L, "new-hash", "generated-key");
        savedMedia.setMediaType(MediaType.IMAGE);

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(300L), eq(EntityType.PRODUCT)))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(anyString()))
                .thenReturn(Optional.empty());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(mediaRepository.save(any(Media.class)))
                .thenReturn(savedMedia);

        MediaUploadResponse response = mediaService.uploadAndSaveMedia(request);

        assertThat(response.getMediaId()).isEqualTo(30L);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo("test-bucket");
        assertThat(capturedRequest.key()).startsWith("new-file_"); // Исправлено: дефисы НЕ заменяются
        assertThat(capturedRequest.key()).endsWith(".jpg");
        assertThat(capturedRequest.contentType()).isEqualTo("image/jpeg");

        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    /**
     * Проверяет, что при загрузке нового файла все общие поля из запроса корректно устанавливаются.
     */
    @Test
    void uploadAndSaveMedia_ShouldSetCommonFields_Correctly() {
        // Given
        MockMultipartFile file = createValidJpegFile("field-test.jpg");
        UploadRequest request = UploadRequest.builder()
                .file(file)
                .requestId(UUID.randomUUID())
                .entityId(400L)
                .entityType(EntityType.COLLECTION)
                .sortOrder(5)
                .isPrimary(true)
                .uploadedBy("test-user")
                .build();

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(400L), eq(EntityType.COLLECTION)))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(anyString()))
                .thenReturn(Optional.empty());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(mediaRepository.save(any(Media.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.uploadAndSaveMedia(request);

        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(mediaCaptor.capture());

        Media capturedMedia = mediaCaptor.getValue();
        assertThat(capturedMedia.getEntityId()).isEqualTo(400L);
        assertThat(capturedMedia.getEntityType()).isEqualTo(EntityType.COLLECTION);
        assertThat(capturedMedia.getFileName()).isEqualTo("field-test.jpg");
        assertThat(capturedMedia.getMimeType()).isEqualTo("image/jpeg");
        assertThat(capturedMedia.getMediaType()).isEqualTo(MediaType.IMAGE);
        assertThat(capturedMedia.getSortOrder()).isEqualTo(5);
        assertThat(capturedMedia.isPrimary()).isTrue();
        assertThat(capturedMedia.getUploadedBy()).isEqualTo("test-user");
        assertThat(capturedMedia.getS3Bucket()).isEqualTo("test-bucket");
        assertThat(capturedMedia.getS3Key()).startsWith("field-test_");
        assertThat(capturedMedia.getS3Key()).endsWith(".jpg");
        assertThat(capturedMedia.getS3Url()).startsWith("https://test-bucket.storage.yandexcloud.net/field-test_");
    }

    /**
     * Проверяет, что если валидация файла не пройдена (например, неподдерживаемый тип),
     * сервис выбрасывает исключение и прекращает обработку.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleValidationFailure() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "some text content".getBytes()
        );
        UploadRequest request = createUploadRequest(invalidFile, 500L, EntityType.PRODUCT);

        assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Поддерживаются только изображения");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaRepository, never()).findByFileHashAndEntityIdAndEntityType(anyString(), any(), any());
        verify(mediaRepository, never()).save(any());
    }

    /**
     * Проверяет обработку файла слишком большого размера.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleFileSizeValidationFailure() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB > 10MB лимита
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );
        UploadRequest request = createUploadRequest(largeFile, 600L, EntityType.PRODUCT);

        assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Размер файла не должен превышать");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaRepository, never()).save(any());
    }

    /**
     * Проверяет обработку пустого файла.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleEmptyFileValidationFailure() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );
        UploadRequest request = createUploadRequest(emptyFile, 700L, EntityType.PRODUCT);

        assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Файл не выбран или пустой");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaRepository, never()).save(any());
    }

    /**
     * Проверяет, что если происходит ошибка при чтении файла,
     * она корректно обрабатывается и пробрасывается.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleFileReadFailure() throws IOException {
        MockMultipartFile mockFile = mock(MockMultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("failing-file.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenThrow(new IOException("Failed to read file stream"));

        UploadRequest request = createUploadRequest(mockFile, 800L, EntityType.PRODUCT);

        assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error processing file metadata")
                .hasCauseInstanceOf(IOException.class);

        verify(mediaRepository, never()).save(any());
    }


    /**
     * Проверяет, что сервис корректно обрабатывает видеофайлы,
     * устанавливая правильный MediaType (VIDEO).
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleVideoFileType() {
        MockMultipartFile videoFile = createValidMp4File("video.mp4");
        UploadRequest request = createUploadRequest(videoFile, 900L, EntityType.PRODUCT);

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(900L), eq(EntityType.PRODUCT)))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(anyString()))
                .thenReturn(Optional.empty());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(mediaRepository.save(any(Media.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.uploadAndSaveMedia(request);

        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(mediaCaptor.capture());

        Media capturedMedia = mediaCaptor.getValue();
        assertThat(capturedMedia.getMediaType()).isEqualTo(MediaType.VIDEO);
        assertThat(capturedMedia.getMimeType()).isEqualTo("video/mp4");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertThat(requestCaptor.getValue().contentType()).isEqualTo("video/mp4");
    }

    /**
     * Проверяет, что FileKeyGenerator корректно генерирует S3 ключи с правильным форматом.
     */
    @Test
    void uploadAndSaveMedia_ShouldGenerateCorrectS3Keys() {
        MockMultipartFile file1 = createValidJpegFile("тест файл & (спец символы).jpg"); // С кириллицей и спецсимволами
        MockMultipartFile file2 = createValidJpegFile("simple.png");

        UploadRequest request1 = createUploadRequest(file1, 1000L, EntityType.PRODUCT);
        UploadRequest request2 = createUploadRequest(file2, 1001L, EntityType.PRODUCT);

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(1000L), eq(EntityType.PRODUCT)))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(anyString()))
                .thenReturn(Optional.empty());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(mediaRepository.save(any(Media.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.uploadAndSaveMedia(request1);

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(1001L), eq(EntityType.PRODUCT)))
                .thenReturn(Optional.empty());
        mediaService.uploadAndSaveMedia(request2);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(2)).putObject(requestCaptor.capture(), any(RequestBody.class));

        var capturedRequests = requestCaptor.getAllValues();

        String key1 = capturedRequests.get(0).key();
        assertThat(key1).startsWith("_____");
        assertThat(key1).contains("_");
        assertThat(key1).endsWith(".jpg");
        assertThat(key1).matches(".*_\\d{8}_\\d{6}_\\d{3}_[a-f0-9]{8}\\.jpg");

        String key2 = capturedRequests.get(1).key();
        assertThat(key2).startsWith("simple_");
        assertThat(key2).endsWith(".png");
        assertThat(key2).matches("simple_\\d{8}_\\d{6}_\\d{3}_[a-f0-9]{8}\\.png");
    }

    private UploadRequest createUploadRequest(MockMultipartFile file, Long entityId, EntityType entityType) {
        return UploadRequest.builder()
                .file(file)
                .requestId(UUID.randomUUID())
                .entityId(entityId)
                .entityType(entityType)
                .sortOrder(0)
                .isPrimary(false)
                .uploadedBy("test-user")
                .build();
    }

    private Media createMediaEntity(Long id, String fileHash, String s3Key) {
        Media media = new Media();
        media.setId(id);
        media.setFileHash(fileHash);
        media.setS3Key(s3Key);
        media.setS3Bucket("test-bucket");
        media.setS3Url("https://test-bucket.storage.yandexcloud.net/" + s3Key);
        media.setMediaType(MediaType.IMAGE);
        return media;
    }

    /**
     * Создает валидный JPEG файл с реальными байтами изображения.
     * Это минимальный 1x1 пиксельный JPEG файл.
     */
    private MockMultipartFile createValidJpegFile(String filename) {
        // Минимальный валидный JPEG (1x1 пиксель, красный цвет)
        byte[] jpegBytes = {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
                0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00, 0x08,
                0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D,
                0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C,
                0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30,
                0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32, (byte) 0xFF,
                (byte) 0xC0, 0x00, 0x11, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00, 0x02, 0x11, 0x01,
                0x03, 0x11, 0x01, (byte) 0xFF, (byte) 0xC4, 0x00, 0x1F, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01,
                0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
                0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, (byte) 0xFF, (byte) 0xC4, 0x00, (byte) 0xB5, 0x10, 0x00, 0x02,
                0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05, 0x04, 0x04, 0x00, 0x00, 0x01, 0x7D, 0x01, 0x02,
                0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71,
                0x14, 0x32, (byte) 0x81, (byte) 0x91, (byte) 0xA1, 0x08, 0x23, 0x42, (byte) 0xB1, (byte) 0xC1, 0x15,
                0x52, (byte) 0xD1, (byte) 0xF0, 0x24, 0x33, 0x62, 0x72, (byte) 0x82, 0x09, 0x0A, 0x16, 0x17, 0x18,
                0x19, 0x1A, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43,
                0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63,
                0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, (byte) 0x83,
                (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8A, (byte) 0x92,
                (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9A,
                (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7, (byte) 0xA8, (byte) 0xA9,
                (byte) 0xAA, (byte) 0xB2, (byte) 0xB3, (byte) 0xB4, (byte) 0xB5, (byte) 0xB6, (byte) 0xB7, (byte) 0xB8,
                (byte) 0xB9, (byte) 0xBA, (byte) 0xC2, (byte) 0xC3, (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7,
                (byte) 0xC8, (byte) 0xC9, (byte) 0xCA, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6,
                (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xE1, (byte) 0xE2, (byte) 0xE3, (byte) 0xE4,
                (byte) 0xE5, (byte) 0xE6, (byte) 0xE7, (byte) 0xE8, (byte) 0xE9, (byte) 0xEA, (byte) 0xF1, (byte) 0xF2,
                (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xFA,
                (byte) 0xFF, (byte) 0xDA, 0x00, 0x0C, 0x03, 0x01, 0x00, 0x02, 0x11, 0x03, 0x11, 0x00, 0x3F, 0x00,
                (byte) 0xF7, (byte) 0xFA, (byte) 0x28, (byte) 0xA2, (byte) 0x8A, 0x00, (byte) 0x28, (byte) 0xA2,
                (byte) 0x8A, 0x00, (byte) 0xFF, (byte) 0xD9
        };

        return new MockMultipartFile(
                "file",
                filename,
                "image/jpeg",
                jpegBytes
        );
    }

    /**
     * Создает валидный MP4 файл с минимальными байтами.
     */
    private MockMultipartFile createValidMp4File(String filename) {
        // Минимальный валидный MP4 заголовок
        byte[] mp4Bytes = {
                0x00, 0x00, 0x00, 0x20, 0x66, 0x74, 0x79, 0x70, // ftyp box
                0x69, 0x73, 0x6F, 0x6D, 0x00, 0x00, 0x02, 0x00,
                0x69, 0x73, 0x6F, 0x6D, 0x69, 0x73, 0x6F, 0x32,
                0x61, 0x76, 0x63, 0x31, 0x6D, 0x70, 0x34, 0x31,
                0x00, 0x00, 0x00, 0x08, 0x66, 0x72, 0x65, 0x65 // free box
        };

        return new MockMultipartFile(
                "video",
                filename,
                "video/mp4",
                mp4Bytes
        );
    }
}