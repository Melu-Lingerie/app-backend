package ru.melulingerie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "videos")
@Getter
@Setter
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_id_seq")
    @SequenceGenerator(name = "video_id_seq", sequenceName = "video_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    private int width;

    private int height;

    private int duration;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private VideoCategory videoCategory;
}