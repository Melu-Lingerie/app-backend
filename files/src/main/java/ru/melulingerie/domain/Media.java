package ru.melulingerie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media")
@Getter
@Setter
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_id_seq")
    @SequenceGenerator(name = "media_id_seq", sequenceName = "media_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false, length = 64)
    private String fileHash;

    @Column(nullable = false)
    private String s3Bucket;

    @Column(nullable = false, length = 500)
    private String s3Key;

    @Column(nullable = false, length = 1000)
    private String s3Url;

    @Column(columnDefinition = "int default 0")
    private int sortOrder = 0;

    @Column(columnDefinition = "boolean default false")
    private boolean isPrimary = false;

    @Column(columnDefinition = "boolean default true")
    private boolean isActive = true;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String uploadedBy;

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL)
    private Image image;

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL)
    private Video video;
}