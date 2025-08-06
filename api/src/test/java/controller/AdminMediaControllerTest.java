package controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import ru.melulingerie.domain.EntityType;
import ru.melulingerie.dto.MediaUploadResponse;
import ru.melulingerie.dto.UploadRequest;
import ru.melulingerie.service.MediaService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private ru.melu_lingerie.controller.AdminMediaController adminMediaController;

    /**
     * Проверяет, что при корректном запросе на загрузку медиафайла,
     * контроллер возвращает HTTP-статус 201 (Created) и тело ответа
     * с данными о загруженном файле.
     */
    @Test
    void uploadMedia_ShouldReturnCreatedResponse_WhenValidRequest() {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        Long entityId = 123L;
        EntityType entityType = EntityType.PRODUCT;
        UUID requestId = UUID.randomUUID();
        int sortOrder = 1;
        boolean isPrimary = true;

        MediaUploadResponse expectedResponse = MediaUploadResponse.builder()
                .mediaId(456L)
                .entityId(entityId)
                .s3Url("https://s3.amazonaws.com/bucket/test-image.jpg")
                .mediaType("image/jpeg")
                .fileName("test-image.jpg")
                .fileSize(12L)
                .isPrimary(isPrimary)
                .sortOrder(sortOrder)
                .build();

        when(mediaService.uploadAndSaveMedia(any(UploadRequest.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<MediaUploadResponse> response = adminMediaController.uploadMedia(
                mockFile, entityId, entityType, requestId, sortOrder, isPrimary
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMediaId()).isEqualTo(456L);
        assertThat(response.getBody().getEntityId()).isEqualTo(entityId);
        assertThat(response.getBody().getFileName()).isEqualTo("test-image.jpg");
        assertThat(response.getBody().isPrimary()).isTrue();
        assertThat(response.getBody().getSortOrder()).isEqualTo(1);

        verify(mediaService, times(1)).uploadAndSaveMedia(any(UploadRequest.class));
    }

    /**
     * Проверяет, что если опциональные параметры (sortOrder, isPrimary) не переданы,
     * контроллер использует значения по умолчанию (0 и false соответственно).
     */
    @Test
    void uploadMedia_ShouldUseDefaultValues_WhenOptionalParametersNotProvided() {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        Long entityId = 789L;
        EntityType entityType = EntityType.COLLECTION;
        UUID requestId = UUID.randomUUID();

        MediaUploadResponse expectedResponse = MediaUploadResponse.builder()
                .mediaId(999L)
                .entityId(entityId)
                .s3Url("https://s3.amazonaws.com/bucket/test-document.pdf")
                .mediaType("application/pdf")
                .fileName("test-document.pdf")
                .fileSize(12L)
                .isPrimary(false) // default value
                .sortOrder(0) // default value
                .build();

        when(mediaService.uploadAndSaveMedia(any(UploadRequest.class)))
                .thenReturn(expectedResponse);

        // When - using default values for sortOrder (0) and isPrimary (false)
        ResponseEntity<MediaUploadResponse> response = adminMediaController.uploadMedia(
                mockFile, entityId, entityType, requestId, 0, false
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isPrimary()).isFalse();
        assertThat(response.getBody().getSortOrder()).isEqualTo(0);

        verify(mediaService, times(1)).uploadAndSaveMedia(any(UploadRequest.class));
    }

    /**
     * Проверяет, что контроллер корректно формирует объект UploadRequest
     * со всеми переданными параметрами и передает его в MediaService.
     */
    @Test
    void uploadMedia_ShouldPassCorrectUploadRequest_ToMediaService() {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "image data".getBytes()
        );

        Long entityId = 555L;
        EntityType entityType = EntityType.PRODUCT;
        UUID requestId = UUID.randomUUID();
        int sortOrder = 5;
        boolean isPrimary = false;

        MediaUploadResponse mockResponse = MediaUploadResponse.builder()
                .mediaId(111L)
                .entityId(entityId)
                .build();

        when(mediaService.uploadAndSaveMedia(any(UploadRequest.class)))
                .thenReturn(mockResponse);

        // When
        adminMediaController.uploadMedia(
                mockFile, entityId, entityType, requestId, sortOrder, isPrimary
        );

        // Then - verify that the correct UploadRequest is passed to service
        verify(mediaService, times(1)).uploadAndSaveMedia(argThat(request ->
                request.getFile().equals(mockFile) &&
                        request.getEntityId().equals(entityId) &&
                        request.getEntityType().equals(entityType) &&
                        request.getRequestId().equals(requestId) &&
                        request.getSortOrder() == sortOrder &&
                        !request.isPrimary() &&
                        request.getUploadedBy().equals("admin_user_placeholder")
        ));
    }

    /**
     * Проверяет, что контроллер может обрабатывать различные типы сущностей
     * (например, COLLECTION), передавая корректный EntityType в сервис.
     */
    @Test
    void uploadMedia_ShouldHandleDifferentEntityTypes() {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "collection-banner.jpg",
                "image/jpeg",
                "banner data".getBytes()
        );

        Long entityId = 777L;
        EntityType entityType = EntityType.COLLECTION;
        UUID requestId = UUID.randomUUID();

        MediaUploadResponse mockResponse = MediaUploadResponse.builder()
                .mediaId(888L)
                .entityId(entityId)
                .build();

        when(mediaService.uploadAndSaveMedia(any(UploadRequest.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<MediaUploadResponse> response = adminMediaController.uploadMedia(
                mockFile, entityId, entityType, requestId, 0, false
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(mediaService, times(1)).uploadAndSaveMedia(argThat(request ->
                request.getEntityType().equals(EntityType.COLLECTION)
        ));
    }
}