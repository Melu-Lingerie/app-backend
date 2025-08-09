package ru.mellingerie.facade.media.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.mellingerie.facade.config.MapStructConfig;
import ru.mellingerie.facade.media.dto.CustomMultipartFileFacadeDto;
import ru.mellingerie.facade.media.dto.EntityTypeFacadeDto;
import ru.mellingerie.facade.media.dto.MediaUploadFacadeResponse;
import ru.mellingerie.facade.media.dto.UploadFacadeRequest;
import ru.melulingerie.files.domain.EntityType;
import ru.melulingerie.files.dto.CustomMultipartFile;
import ru.melulingerie.files.dto.MediaUploadResponse;
import ru.melulingerie.files.dto.UploadRequest;

@Mapper(config = MapStructConfig.class)
public interface MediaFacadeMapper {

    @Mapping(target = "file", source = "file", qualifiedByName = "mapFile")
    @Mapping(target = "entityType", source = "entityType", qualifiedByName = "mapEntityType")
    UploadRequest toCoreUploadRequest(UploadFacadeRequest facadeRequest);

    MediaUploadFacadeResponse toFacadeMediaUploadResponse(MediaUploadResponse coreResponse);

    @Named("mapEntityType")
    default EntityType mapEntityType(EntityTypeFacadeDto facadeEntityType) {
        if (facadeEntityType == null) {
            return null;
        }
        return EntityType.valueOf(facadeEntityType.name());
    }

    @Named("mapFile")
    default CustomMultipartFile mapFile(CustomMultipartFileFacadeDto facadeFile) {
        if (facadeFile == null) {
            return null;
        }
        return CustomMultipartFile.builder()
                .content(facadeFile.content())
                .originalFilename(facadeFile.originalFilename())
                .contentType(facadeFile.contentType())
                .size(facadeFile.size())
                .name(facadeFile.name())
                .build();
    }
}