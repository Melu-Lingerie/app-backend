package ru.melulingerie.media.api;

import ru.melulingerie.media.dto.CustomMultipartFile;
import ru.melulingerie.media.dto.MediaUploadResponseDto;

public interface FileUploadService {
    MediaUploadResponseDto upload(CustomMultipartFile file);
}
