package ru.melulingerie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.dto.UploadResult;
import ru.melulingerie.util.FileKeyGenerator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final FileKeyGenerator fileKeyGenerator;

    @Value("${yandex.storage.bucket-name}")
    private String bucketName;

    @Value("${yandex.storage.public-url-template:https://%s.storage.yandexcloud.net/%s}")
    private String publicUrlTemplate;

    public UploadResult upload(MultipartFile file) throws IOException {
        String key = fileKeyGenerator.generate(file.getOriginalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String url = String.format(publicUrlTemplate, bucketName, key);
        log.info("File {} uploaded to S3. URL: {}", file.getOriginalFilename(), url);

        return UploadResult.builder()
                .bucket(bucketName)
                .key(key)
                .url(url)
                .build();
    }

}