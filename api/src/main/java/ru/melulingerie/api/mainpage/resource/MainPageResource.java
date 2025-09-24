package ru.melulingerie.api.mainpage.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.melulingerie.facade.mainpage.dto.BannerMainPageFacadeDto;

import java.util.List;

@RequestMapping("/api/v1/main-page")
public interface MainPageResource {

    @GetMapping("/banner")
    ResponseEntity<List<BannerMainPageFacadeDto>>toBannerMainPageFacadeDto();
}
