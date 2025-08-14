package ru.mellingerie.media.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mellingerie.media.api.FileUploadService;
import ru.mellingerie.media.dto.CustomMultipartFile;
import ru.mellingerie.media.dto.MediaUploadResponseDto;
import ru.mellingerie.media.util.FileKeyGenerator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;

@Service
@Slf4j
@RequiredArgsConstructor
class FileUploadServiceImpl implements FileUploadService {

    private final S3Client s3Client;
    private final FileKeyGenerator fileKeyGenerator;

    @Value("${yandex.storage.bucket-name}")
    private String bucketName;

    @Value("${yandex.storage.public-url-template:https://%s.storage.yandexcloud.net/%s}")
    private String publicUrlTemplate;

    public MediaUploadResponseDto upload(CustomMultipartFile file) {
        String key = fileKeyGenerator.generate(file.originalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.contentType())
                .contentLength(file.size())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(new ByteArrayInputStream(file.content()), file.size()));

        String url = String.format(publicUrlTemplate, bucketName, key);
        log.info("File {} uploaded to S3. URL: {}", file.originalFilename(), url);

        return MediaUploadResponseDto.builder()
                .bucket(bucketName)
                .key(key)
                .url(url)
                .build();
    }

}