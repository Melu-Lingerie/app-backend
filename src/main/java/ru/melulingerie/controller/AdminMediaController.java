package ru.melulingerie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Manages media content for products on behalf of administrators.
 * This controller provides endpoints for uploading, deleting, and modifying
 * images associated with products. All operations are designed
 * to be idempotent to ensure data consistency in distributed systems.
 * For example, to upload a new image for a product, a client would
 * send a POST request to /api/v1/products/{productId}/media/upload.
 */
@RestController
@RequestMapping("/api/v1/products")
public final class AdminMediaController {

    /**
     * Uploads a new media file for a specific product.
     * The request is idempotent, meaning that multiple identical requests
     * will have the same effect as a single request. This is achieved
     * through the use of a unique request ID. The server will also perform
     * deduplication by calculating the SHA-256 hash of the file to avoid
     * storing duplicate files.
     * @param productId The unique identifier of the product.
     * @param requestId The unique identifier for the request, for idempotency.
     * @param mediaType The type of media, e.g., "IMAGE" or "VIDEO".
     * @param sortOrder The order of the media file in listings.
     * @param isPrimary Indicates if this is the primary media file.
     * @param isActive Indicates if the media file is active.
     * @param file The multipart file to be uploaded.
     * @return An empty response with a status code indicating success.
     */
    @PostMapping("/{productId}/media/upload")
    public ResponseEntity<Void> upload(
        @PathVariable final UUID productId,
        @RequestParam("request_id") final UUID requestId,
        @RequestParam("mediaType") final String mediaType,
        @RequestParam(value = "sortOrder", defaultValue = "0") final int sortOrder,
        @RequestParam(value = "isPrimary", defaultValue = "false") final boolean isPrimary,
        @RequestParam(value = "is_active", defaultValue = "true") final boolean isActive,
        @RequestParam("file") final MultipartFile file
    ) {
        // TODO: Implement the logic for file upload, including idempotency and deduplication.
        return ResponseEntity.ok().build();
    }
}