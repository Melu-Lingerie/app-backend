package ru.melulingerie.facade.media.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.media.dto.CustomMultipartFileFacadeDto;
import ru.melulingerie.facade.media.dto.MediaApiResponseDto;
import ru.melulingerie.facade.media.dto.MediaFacadeRequestDto;
import ru.mellingerie.media.dto.*;

@Mapper(config = MapStructConfig.class)
public interface MediaFacadeMapper {

    @Mapping(target = "file", source = "file", qualifiedByName = "mapFile")
    MediaRequestDto toMediaRequestDto(MediaFacadeRequestDto facadeRequest);

    MediaApiResponseDto toMediaApiResponseDto(MediaResponseDto coreResponse);

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