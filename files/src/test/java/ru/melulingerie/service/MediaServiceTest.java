package ru.melulingerie.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.melulingerie.domain.*;
import ru.melulingerie.dto.MediaUploadResponse;
import ru.melulingerie.dto.UploadRequest;
import ru.melulingerie.dto.UploadResult;
import ru.melulingerie.repository.MediaRepository;
import ru.melulingerie.util.FileHashingUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private FileValidationService fileValidationService;

    @InjectMocks
    private MediaService mediaService;

    /**
     * Проверяет, что если медиафайл с таким же хешем и для той же сущности уже существует,
     * сервис возвращает существующую запись Media, не выполняя новую загрузку в S3 и не создавая новую запись в базе данных.
     * Это предотвращает создание дубликатов.
     */
    @Test
    void uploadAndSaveMedia_ShouldReturnExistingMedia_WhenDuplicateFound() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("test.jpg");

        UploadRequest request = createUploadRequest(mockFile, 123L, EntityType.PRODUCT);
        String fileHash = "test-hash-123";

        Media existingMedia = createMediaEntity(1L, fileHash, "existing-key");

        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 123L, EntityType.PRODUCT))
                .thenReturn(Optional.of(existingMedia));

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            MediaUploadResponse result = mediaService.uploadAndSaveMedia(request);

            assertThat(result).isNotNull();
            assertThat(result.getMediaId()).isEqualTo(existingMedia.getId());

            verify(fileUploadService, never()).upload(any());
            verify(mediaRepository, never()).save(any());
            verify(mediaRepository, times(1))
                    .findByFileHashAndEntityIdAndEntityType(fileHash, 123L, EntityType.PRODUCT);
        }
    }

    /**
     * Проверяет, что если файл с таким же хешем уже существует в S3 (но связан с другой сущностью),
     * сервис повторно использует существующий S3-объект (ключ), создавая новую запись в Media,
     * но избегая повторной загрузки файла в S3.
     */
    @Test
    void uploadAndSaveMedia_ShouldReuseS3File_WhenFileWithSameHashExists() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("new-image.jpg");

        UploadRequest request = createUploadRequest(mockFile, 456L, EntityType.COLLECTION);
        String fileHash = "existing-file-hash";

        Media existingFileMedia = createMediaEntity(2L, fileHash, "existing-s3-key");
        Media savedMedia = createMediaEntity(3L, fileHash, "existing-s3-key");
        savedMedia.setEntityId(456L);
        savedMedia.setEntityType(EntityType.COLLECTION);

        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 456L, EntityType.COLLECTION))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(fileHash))
                .thenReturn(Optional.of(existingFileMedia));
        when(fileValidationService.getMediaType(mockFile)).thenReturn(MediaType.IMAGE);
        when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            MediaUploadResponse result = mediaService.uploadAndSaveMedia(request);

            assertThat(result).isNotNull();
            assertThat(result.getEntityId()).isEqualTo(456L);

            verify(fileUploadService, never()).upload(any());
            verify(mediaRepository, times(1)).save(argThat(media ->
                    media.getFileHash().equals(fileHash) &&
                            media.getS3Key().equals(existingFileMedia.getS3Key()) &&
                            media.getEntityId().equals(456L) &&
                            media.getEntityType() == EntityType.COLLECTION &&
                            media.getMediaType() != null
            ));
        }
    }

    /**
     * Проверяет, что если идентичный файл (с таким же хешем) отсутствует в системе,
     * сервис корректно загружает новый файл в S3, создает новую запись Media в базе данных
     * и возвращает данные о загруженном медиа.
     */
    @Test
    void uploadAndSaveMedia_ShouldUploadNewFile_WhenFileDoesNotExist() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("brand-new.jpg");

        UploadRequest request = createUploadRequest(mockFile, 789L, EntityType.PRODUCT);
        String fileHash = "new-file-hash";

        UploadResult uploadResult = UploadResult.builder()
                .bucket("test-bucket")
                .key("uploads/new-file.jpg")
                .url("https://test-bucket.s3.amazonaws.com/uploads/new-file.jpg")
                .build();

        Media savedMedia = createMediaEntity(4L, fileHash, uploadResult.getKey());
        savedMedia.setEntityId(789L);
        savedMedia.setEntityType(EntityType.PRODUCT);

        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 789L, EntityType.PRODUCT))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(fileHash))
                .thenReturn(Optional.empty());
        when(fileUploadService.upload(mockFile)).thenReturn(uploadResult);
        when(fileValidationService.getMediaType(mockFile)).thenReturn(MediaType.IMAGE);
        when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            MediaUploadResponse result = mediaService.uploadAndSaveMedia(request);

            assertThat(result).isNotNull();
            assertThat(result.getEntityId()).isEqualTo(789L);

            verify(fileUploadService, times(1)).upload(mockFile);
            verify(mediaRepository, times(1)).save(argThat(media ->
                    media.getFileHash().equals(fileHash) &&
                            media.getS3Bucket().equals(uploadResult.getBucket()) &&
                            media.getS3Key().equals(uploadResult.getKey()) &&
                            media.getS3Url().equals(uploadResult.getUrl()) &&
                            media.getEntityId().equals(789L) &&
                            media.getEntityType() == EntityType.PRODUCT &&
                            media.getMediaType() == MediaType.IMAGE
            ));
        }
    }

    /**
     * Проверяет, что при загрузке нового файла все общие поля из запроса (такие как sortOrder, isPrimary)
     * и метаданные файла (имя, MIME-тип, размер) корректно устанавливаются в создаваемой сущности Media.
     */
    @Test
    void uploadAndSaveMedia_ShouldSetCommonFields_Correctly() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("test-image.jpg");

        UploadRequest request = UploadRequest.builder()
                .file(mockFile)
                .requestId(UUID.randomUUID())
                .entityId(100L)
                .entityType(EntityType.COLLECTION)
                .sortOrder(5)
                .isPrimary(true)
                .uploadedBy("test-user")
                .build();

        String fileHash = "image-hash";
        UploadResult uploadResult = UploadResult.builder()
                .bucket("images-bucket")
                .key("images/test.jpg")
                .url("https://images-bucket.s3.amazonaws.com/images/test.jpg")
                .build();

        Media savedMedia = createMediaEntity(5L, fileHash, uploadResult.getKey());
        savedMedia.setEntityId(100L);
        savedMedia.setEntityType(EntityType.COLLECTION);
        savedMedia.setFileName("test-image.jpg");
        savedMedia.setSortOrder(5);
        savedMedia.setPrimary(true);
        savedMedia.setUploadedBy("test-user");

        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 100L, EntityType.COLLECTION))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(fileHash))
                .thenReturn(Optional.empty());
        when(fileUploadService.upload(mockFile)).thenReturn(uploadResult);
        when(fileValidationService.getMediaType(mockFile)).thenReturn(MediaType.IMAGE);
        when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            MediaUploadResponse result = mediaService.uploadAndSaveMedia(request);

            assertThat(result).isNotNull();
            assertThat(result.getFileName()).isEqualTo("test-image.jpg");
            assertThat(result.getSortOrder()).isEqualTo(5);
            assertThat(result.isPrimary()).isTrue();

            verify(mediaRepository, times(1)).save(argThat(media ->
                    media.getFileName().equals("test-image.jpg") &&
                            media.getMimeType().equals("image/jpeg") &&
                            media.getFileSize() == mockFile.getSize() &&
                            media.getSortOrder() == 5 &&
                            media.isPrimary() &&
                            media.getUploadedBy().equals("test-user") &&
                            media.getMediaType() == MediaType.IMAGE
            ));
        }
    }

    /**
     * Проверяет, что если валидация файла не пройдена (например, из-за неверного типа),
     * сервис выбрасывает исключение и прекращает обработку, не выполняя хеширование,
     * загрузку или сохранение в базу данных.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleValidationFailure() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "invalid.txt", "text/plain", "invalid content".getBytes()
        );

        UploadRequest request = createUploadRequest(mockFile, 999L, EntityType.PRODUCT);

        doThrow(new IllegalArgumentException("Invalid file type"))
                .when(fileValidationService).validateSingleFile(mockFile);

        assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid file type");

        verify(mediaRepository, never()).findByFileHashAndEntityIdAndEntityType(anyString(), anyLong(), any());
        verify(fileUploadService, never()).upload(any());
        verify(mediaRepository, never()).save(any());
    }

    /**
     * Проверяет, что если в процессе вычисления хеша файла происходит ошибка
     * (например, недоступен алгоритм хеширования), сервис выбрасывает RuntimeException,
     * инкапсулируя исходное исключение.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleFileHashingFailure() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("test.jpg");

        UploadRequest request = createUploadRequest(mockFile, 111L, EntityType.PRODUCT);

        doNothing().when(fileValidationService).validateSingleFile(mockFile);

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile))
                    .thenThrow(new NoSuchAlgorithmException("Hash algorithm not found"));

            assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error processing file metadata")
                    .hasCauseInstanceOf(NoSuchAlgorithmException.class);
        }
    }

    /**
     * Проверяет, что если сервис загрузки файлов (FileUploadService) возвращает ошибку
     * во время загрузки в S3, эта ошибка корректно обрабатывается и пробрасывается
     * как RuntimeException.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleUploadServiceFailure() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("failing-upload.jpg");

        UploadRequest request = createUploadRequest(mockFile, 222L, EntityType.COLLECTION);
        String fileHash = "failing-hash";

        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 222L, EntityType.COLLECTION))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(fileHash))
                .thenReturn(Optional.empty());
        when(fileUploadService.upload(mockFile))
                .thenThrow(new IOException("S3 upload failed"));

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error processing file metadata")
                    .hasCauseInstanceOf(IOException.class);
        }
    }

    /**
     * Проверяет, что сервис корректно обрабатывает видеофайлы,
     * устанавливая правильный MediaType (VIDEO) и MIME-тип для записи в базе данных.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleVideoFileType() throws Exception {
        // Given
        MockMultipartFile mockFile = createValidMp4File("test-video.mp4");

        UploadRequest request = createUploadRequest(mockFile, 333L, EntityType.PRODUCT);
        String fileHash = "video-hash";

        UploadResult uploadResult = UploadResult.builder()
                .bucket("video-bucket")
                .key("videos/test.mp4")
                .url("https://video-bucket.s3.amazonaws.com/videos/test.mp4")
                .build();

        Media savedMedia = createMediaEntity(6L, fileHash, uploadResult.getKey());
        savedMedia.setMediaType(MediaType.VIDEO);
        savedMedia.setMimeType("video/mp4");
        savedMedia.setEntityId(333L);
        savedMedia.setEntityType(EntityType.PRODUCT);

        // Mocking
        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 333L, EntityType.PRODUCT))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(fileHash))
                .thenReturn(Optional.empty());
        when(fileUploadService.upload(mockFile)).thenReturn(uploadResult);
        when(fileValidationService.getMediaType(mockFile)).thenReturn(MediaType.VIDEO);
        when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            // When
            MediaUploadResponse result = mediaService.uploadAndSaveMedia(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getMediaType()).isEqualTo("VIDEO");

            verify(mediaRepository, times(1)).save(argThat(media ->
                    media.getMediaType() == MediaType.VIDEO &&
                            media.getMimeType().equals("video/mp4")
            ));
        }
    }

    /**
     * Проверяет, что если происходит сбой при сохранении метаданных медиафайла в базу данных
     * (после успешной загрузки в S3), сервис корректно пробрасывает исключение.
     */
    @Test
    void uploadAndSaveMedia_ShouldHandleMetadataProcessingFailure() throws Exception {
        MockMultipartFile mockFile = createValidJpegFile("test.jpg");
        UploadRequest request = createUploadRequest(mockFile, 444L, EntityType.PRODUCT);
        String fileHash = "test-hash";

        UploadResult uploadResult = UploadResult.builder()
                .bucket("test-bucket")
                .key("images/test.jpg")
                .url("https://test-bucket.s3.amazonaws.com/images/test.jpg")
                .build();

        // Mocking
        doNothing().when(fileValidationService).validateSingleFile(mockFile);
        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash, 444L, EntityType.PRODUCT))
                .thenReturn(Optional.empty());
        when(mediaRepository.findFirstByFileHash(fileHash))
                .thenReturn(Optional.empty());
        when(fileUploadService.upload(mockFile)).thenReturn(uploadResult);
        when(fileValidationService.getMediaType(mockFile)).thenReturn(MediaType.IMAGE);

        when(mediaRepository.save(any(Media.class)))
                .thenThrow(new RuntimeException("Database save failed"));

        try (MockedStatic<FileHashingUtil> hashingMock = mockStatic(FileHashingUtil.class)) {
            hashingMock.when(() -> FileHashingUtil.calculateSHA256(mockFile)).thenReturn(fileHash);

            // When & Then
            assertThatThrownBy(() -> mediaService.uploadAndSaveMedia(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database save failed");
        }

        verify(mediaRepository, times(1)).save(any(Media.class));
    }


    private UploadRequest createUploadRequest(MockMultipartFile file, Long entityId, EntityType entityType) {
        return UploadRequest.builder()
                .file(file)
                .requestId(UUID.randomUUID())
                .entityId(entityId)
                .entityType(entityType)
                .sortOrder(1)
                .isPrimary(false)
                .uploadedBy("test-user")
                .build();
    }

    private Media createMediaEntity(Long id, String fileHash, String s3Key) {
        Media media = new Media();
        media.setId(id);
        media.setFileHash(fileHash);
        media.setS3Bucket("test-bucket");
        media.setS3Key(s3Key);
        media.setS3Url("https://test-bucket.s3.amazonaws.com/" + s3Key);
        media.setFileName("test-file.jpg");
        media.setMimeType("image/jpeg");
        media.setFileSize(1024L);
        media.setMediaType(MediaType.IMAGE);
        media.setSortOrder(1);
        media.setPrimary(false);
        media.setUploadedBy("test-user");
        media.setEntityId(1L);
        media.setEntityType(EntityType.PRODUCT);
        return media;
    }

    /**
     * Создает валидный JPEG файл с реальными байтами изображения
     * Это минимальный 1x1 пиксельный JPEG файл
     */
    private MockMultipartFile createValidJpegFile(String filename) {
        // Минимальный валидный JPEG файл (1x1 пиксель, черный)
        byte[] jpegBytes = {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
                0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
                0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00,
                (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00, 0x03,
                0x02, 0x02, 0x02, 0x02, 0x02, 0x03, 0x02, 0x02,
                0x02, 0x03, 0x03, 0x03, 0x03, 0x04, 0x06, 0x04,
                0x04, 0x04, 0x04, 0x04, 0x08, 0x06, 0x06, 0x05,
                0x06, 0x09, 0x08, 0x0A, 0x0A, 0x09, 0x08, 0x09,
                0x09, 0x0A, 0x0C, 0x0F, 0x0C, 0x0A, 0x0B, 0x0E,
                0x0B, 0x09, 0x09, 0x0D, 0x11, 0x0D, 0x0E, 0x0F,
                0x10, 0x10, 0x11, 0x10, 0x0A, 0x0C, 0x12, 0x13,
                0x12, 0x10, 0x13, 0x0F, 0x10, 0x10, 0x10,
                (byte) 0xFF, (byte) 0xC0, 0x00, 0x11, 0x08, 0x00,
                0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00, 0x02,
                0x11, 0x01, 0x03, 0x11, 0x01, (byte) 0xFF,
                (byte) 0xC4, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x08, (byte) 0xFF,
                (byte) 0xC4, 0x00, 0x14, 0x10, 0x01, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF,
                (byte) 0xDA, 0x00, 0x0C, 0x03, 0x01, 0x00, 0x02,
                0x11, 0x03, 0x11, 0x00, 0x3F, 0x00, 0x00,
                (byte) 0xFF, (byte) 0xD9
        };

        return new MockMultipartFile("file", filename, "image/jpeg", jpegBytes);
    }

    /**
     * Создает валидный MP4 файл с реальными байтами
     * Это минимальный MP4 контейнер
     */
    private MockMultipartFile createValidMp4File(String filename) {
        byte[] mp4Bytes = {
                0x00, 0x00, 0x00, 0x20, // Box size (32 bytes)
                0x66, 0x74, 0x79, 0x70, // "ftyp"
                0x69, 0x73, 0x6F, 0x6D, // Major brand "isom"
                0x00, 0x00, 0x02, 0x00, // Minor version
                0x69, 0x73, 0x6F, 0x6D, // Compatible brand "isom"
                0x69, 0x73, 0x6F, 0x32, // Compatible brand "iso2"
                0x61, 0x76, 0x63, 0x31, // Compatible brand "avc1"
                0x6D, 0x70, 0x34, 0x31, // Compatible brand "mp41"
                // Добавляем минимальный mdat box
                0x00, 0x00, 0x00, 0x08, // Box size (8 bytes)
                0x6D, 0x64, 0x61, 0x74  // "mdat"
        };

        return new MockMultipartFile("file", filename, "video/mp4", mp4Bytes);
    }
}