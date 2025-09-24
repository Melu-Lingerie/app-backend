package ru.melulingerie.mainpage.dto;

import ru.melulingerie.mainpage.domain.BannerMainPage;

public record BannerMainPageDto(
        Long id,
        String title,
        String url,
        Long mediaId,
        Long order
) {
    public BannerMainPageDto(BannerMainPage entity) {
        this(
                entity.getId(),
                entity.getTitle(),
                entity.getUrl(),
                entity.getMediaId(),
                entity.getOrder()
        );
    }

}
