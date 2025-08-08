package ru.melulingerie.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.melulingerie.domain.EntityType;
import ru.melulingerie.dto.MediaUploadResponse;
import ru.melulingerie.repository.MediaRepository;
import ru.melulingerie.service.FileUploadService;
import ru.melulingerie.service.FileValidationService;
import ru.melulingerie.service.MediaService;
import ru.melulingerie.util.FileKeyGenerator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMediaControllerTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MediaRepository mediaRepository;

    private AdminMediaController adminMediaController;
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        FileKeyGenerator fileKeyGenerator = new FileKeyGenerator();

        FileValidationService fileValidationService = new FileValidationService();
        ReflectionTestUtils.setField(fileValidationService, "maxFileSize", 10485760L);
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
        adminMediaController = new AdminMediaController(mediaService);
    }

    /**
     * Проверяет полный end-to-end поток загрузки медиафайла через контроллер.
     */
    @Test
    void uploadMedia_ShouldReturnCreatedResponse_WhenValidRequest() {
        MockMultipartFile mockFile = createValidJpegFile("test-image.jpg");

        Long entityId = 123L;
        EntityType entityType = EntityType.PRODUCT;
        UUID requestId = UUID.randomUUID();
        int sortOrder = 1;
        boolean isPrimary = true;

        when(mediaRepository.findByFileHashAndEntityIdAndEntityType(anyString(), eq(entityId), eq(entityType)))
                .thenReturn(java.util.Optional.empty());
        when(mediaRepository.findFirstByFileHash(anyString()))
                .thenReturn(java.util.Optional.empty());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(mediaRepository.save(any(ru.melulingerie.domain.Media.class)))
                .thenAnswer(invocation -> {
                    var media = (ru.melulingerie.domain.Media) invocation.getArgument(0);
                    media.setId(456L);
                    return media;
                });

        ResponseEntity<MediaUploadResponse> response = adminMediaController.uploadMedia(
                mockFile, entityId, entityType, requestId, sortOrder, isPrimary
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMediaId()).isEqualTo(456L);
        assertThat(response.getBody().getEntityId()).isEqualTo(entityId);
        assertThat(response.getBody().getFileName()).isEqualTo("test-image.jpg");
        assertThat(response.getBody().isPrimary()).isTrue();
        assertThat(response.getBody().getSortOrder()).isEqualTo(1);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaRepository, times(1)).save(any(ru.melulingerie.domain.Media.class));
    }

    private MockMultipartFile createValidJpegFile(String filename) {
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
        return new MockMultipartFile("file", filename, "image/jpeg", jpegBytes);
    }
}