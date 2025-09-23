package ru.melulingerie.facade.mainpage.dto;

public record BannerMainPageFacadeDto(
        Long id,
        String title,
        String url,
        Long mediaId,
        Long order
) {
}
