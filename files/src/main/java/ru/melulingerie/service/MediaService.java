package ru.melulingerie.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
import com.drew.metadata.png.PngDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.domain.Image;
import ru.melulingerie.domain.Media;
import ru.melulingerie.domain.MediaType;
import ru.melulingerie.domain.Video;
import ru.melulingerie.dto.Dimension;
import ru.melulingerie.dto.MediaUploadResponse;
import ru.melulingerie.dto.UploadRequest;
import ru.melulingerie.dto.UploadResult;
import ru.melulingerie.repository.MediaRepository;
import ru.melulingerie.util.FileHashingUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaService {

    private final FileUploadService fileUploadService;
    private final MediaRepository mediaRepository;
    private final FileValidationService fileValidationService;

    @Transactional
    public MediaUploadResponse uploadAndSaveMedia(UploadRequest request) {
        log.info("Processing upload request with ID: {}", request.getRequestId());
        fileValidationService.validateSingleFile(request.getFile());

        try {
            String fileHash = FileHashingUtil.calculateSHA256(request.getFile());

            Optional<Media> duplicateMediaOpt = mediaRepository.findByFileHashAndEntityIdAndEntityType(
                    fileHash, request.getEntityId(), request.getEntityType()
            );

            if (duplicateMediaOpt.isPresent()) {
                log.info("Duplicate record found for file hash {} and entity {}/{}. Skipping creation.",
                        fileHash, request.getEntityType(), request.getEntityId());
                return MediaUploadResponse.fromEntity(duplicateMediaOpt.get());
            }

            Optional<Media> existingFileOpt = mediaRepository.findFirstByFileHash(fileHash);

            Media media;
            if (existingFileOpt.isPresent()) {
                log.info("File with hash {} already exists. Reusing S3 object.", fileHash);
                media = createMediaEntityFromExisting(request, existingFileOpt.get());
            } else {
                UploadResult s3Result = fileUploadService.upload(request.getFile());
                media = createNewMediaEntity(request, fileHash, s3Result);
            }

            populateSpecificMetadata(media, request.getFile());

            Media savedMedia = mediaRepository.save(media);
            log.info("Successfully saved NEW media with ID {} for entity {}/{}", savedMedia.getId(), savedMedia.getEntityType(), savedMedia.getEntityId());

            return MediaUploadResponse.fromEntity(savedMedia);

        } catch (IOException | NoSuchAlgorithmException | ImageProcessingException | MetadataException e) {
            log.error("Failed to process file for request ID {}", request.getRequestId(), e);
            throw new RuntimeException("Error processing file metadata", e);
        }
    }


    private void populateSpecificMetadata(Media media, MultipartFile file)
            throws ImageProcessingException, IOException, MetadataException {
        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            if (media.getMediaType() == MediaType.IMAGE) {
                Image image = new Image();
                getDimensionFromImage(metadata).ifPresent(dim -> {
                    image.setWidth(dim.width());
                    image.setHeight(dim.height());
                });
                image.setMedia(media);
                media.setImage(image);
                log.info("Extracted image metadata: width={}, height={}", image.getWidth(), image.getHeight());

            } else if (media.getMediaType() == MediaType.VIDEO) {
                Video video = new Video();
                Mp4VideoDirectory videoTrackDirectory = metadata.getFirstDirectoryOfType(Mp4VideoDirectory.class);
                if (videoTrackDirectory != null) {
                    if (videoTrackDirectory.containsTag(Mp4VideoDirectory.TAG_WIDTH) && videoTrackDirectory.containsTag(Mp4VideoDirectory.TAG_HEIGHT)) {
                        video.setWidth(videoTrackDirectory.getInteger(Mp4VideoDirectory.TAG_WIDTH));
                        video.setHeight(videoTrackDirectory.getInteger(Mp4VideoDirectory.TAG_HEIGHT));
                    }
                }
                Mp4Directory containerDirectory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
                if (containerDirectory != null && containerDirectory.containsTag(Mp4Directory.TAG_DURATION)) {
                    long durationInMillis = containerDirectory.getLong(Mp4Directory.TAG_DURATION);
                    long durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis);
                    video.setDuration((int) durationInSeconds);
                }
                video.setMedia(media);
                media.setVideo(video);
                log.info("Extracted video metadata: width={}, height={}, duration={}s", video.getWidth(), video.getHeight(), video.getDuration());
            }
        }
    }

    private Optional<Dimension> getDimensionFromImage(Metadata metadata) {
        try {
            if (metadata.getFirstDirectoryOfType(JpegDirectory.class) != null) {
                JpegDirectory dir = metadata.getFirstDirectoryOfType(JpegDirectory.class);
                return Optional.of(new Dimension(dir.getImageWidth(), dir.getImageHeight()));
            } else if (metadata.getFirstDirectoryOfType(PngDirectory.class) != null) {
                PngDirectory dir = metadata.getFirstDirectoryOfType(PngDirectory.class);
                return Optional.of(new Dimension(dir.getInt(PngDirectory.TAG_IMAGE_WIDTH), dir.getInt(PngDirectory.TAG_IMAGE_HEIGHT)));
            }
        } catch (MetadataException e) {
            log.warn("Could not extract dimensions from image: {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Media createNewMediaEntity(UploadRequest request, String fileHash, UploadResult s3Result) {
        Media media = new Media();
        media.setFileHash(fileHash);
        media.setS3Bucket(s3Result.getBucket());
        media.setS3Key(s3Result.getKey());
        media.setS3Url(s3Result.getUrl());
        setCommonFields(media, request);
        return media;
    }

    private Media createMediaEntityFromExisting(UploadRequest request, Media existingMedia) {
        Media media = new Media();
        media.setFileHash(existingMedia.getFileHash());
        media.setS3Bucket(existingMedia.getS3Bucket());
        media.setS3Key(existingMedia.getS3Key());
        media.setS3Url(existingMedia.getS3Url());
        setCommonFields(media, request);
        return media;
    }

    private void setCommonFields(Media media, UploadRequest request) {
        media.setEntityId(request.getEntityId());
        media.setEntityType(request.getEntityType());
        media.setFileName(request.getFile().getOriginalFilename());
        media.setMimeType(request.getFile().getContentType());
        media.setFileSize(request.getFile().getSize());
        media.setMediaType(fileValidationService.getMediaType(request.getFile()));
        media.setSortOrder(request.getSortOrder());
        media.setPrimary(request.isPrimary());
        media.setUploadedBy(request.getUploadedBy());
    }
}