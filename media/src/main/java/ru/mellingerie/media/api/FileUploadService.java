package ru.mellingerie.media.api;

import ru.mellingerie.media.dto.CustomMultipartFile;
import ru.mellingerie.media.dto.MediaUploadResponseDto;

public interface FileUploadService {
    MediaUploadResponseDto upload(CustomMultipartFile file);
}
