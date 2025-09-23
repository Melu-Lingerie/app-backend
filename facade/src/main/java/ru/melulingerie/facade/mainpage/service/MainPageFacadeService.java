package ru.melulingerie.facade.mainpage.service;

import ru.melulingerie.facade.mainpage.dto.BannerMainPageFacadeDto;
import ru.melulingerie.mainpage.domain.BannerMainPage;

import java.util.List;

public interface MainPageFacadeService {

    List<BannerMainPageFacadeDto> getBannerMainPageFacadeDto();

}
