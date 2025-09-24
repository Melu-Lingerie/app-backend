package ru.melulingerie.mainpage.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.mainpage.dto.BannerMainPageDto;
import ru.melulingerie.mainpage.repository.BannerMainPageRepository;
import ru.melulingerie.mainpage.service.MainPageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageServiceImpl implements MainPageService {

    private final BannerMainPageRepository bannerMainPageRepository;

    @Override
    public List<BannerMainPageDto> getMainPageBanners() {
        return bannerMainPageRepository.findAll()
                .stream()
                .map(BannerMainPageDto::new)
                .toList();
    }
}
