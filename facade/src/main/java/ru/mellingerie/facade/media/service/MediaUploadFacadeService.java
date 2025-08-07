package ru.mellingerie.facade.media.service;

import ru.mellingerie.facade.media.dto.MediaUploadFacadeResponse;
import ru.mellingerie.facade.media.dto.UploadFacadeRequest;

public interface MediaUploadFacadeService {
    MediaUploadFacadeResponse uploadAndSaveMedia(UploadFacadeRequest request);
}
