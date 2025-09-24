package ru.melulingerie.mainpage.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BannerMainPage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "main_page_id_seq")
    @SequenceGenerator(name = "main_page_id_seq", sequenceName = "main_page_id_seq")
    private Long id;


    @Column(length = 100)
    private String title;

    /**
     * URL на который должен произойти редирект при нажатии на ссылку на банере
     */
    private String url;

    @Column(name = "media_id")
    private Long mediaId;

    @Column(name = "order_banner")
    private Long order;
}
