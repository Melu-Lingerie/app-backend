package ru.melulingerie.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        FileKeyGenerator fileKeyGenerator = new FileKeyGenerator();

        fileUploadService = new FileUploadService(s3Client, fileKeyGenerator);
        ReflectionTestUtils.setField(fileUploadService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", "https://%s.storage.yandexcloud.net/%s");
    }

    /**
     * Проверяет успешную загрузку валидного файла.
     * Убеждается, что сервис возвращает корректный результат UploadResult с правильными
     * именем бакета, ключом S3 и URL-адресом файла. Также проверяет, что S3 клиент был вызван.
     */
    @Test
    void upload_ShouldSuccessfullyUploadFile_WhenValidFileProvided() throws IOException {
        byte[] fileContent = "test image content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "original-image.jpg",
                "image/jpeg",
                fileContent
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UploadResult result = fileUploadService.upload(mockFile);

        assertThat(result).isNotNull();
        assertThat(result.getBucket()).isEqualTo("test-bucket");
        assertThat(result.getKey()).startsWith("original-image_"); // Реально сгенерированный ключ
        assertThat(result.getKey()).endsWith(".jpg");
        assertThat(result.getUrl()).startsWith("https://test-bucket.storage.yandexcloud.net/original-image_");
        assertThat(result.getUrl()).endsWith(".jpg");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    /**
     * Проверяет, что при загрузке файла создается корректный запрос PutObjectRequest для S3.
     * Использует ArgumentCaptor для захвата запроса и проверки его полей:
     * имя бакета, ключ, тип контента, размер и ACL (Access Control List).
     */
    @Test
    void upload_ShouldCreateCorrectPutObjectRequest_WhenCalled() throws IOException {
        byte[] fileContent = "PDF document content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "document",
                "important-doc.pdf",
                "application/pdf",
                fileContent
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        fileUploadService.upload(mockFile);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo("test-bucket");
        assertThat(capturedRequest.key()).startsWith("important-doc_"); // Реально сгенерированный ключ
        assertThat(capturedRequest.key()).endsWith(".pdf");
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
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "vacation.mp4",
                "video/mp4",
                "video binary content".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UploadResult result = fileUploadService.upload(videoFile);

        assertThat(result.getKey()).startsWith("vacation_");
        assertThat(result.getKey()).endsWith(".mp4");
        assertThat(result.getUrl()).contains("vacation_");
        assertThat(result.getUrl()).endsWith(".mp4");

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
        String customUrlTemplate = "https://cdn.example.com/%s/%s"; // Custom CDN template
        ReflectionTestUtils.setField(fileUploadService, "publicUrlTemplate", customUrlTemplate);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "logo.png",
                "image/png",
                "logo content".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UploadResult result = fileUploadService.upload(mockFile);

        assertThat(result.getUrl()).startsWith("https://cdn.example.com/test-bucket/logo_");
        assertThat(result.getUrl()).endsWith(".png");
    }

    /**
     * Проверяет обработку ошибки, когда невозможно прочитать входной поток файла.
     * Тест ожидает, что будет выброшено исключение IOException, и проверяет,
     * что отправка объекта в S3 не производилась.
     */
    @Test
    void upload_ShouldThrowIOException_WhenFileInputStreamFails() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("failing-file.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getInputStream()).thenThrow(new IOException("Failed to read file stream"));

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
        MockMultipartFile emptyFile = new MockMultipartFile(
                "empty",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UploadResult result = fileUploadService.upload(emptyFile);

        assertThat(result).isNotNull();
        assertThat(result.getKey()).startsWith("empty_");
        assertThat(result.getKey()).endsWith(".txt");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.contentLength()).isEqualTo(0L);
    }

    /**
     * Проверяет, что сервис корректно обрабатывает имена файлов, содержащие спецсимволы,
     * пробелы и кириллицу. Убеждается, что FileKeyGenerator обрабатывает имя файла.
     */
    @Test
    void upload_ShouldHandleSpecialCharactersInFilename_Correctly() throws IOException {
        MockMultipartFile fileWithSpecialChars = new MockMultipartFile(
                "file",
                "файл с пробелами & символами (1).jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UploadResult result = fileUploadService.upload(fileWithSpecialChars);

        assertThat(result.getKey()).startsWith("_____");
        assertThat(result.getKey()).contains("_");
        assertThat(result.getKey()).endsWith(".jpg");
        assertThat(result.getKey()).matches(".*_\\d{8}_\\d{6}_\\d{3}_[a-f0-9]{8}\\.jpg");
    }

    /**
     * Проверяет, что в S3 клиент передается корректное тело запроса (RequestBody).
     * Тест захватывает RequestBody и проверяет, что он не null, что подтверждает
     * передачу содержимого файла для загрузки.
     */
    @Test
    void upload_ShouldPassCorrectRequestBodyToS3Client() throws IOException {
        byte[] testContent = "specific test content for verification".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "testFile",
                "test.txt",
                "text/plain",
                testContent
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        fileUploadService.upload(mockFile);

        ArgumentCaptor<RequestBody> requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), requestBodyCaptor.capture());

        RequestBody capturedRequestBody = requestBodyCaptor.getValue();
        assertThat(capturedRequestBody).isNotNull();
    }

    /**
     * Проверяет, что FileKeyGenerator генерирует правильный ключ для S3.
     * Тест убеждается, что ключ имеет правильный формат и содержит оригинальное имя файла.
     */
    @Test
    void upload_ShouldGenerateCorrectS3Key_WhenCalled() throws IOException {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "document content".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        fileUploadService.upload(mockFile);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.key()).startsWith("test-document_");
        assertThat(capturedRequest.key()).endsWith(".pdf");
        assertThat(capturedRequest.key()).matches("test-document_\\d{8}_\\d{6}_\\d{3}_[a-f0-9]{8}\\.pdf");
    }
}