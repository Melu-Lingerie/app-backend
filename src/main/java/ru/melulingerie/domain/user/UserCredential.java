package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.melulingerie.domain.IdentityType;

import java.time.LocalDateTime;

/**
 * Represents a user's credential, including identity type, identifier, password hash, and verification status.
 * Each record is uniquely associated with a user and an identity type.
 * Example usage:
 * <pre>
 *   UserCredential cred = new UserCredential(...);
 * </pre>
 */
@Getter
@Entity
@Table(name = "user_credentials", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"identifier"}, name = "uq_identifier"),
        @UniqueConstraint(columnNames = {"user_id", "identity_type"}, name = "uq_user_identity")
})
@NoArgsConstructor
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_credential_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", nullable = false)
    private IdentityType identityType;

    @Column(name = "identifier", length = 100, nullable = false, unique = true)
    private String identifier;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructs a new immutable UserCredential.
     *
     * @param id                Unique identifier
     * @param user              User reference
     * @param identityType      Identity type
     * @param identifier        Identifier (email or phone)
     * @param passwordHash      Password hash
     * @param isVerified        Verification flag
     * @param verifiedAt        Verification timestamp
     * @param passwordUpdatedAt Password update timestamp
     * @param lastFailedLoginAt Last failed login timestamp
     * @param failedLoginCount  Failed login count
     * @param createdAt         Creation timestamp
     */
    public UserCredential(Long id, User user, IdentityType identityType, String identifier, String passwordHash, boolean isVerified, LocalDateTime verifiedAt, LocalDateTime passwordUpdatedAt, LocalDateTime lastFailedLoginAt, int failedLoginCount, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.identityType = identityType;
        this.identifier = identifier;
        this.passwordHash = passwordHash;
        this.isVerified = isVerified;
        this.verifiedAt = verifiedAt;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.lastFailedLoginAt = lastFailedLoginAt;
        this.failedLoginCount = failedLoginCount;
        this.createdAt = createdAt;
    }
} 