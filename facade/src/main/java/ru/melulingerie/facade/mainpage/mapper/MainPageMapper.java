package ru.melulingerie.facade.mainpage.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.mainpage.dto.BannerMainPageFacadeDto;
import ru.melulingerie.mainpage.dto.BannerMainPageDto;

@Mapper(config = MapStructConfig.class)
public interface MainPageMapper {

    BannerMainPageFacadeDto toBannerMainPageFacadeDto(BannerMainPageDto bannerMainPage);
}
