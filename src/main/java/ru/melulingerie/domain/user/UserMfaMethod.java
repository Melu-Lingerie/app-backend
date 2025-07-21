package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.melulingerie.domain.MfaMethod;

import java.time.LocalDateTime;

/**
 * Represents a user's MFA method, including type, secret, and verification timestamp.
 * Each record is uniquely associated with a user and an MFA method type.
 * Example usage:
 * <pre>
 *   UserMfaMethod mfa = new UserMfaMethod(...);
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_mfa_methods", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "method"}, name = "uq_user_mfa")
})
public class UserMfaMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_mfa_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private MfaMethod method;

    @Column(name = "secret", columnDefinition = "TEXT", nullable = false)
    private String secret;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructs a new UserMfaMethod.
     *
     * @param id         Unique identifier
     * @param user       User reference
     * @param method     MFA method type
     * @param secret     Encrypted secret
     * @param verifiedAt Verification timestamp
     * @param createdAt  Creation timestamp
     */
    public UserMfaMethod(Long id, User user, MfaMethod method, String secret, LocalDateTime verifiedAt, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.method = method;
        this.secret = secret;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }
} 