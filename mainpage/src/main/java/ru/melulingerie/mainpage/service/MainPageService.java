package ru.melulingerie.mainpage.service;

import ru.melulingerie.mainpage.dto.BannerMainPageDto;

import java.util.List;

public interface MainPageService {
    List<BannerMainPageDto> getMainPageBanners();
}
