package ru.melulingerie.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * A Data Transfer Object for carrying media upload request data.
 * This class encapsulates all the parameters from the upload request,
 * including product and request identifiers, media attributes, and the
 * file itself, providing a clean way to pass data between the controller
 * and service layers.
 *
 * @param productId The unique identifier of the product.
 * @param requestId The unique identifier for the request, for idempotency.
 * @param mediaType The type of media, e.g., "IMAGE" or "VIDEO".
 * @param sortOrder The order of the media file in listings.
 * @param isPrimary Indicates if this is the primary media file.
 * @param isActive  Indicates if the media file is active.
 * @param file      The multipart file to be uploaded.
 */
public record MediaUploadRequest(
        UUID productId,
        UUID requestId,
        String mediaType,
        int sortOrder,
        boolean isPrimary,
        boolean isActive,
        MultipartFile file) {
}