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

    @Mapping(target = "fileId", expression = "java(coreResponse.mediaId() != null ? java.util.UUID.randomUUID() : null)")
    @Mapping(target = "url", source = "s3Url")
    @Mapping(target = "message", constant = "Файл успешно загружен")
    UploadMediaResponseDto toMediaApiResponseDto(MediaResponseDto coreResponse);

    MediaGetInfoFacadeResponseDto toMediaInfo(MediaGetInfoResponseDto coreResponse);

    @Named("mapFile")
    default CustomMultipartFile mapFile(UploadMultipartFileDto facadeFile) {
        if (facadeFile == null) {
            return null;
        }
        try {
            byte[] content = facadeFile.inputStream().readAllBytes();
            return CustomMultipartFile.builder()
                    .content(content)
                    .originalFilename(facadeFile.originalFilename())
                    .contentType(facadeFile.contentType())
                    .size(facadeFile.size())
                    .name(facadeFile.name())
                    .build();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read file content", e);
        }
    }
}