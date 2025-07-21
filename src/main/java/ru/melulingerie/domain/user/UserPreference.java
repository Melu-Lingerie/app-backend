package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import ru.melulingerie.domain.Style;

import java.time.LocalDateTime;

/**
 * Represents user-specific preferences, including size, style, colors, and newsletter subscription.
 * Each record is uniquely associated with a user and may reference a preferred style.
 * Example usage:
 * <pre>
 *   UserPreference pref = new UserPreference(...);
 * </pre>
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_user_preference_user"), referencedColumnName = "id")
    private User user;

    @Column(name = "size_bust", length = 10)
    private String sizeBust;

    @Column(name = "size_bottom", length = 10)
    private String sizeBottom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_style_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_preference_style"))
    private Style preferredStyle;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "favorite_colors", columnDefinition = "jsonb", nullable = false)
    private String favoriteColors;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences_extended", columnDefinition = "jsonb", nullable = false)
    private String preferencesExtended;

    @Column(name = "newsletter_subscribed", nullable = false)
    private boolean newsletterSubscribed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructs a new immutable UserPreference.
     *
     * @param id                   Unique identifier
     * @param user                 User reference
     * @param sizeBust             Bust size
     * @param sizeBottom           Bottom size
     * @param preferredStyle       Preferred style
     * @param favoriteColors       Favorite colors as JSON array string
     * @param preferencesExtended  Extended preferences as JSON object string
     * @param newsletterSubscribed Newsletter subscription flag
     * @param createdAt            Creation timestamp
     * @param updatedAt            Update timestamp
     */
    public UserPreference(Long id, User user, String sizeBust, String sizeBottom, Style preferredStyle, String favoriteColors, String preferencesExtended, boolean newsletterSubscribed, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.sizeBust = sizeBust;
        this.sizeBottom = sizeBottom;
        this.preferredStyle = preferredStyle;
        this.favoriteColors = favoriteColors;
        this.preferencesExtended = preferencesExtended;
        this.newsletterSubscribed = newsletterSubscribed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
