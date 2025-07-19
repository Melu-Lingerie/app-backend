package ru.melulingerie.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.domain.*;
import ru.melulingerie.dto.MediaUploadRequest;
import ru.melulingerie.repository.IdempotencyKeyRepository;
import ru.melulingerie.repository.ImageRepository;
import ru.melulingerie.repository.ProductMediaRepository;
import ru.melulingerie.repository.VideoRepository;
import ru.melulingerie.service.MediaService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implements the media management functionalities.
 * This service contains the business logic for handling file uploads,
 * ensuring idempotency, and preventing data duplication. It coordinates
 * interactions between repositories and external services like S3.
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final ProductMediaRepository productMediaRepository;
    private final ImageRepository imageRepository;
    private final VideoRepository videoRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    /**
     * Orchestrates the file upload process.
     * This method uses a transactional boundary to ensure all database
     * operations succeed or fail together. It checks for idempotency,
     * uploads the file, and creates the corresponding media records.
     *
     * @param request The DTO containing upload data.
     */
    @Override
    @Transactional
    public void uploadFile(MediaUploadRequest request) {
        final String idempotencyKey = request.requestId().toString();
        final Optional<IdempotencyKeyEntity> existingKey = this.idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey);
        if (existingKey.isPresent()) {
            // Request already processed or in progress, return.
            return;
        }
        this.createIdempotencyKey(idempotencyKey);
        try {
            final String fileHash = calculateSha256(request.file());
            final Optional<ProductMediaEntity> existingMedia = this.productMediaRepository.findByFileHash(fileHash);

            if (existingMedia.isPresent()) {
                // File already exists, link it without re-uploading
                this.createMediaFromExisting(request, existingMedia.get());
            } else {
                // New file, upload and create new records
                this.createNewMedia(request, fileHash);
            }
            this.updateIdempotencyKey(idempotencyKey, ProcessingStatus.COMPLETED);
        } catch (final Exception e) {
            this.updateIdempotencyKey(idempotencyKey, ProcessingStatus.FAILED);
            // Re-throw exception to be handled by a global exception handler
            throw new RuntimeException("Failed to process file upload", e);
        }
    }

    private void createNewMedia(MediaUploadRequest request, String fileHash) {
        // TODO: Implement actual file upload to S3 and get URL and key
        final String s3Url = "https://s3.bucket.url/" + request.file().getOriginalFilename();
        final String s3Key = "media/" + request.file().getOriginalFilename();

        final ProductMediaEntity mediaEntity = new ProductMediaEntity();
        mediaEntity.setProductId(request.productId());
        mediaEntity.setMediaType(MediaType.valueOf(request.mediaType()));
        mediaEntity.setFileName(request.file().getOriginalFilename());
        mediaEntity.setMimeType(request.file().getContentType());
        mediaEntity.setFileSize(request.file().getSize());
        mediaEntity.setFileHash(fileHash);
        mediaEntity.setS3Bucket("my-bucket"); // Example bucket
        mediaEntity.setS3Key(s3Key);
        mediaEntity.setS3Url(s3Url);
        mediaEntity.setSortOrder(request.sortOrder());
        mediaEntity.setPrimary(request.isPrimary());
        mediaEntity.setActive(request.isActive());

        final ProductMediaEntity savedMedia = this.productMediaRepository.save(mediaEntity);

        this.createSpecificMediaRecord(savedMedia, request.mediaType());
    }

    private void createMediaFromExisting(MediaUploadRequest request, ProductMediaEntity existingMedia) {
        final ProductMediaEntity newMediaLink = new ProductMediaEntity();
        newMediaLink.setProductId(request.productId());
        newMediaLink.setMediaType(MediaType.valueOf(request.mediaType()));
        newMediaLink.setFileName(existingMedia.getFileName());
        newMediaLink.setMimeType(existingMedia.getMimeType());
        newMediaLink.setFileSize(existingMedia.getFileSize());
        newMediaLink.setFileHash(existingMedia.getFileHash());
        newMediaLink.setS3Bucket(existingMedia.getS3Bucket());
        newMediaLink.setS3Key(existingMedia.getS3Key());
        newMediaLink.setS3Url(existingMedia.getS3Url());
        newMediaLink.setSortOrder(request.sortOrder());
        newMediaLink.setPrimary(request.isPrimary());
        newMediaLink.setActive(request.isActive());

        final ProductMediaEntity savedMedia = this.productMediaRepository.save(newMediaLink);
        this.createSpecificMediaRecord(savedMedia, request.mediaType());
    }

    private void createSpecificMediaRecord(ProductMediaEntity mediaEntity, String mediaType) {
        // TODO: Implement actual metadata extraction (e.g., using a library)
        if (MediaType.valueOf(mediaType) == MediaType.IMAGE) {
            final ImageEntity image = new ImageEntity();
            image.setMedia(mediaEntity);
            image.setWidth(1920);
            image.setHeight(1080);
            this.imageRepository.save(image);
        } else if (MediaType.valueOf(mediaType) == MediaType.VIDEO) {
            final VideoEntity video = new VideoEntity();
            video.setMedia(mediaEntity);
            video.setWidth(1920);
            video.setHeight(1080);
            video.setDuration(300);
            this.videoRepository.save(video);
        }
    }

    private String calculateSha256(final MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return DigestUtils.sha256Hex(is);
        }
    }

    private void createIdempotencyKey(final String key) {
        final IdempotencyKeyEntity keyEntity = new IdempotencyKeyEntity();
        keyEntity.setIdempotencyKey(key);
        keyEntity.setProcessingStatus(ProcessingStatus.PROCESSING);
        keyEntity.setExpiresAt(LocalDateTime.now().plusHours(24));
        this.idempotencyKeyRepository.save(keyEntity);
    }

    private void updateIdempotencyKey(final String key, final ProcessingStatus status) {
        this.idempotencyKeyRepository.findByIdempotencyKey(key).ifPresent(entity -> {
            entity.setProcessingStatus(status);
            this.idempotencyKeyRepository.save(entity);
        });
    }
}