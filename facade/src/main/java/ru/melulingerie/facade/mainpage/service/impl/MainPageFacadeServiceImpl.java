package ru.melulingerie.facade.mainpage.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.mainpage.dto.BannerMainPageFacadeDto;
import ru.melulingerie.facade.mainpage.mapper.MainPageMapper;
import ru.melulingerie.facade.mainpage.service.MainPageFacadeService;
import ru.melulingerie.mainpage.service.MainPageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageFacadeServiceImpl implements MainPageFacadeService {

    private final MainPageMapper mainPageMapper;
    private final MainPageService mainPageService;

    @Override
    public List<BannerMainPageFacadeDto> getBannerMainPageFacadeDto() {
        return mainPageService.getMainPageBanners()
                .stream()
                .map(mainPageMapper::toBannerMainPageFacadeDto)
                .toList();
    }
}
