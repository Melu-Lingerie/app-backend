package ru.melulingerie.api.mainpage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.mainpage.resource.MainPageResource;
import ru.melulingerie.facade.mainpage.dto.BannerMainPageFacadeDto;
import ru.melulingerie.facade.mainpage.service.MainPageFacadeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainPageController implements MainPageResource {

    private final MainPageFacadeService mainPageFacadeService;

    @Override
    public ResponseEntity<List<BannerMainPageFacadeDto>> toBannerMainPageFacadeDto() {
        return ResponseEntity.ok(mainPageFacadeService.getBannerMainPageFacadeDto());
    }
}
