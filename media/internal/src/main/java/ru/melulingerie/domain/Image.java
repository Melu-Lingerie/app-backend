package ru.melulingerie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_iq_seq")
    @SequenceGenerator(name = "image_iq_seq", sequenceName = "image_iq_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    private int width;

    private int height;
}