package ru.mellingerie.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_preferences_id_seq")
    @SequenceGenerator(name = "user_preferences_id_seq", sequenceName = "user_preferences_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "size_bust", length = 10)
    private String sizeBust;

    @Column(name = "size_bottom", length = 10)
    private String sizeBottom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_style_id")
    private Style preferredStyle;

    @Column(name = "favorite_colors", columnDefinition = "jsonb")
    private String favoriteColors;

    @Column(name = "preferences_extended", columnDefinition = "jsonb")
    private String preferencesExtended;

    @Builder.Default
    @Column(name = "newsletter_subscribed", nullable = false)
    private Boolean newsletterSubscribed = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 