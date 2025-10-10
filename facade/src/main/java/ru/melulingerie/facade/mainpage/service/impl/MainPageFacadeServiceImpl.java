package ru.melulingerie.facade.mainpage.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.mainpage.dto.BannerMainPageFacadeDto;
import ru.melulingerie.facade.mainpage.mapper.MainPageMapper;
import ru.melulingerie.facade.mainpage.service.MainPageFacadeService;
import ru.melulingerie.facade.media.dto.MediaGetInfoFacadeResponseDto;
import ru.melulingerie.facade.media.service.MediaGetFacadeService;
import ru.melulingerie.mainpage.dto.BannerMainPageDto;
import ru.melulingerie.mainpage.service.MainPageService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainPageFacadeServiceImpl implements MainPageFacadeService {

    private final MainPageMapper mainPageMapper;
    private final MainPageService mainPageService;
    private final MediaGetFacadeService mediaGetFacadeService;

    @Override
    public List<BannerMainPageFacadeDto> getBannerMainPageFacadeDto() {

        List<BannerMainPageDto> mainPageBanners = mainPageService.getMainPageBanners();
        List<Long> mediaIds = mainPageBanners.stream().map(BannerMainPageDto::mediaId).toList();
        Map<Long, MediaGetInfoFacadeResponseDto> mediaByIds = mediaGetFacadeService.getMediaByIds(mediaIds);

        return mainPageBanners.stream()
                .map(b -> new BannerMainPageFacadeDto(
                        b.id(),
                        b.title(),
                        b.url(),
                        mediaByIds.get(b.mediaId()).s3Url(),
                        b.order()
                )).toList();
    }
}
