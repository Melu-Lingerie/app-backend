package ru.melulingerie.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "styles")
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "styles_id_seq")
    @SequenceGenerator(name = "styles_id_seq", sequenceName = "styles_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
} 