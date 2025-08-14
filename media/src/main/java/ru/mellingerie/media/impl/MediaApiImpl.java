package ru.mellingerie.media.impl;

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
import ru.mellingerie.domain.Image;
import ru.mellingerie.domain.Media;
import ru.mellingerie.domain.MediaType;
import ru.mellingerie.domain.Video;
import ru.mellingerie.media.api.FileUploadService;
import ru.mellingerie.media.api.MediaApi;
import ru.mellingerie.media.dto.*;
import ru.mellingerie.media.exception.MediaProcessingException;
import ru.mellingerie.media.util.FileHashingUtil;
import ru.mellingerie.repository.MediaRepository;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaApiImpl implements MediaApi {

    private final FileUploadService fileUploadService;
    private final MediaRepository mediaRepository;
    private final FileValidationService fileValidationService;

    @Override
    public MediaResponseDto uploadMedia(MediaRequestDto request) {
        log.info("Processing upload request with ID: {}", request.requestId());

        fileValidationService.validateFile(request.file());

        try {
            String fileHash = FileHashingUtil.calculateSHA256(request.file());

            Optional<Media> duplicateMediaOpt = mediaRepository.findByFileHashAndEntityIdAndEntityType(fileHash);
            if (duplicateMediaOpt.isPresent()) {
                log.info("Duplicate record found for file hash {}. Skipping creation.", fileHash);
                return MediaResponseDto.fromEntity(duplicateMediaOpt.get());
            }

            Media media = buildMedia(request, fileHash);

            populateSpecificMetadata(media, request.file());

            Media savedMedia = persistMedia(media);
            log.info("Successfully saved NEW media with ID {}", savedMedia.getId());

            return MediaResponseDto.fromEntity(savedMedia);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash file for request ID {}", request.requestId(), e);
            throw new MediaProcessingException("Error hashing file content", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    protected Media persistMedia(Media media) {
        return mediaRepository.save(media);
    }

    private Media buildMedia(MediaRequestDto request, String fileHash) {
        Optional<Media> existingFileOpt = mediaRepository.findFirstByFileHash(fileHash);

        Media media = new Media();

        existingFileOpt.ifPresentOrElse(existingMedia -> {
            log.info("File with hash {} already exists. Reusing S3 object.", fileHash);
            media.setFileHash(existingMedia.getFileHash());
            media.setS3Bucket(existingMedia.getS3Bucket());
            media.setS3Key(existingMedia.getS3Key());
            media.setS3Url(existingMedia.getS3Url());
        }, () -> {
            MediaUploadResponseDto s3Result = fileUploadService.upload(request.file());
            media.setFileHash(fileHash);
            media.setS3Bucket(s3Result.bucket());
            media.setS3Key(s3Result.key());
            media.setS3Url(s3Result.url());
        });

        setCommonFields(media, request);
        return media;
    }

    private void populateSpecificMetadata(Media media, CustomMultipartFile file) {
        try (InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(file.content()))) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            if (media.getMediaType() == MediaType.IMAGE) {
                extractImageMetadata(metadata, media);
            } else if (media.getMediaType() == MediaType.VIDEO) {
                extractVideoMetadata(metadata, media);
            }
        } catch (ImageProcessingException | MetadataException e) {
            log.error("Failed to parse media metadata", e);
            throw new MediaProcessingException("Error parsing media metadata", e);
        } catch (IOException e) {
            log.error("IO error while reading media content for metadata extraction", e);
            throw new MediaProcessingException("I/O error reading file for metadata extraction", e);
        }
    }


    private void extractImageMetadata(Metadata metadata, Media media) {
        Image image = new Image();
        getDimensionFromImage(metadata).ifPresent(dim -> {
            image.setWidth(dim.width());
            image.setHeight(dim.height());
        });
        image.setMedia(media);
        media.setImage(image);
        log.info("Extracted image metadata: width={}, height={}", image.getWidth(), image.getHeight());
    }

    private void extractVideoMetadata(Metadata metadata, Media media) throws MetadataException {
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

    private Optional<Dimension> getDimensionFromImage(Metadata metadata) {
        try {
            JpegDirectory jpegDir = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            if (jpegDir != null) {
                return Optional.of(new Dimension(jpegDir.getImageWidth(), jpegDir.getImageHeight()));
            }
            PngDirectory pngDir = metadata.getFirstDirectoryOfType(PngDirectory.class);
            if (pngDir != null) {
                return Optional.of(new Dimension(pngDir.getInt(PngDirectory.TAG_IMAGE_WIDTH), pngDir.getInt(PngDirectory.TAG_IMAGE_HEIGHT)));
            }
        } catch (MetadataException e) {
            log.warn("Could not extract dimensions from image: {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }

    private void setCommonFields(Media media, MediaRequestDto request) {
        media.setFileName(request.file().originalFilename());
        media.setMimeType(request.file().contentType());
        media.setFileSize(request.file().size());
        media.setMediaType(fileValidationService.getMediaType(request.file()));
        media.setUploadedBy(request.uploadedBy());
    }
}