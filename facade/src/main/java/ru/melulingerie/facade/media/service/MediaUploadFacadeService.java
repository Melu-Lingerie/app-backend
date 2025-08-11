package ru.melulingerie.facade.media.service;

import ru.melulingerie.facade.media.dto.UploadMediaRequestDto;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;

public interface MediaUploadFacadeService {
    
    UploadMediaResponseDto uploadMedia(UploadMediaRequestDto request);
}