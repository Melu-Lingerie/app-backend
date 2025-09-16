package ru.melulingerie.facade.media.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.media.dto.MediaGetInfoFacadeResponseDto;
import ru.melulingerie.facade.media.dto.UploadMultipartFileDto;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;
import ru.melulingerie.facade.media.dto.UploadMediaRequestDto;
import ru.melulingerie.media.dto.*;

@Mapper(config = MapStructConfig.class)
public interface MediaMapper {

    @Mapping(target = "file", source = "file", qualifiedByName = "mapFile")
    MediaRequestDto toMediaRequestDto(UploadMediaRequestDto facadeRequest);

    UploadMediaResponseDto toMediaApiResponseDto(MediaResponseDto coreResponse);

    MediaGetInfoFacadeResponseDto toMediaInfo(MediaGetInfoResponseDto coreResponse);

    @Named("mapFile")
    default CustomMultipartFile mapFile(UploadMultipartFileDto facadeFile) {
        if (facadeFile == null) {
            return null;
        }
        return CustomMultipartFile.builder()
                .inputStream(facadeFile.inputStream())
                .originalFilename(facadeFile.originalFilename())
                .contentType(facadeFile.contentType())
                .size(facadeFile.size())
                .name(facadeFile.name())
                .build();
    }
}