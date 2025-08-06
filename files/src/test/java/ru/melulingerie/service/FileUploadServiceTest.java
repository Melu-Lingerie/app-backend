package ru.melulingerie.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.dto.UploadResult;
import ru.melulingerie.util.FileKeyGenerator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private FileKeyGenerator fileKeyGenerator;

    @InjectMocks
    private FileUploadService fileUploadService;

    /**
     * Проверяет успешную загрузку валидного файла.
     * Убеждается, что сервис возвращает корректный результат UploadResult с правильными
     * именем бакета, ключом S3 и URL-адресом файла. Также проверяет, что
     * генератор ключей и S3 клиент были вызваны.
     */
    @Test
    void upload_ShouldSuccessfullyUploadFile_WhenValidFileProvided() throws IOException {
        // Given
        String bucketName = "test-bucket";
        String publicUrlTemplate = "https://%s.storage.yandexcloud.net/%s";
        String generatedKey = "uploads/2023/12/unique-file-name.jpg";
        String expectedUrl = "https://test-bucket.storage.yandexcloud.net/uploads/2023/12/unique-file-name.jpg";

        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", publicUrlTemplate);

        byte[] fileContent = "test image content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "original-image.jpg",
                "image/jpeg",
                fileContent
        );

        when(fileKeyGenerator.generate("original-image.jpg")).thenReturn(generatedKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        UploadResult result = fileUploadService.upload(mockFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBucket()).isEqualTo(bucketName);
        assertThat(result.getKey()).isEqualTo(generatedKey);
        assertThat(result.getUrl()).isEqualTo(expectedUrl);

        verify(fileKeyGenerator, times(1)).generate("original-image.jpg");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    /**
     * Проверяет, что при загрузке файла создается корректный запрос PutObjectRequest для S3.
     * Использует ArgumentCaptor для захвата запроса и проверки его полей:
     * имя бакета, ключ, тип контента, размер и ACL (Access Control List).
     */
    @Test
    void upload_ShouldCreateCorrectPutObjectRequest_WhenCalled() throws IOException {
        // Given
        String bucketName = "my-storage-bucket";
        String generatedKey = "files/2023/document.pdf";

        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        byte[] fileContent = "PDF document content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "document",
                "important-doc.pdf",
                "application/pdf",
                fileContent
        );

        when(fileKeyGenerator.generate("important-doc.pdf")).thenReturn(generatedKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        fileUploadService.upload(mockFile);

        // Then - используем ArgumentCaptor для проверки параметров
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(bucketName);
        assertThat(capturedRequest.key()).isEqualTo(generatedKey);
        assertThat(capturedRequest.contentType()).isEqualTo("application/pdf");
        assertThat(capturedRequest.contentLength()).isEqualTo((long) fileContent.length);
        assertThat(capturedRequest.acl()).isEqualTo(ObjectCannedACL.PUBLIC_READ);
    }

    /**
     * Проверяет корректную обработку различных типов файлов.
     * В данном тесте используется видеофайл (video/mp4) для проверки того,
     * что сервис правильно устанавливает ContentType в запросе к S3.
     */
    @Test
    void upload_ShouldHandleDifferentFileTypes_Correctly() throws IOException {
        // Given
        String bucketName = "media-bucket";
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        // Test with video file
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "vacation.mp4",
                "video/mp4",
                "video binary content".getBytes()
        );

        String videoKey = "videos/vacation-processed.mp4";
        when(fileKeyGenerator.generate("vacation.mp4")).thenReturn(videoKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        UploadResult result = fileUploadService.upload(videoFile);

        // Then
        assertThat(result.getKey()).isEqualTo(videoKey);
        assertThat(result.getUrl()).contains("videos/vacation-processed.mp4");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.contentType()).isEqualTo("video/mp4");
    }

    /**
     * Проверяет, что сервис использует кастомный шаблон URL, если он сконфигурирован.
     * Это полезно для интеграции с CDN. Тест убеждается, что итоговый URL
     * файла форматируется согласно заданному шаблону.
     */
    @Test
    void upload_ShouldUseCustomUrlTemplate_WhenConfigured() throws IOException {
        // Given
        String bucketName = "custom-bucket";
        String customUrlTemplate = "https://cdn.example.com/%s/%s"; // Custom CDN template

        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", customUrlTemplate);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "logo.png",
                "image/png",
                "logo content".getBytes()
        );

        String generatedKey = "assets/logo-2023.png";
        when(fileKeyGenerator.generate("logo.png")).thenReturn(generatedKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        UploadResult result = fileUploadService.upload(mockFile);

        // Then
        String expectedCustomUrl = "https://cdn.example.com/custom-bucket/assets/logo-2023.png";
        assertThat(result.getUrl()).isEqualTo(expectedCustomUrl);
    }

    /**
     * Проверяет обработку ошибки, когда невозможно прочитать входной поток файла.
     * Тест ожидает, что будет выброшено исключение IOException, и проверяет,
     * что отправка объекта в S3 не производилась.
     */
    @Test
    void upload_ShouldThrowIOException_WhenFileInputStreamFails() throws IOException {
        // Given
        String bucketName = "test-bucket";
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("failing-file.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getInputStream()).thenThrow(new IOException("Failed to read file stream"));

        when(fileKeyGenerator.generate("failing-file.jpg")).thenReturn("uploads/failing-file.jpg");

        // When & Then
        assertThatThrownBy(() -> fileUploadService.upload(mockFile))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Failed to read file stream");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    /**
     * Проверяет корректную обработку пустого файла (0 байт).
     * Убеждается, что сервис обрабатывает такой файл без ошибок и
     * в запросе к S3 ContentLength корректно устанавливается в 0.
     */
    @Test
    void upload_ShouldHandleEmptyFile_Correctly() throws IOException {
        // Given
        String bucketName = "empty-files-bucket";
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        MockMultipartFile emptyFile = new MockMultipartFile(
                "empty",
                "empty.txt",
                "text/plain",
                new byte[0] // Empty file
        );

        String emptyKey = "empty-files/empty.txt";
        when(fileKeyGenerator.generate("empty.txt")).thenReturn(emptyKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        UploadResult result = fileUploadService.upload(emptyFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo(emptyKey);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.contentLength()).isEqualTo(0L);
    }

    /**
     * Проверяет, что сервис корректно обрабатывает имена файлов, содержащие спецсимволы,
     * пробелы и кириллицу. Убеждается, что генератор ключей вызывается с
     * оригинальным именем файла.
     */
    @Test
    void upload_ShouldHandleSpecialCharactersInFilename_Correctly() throws IOException {
        // Given
        String bucketName = "special-chars-bucket";
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        MockMultipartFile fileWithSpecialChars = new MockMultipartFile(
                "file",
                "файл с пробелами & символами (1).jpg", // Cyrillic, spaces, special chars
                "image/jpeg",
                "image content".getBytes()
        );

        String processedKey = "images/processed-file-name.jpg";
        when(fileKeyGenerator.generate("файл с пробелами & символами (1).jpg")).thenReturn(processedKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        UploadResult result = fileUploadService.upload(fileWithSpecialChars);

        // Then
        assertThat(result.getKey()).isEqualTo(processedKey);
        verify(fileKeyGenerator, times(1)).generate("файл с пробелами & символами (1).jpg");
    }

    /**
     * Проверяет, что в S3 клиент передается корректное тело запроса (RequestBody).
     * Тест захватывает RequestBody и проверяет, что он не null, что подтверждает
     * передачу содержимого файла для загрузки.
     */
    @Test
    void upload_ShouldPassCorrectRequestBodyToS3Client() throws IOException {
        // Given
        String bucketName = "request-body-test";
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        byte[] testContent = "specific test content for verification".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "testFile",
                "test.txt",
                "text/plain",
                testContent
        );

        when(fileKeyGenerator.generate("test.txt")).thenReturn("test/test.txt");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        fileUploadService.upload(mockFile);

        // Then
        ArgumentCaptor<RequestBody> requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), requestBodyCaptor.capture());

        RequestBody capturedRequestBody = requestBodyCaptor.getValue();
        assertThat(capturedRequestBody).isNotNull();
    }

    /**
     * Проверяет, что FileKeyGenerator вызывается и генерирует правильный ключ для S3.
     * Тест убеждается, что ключ, сгенерированный моком FileKeyGenerator,
     * используется в итоговом запросе PutObjectRequest.
     */
    @Test
    void upload_ShouldGenerateCorrectS3Key_WhenCalled() throws IOException {
        // Given
        String bucketName = "key-generation-test";
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "document content".getBytes()
        );

        String expectedKey = "documents/2023/12/test-document-uuid.pdf";
        when(fileKeyGenerator.generate("test-document.pdf")).thenReturn(expectedKey);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        fileUploadService.upload(mockFile);

        // Then
        verify(fileKeyGenerator, times(1)).generate("test-document.pdf");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.key()).isEqualTo(expectedKey);
    }
}