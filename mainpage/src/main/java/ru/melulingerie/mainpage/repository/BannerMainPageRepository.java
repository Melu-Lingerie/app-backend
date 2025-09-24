package ru.melulingerie.mainpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.mainpage.domain.BannerMainPage;

@Repository
public interface BannerMainPageRepository extends JpaRepository<BannerMainPage, Long> {
}
