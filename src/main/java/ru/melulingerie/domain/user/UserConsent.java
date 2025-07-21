package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import ru.melulingerie.domain.ConsentType;

/**
 * Represents a user's consent, including type, given and revoked timestamps.
 * Each record is uniquely associated with a user and a consent type.
 * Example usage:
 * <pre>
 *   UserConsent consent = new UserConsent(...);
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_consents", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "consent_type"}, name = "uq_user_consent")
})
public class UserConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_consent_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false)
    private ConsentType consentType;

    @CreationTimestamp
    @Column(name = "given_at", nullable = false, updatable = false)
    private LocalDateTime givenAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Constructs a new UserConsent.
     * @param id Unique identifier
     * @param user User reference
     * @param consentType Consent type
     * @param givenAt Timestamp when consent was given
     * @param revokedAt Timestamp when consent was revoked
     */
    public UserConsent(Long id, User user, ConsentType consentType, LocalDateTime givenAt, LocalDateTime revokedAt) {
        this.id = id;
        this.user = user;
        this.consentType = consentType;
        this.givenAt = givenAt;
        this.revokedAt = revokedAt;
    }
} 