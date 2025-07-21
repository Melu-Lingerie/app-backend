package ru.melulingerie.service;

import ru.melulingerie.dto.MediaUploadRequest;

/**
 * Defines the contract for media management operations.
 * This interface outlines the core functionalities for handling media files,
 * such as uploading, processing, and associating them with products,
 * while ensuring compliance with business rules like idempotency and
 * deduplication.
 */
public interface MediaService {

    /**
     * Handles the upload of a new media file for a product.
     * This method orchestrates the entire upload process, including
     * validating the request, checking for idempotency, calculating the
     * file hash for deduplication, saving the file to storage (e.g., S3),
     * and persisting the media metadata to the database.
     * @param request The DTO containing all request parameters and the file.
     */
    void uploadFile(MediaUploadRequest request);
}