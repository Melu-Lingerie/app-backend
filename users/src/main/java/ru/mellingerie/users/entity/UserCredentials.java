package ru.mellingerie.users.entity;

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
@Table(name = "user_credentials", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_identity", columnNames = {"user_id", "identity_type"})
})
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_credentials_seq")
    @SequenceGenerator(name = "user_credentials_seq", sequenceName = "user_credentials_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", nullable = false)
    private IdentityType identityType;

    @Column(name = "identifier", length = 100, nullable = false, unique = true)
    private String identifier;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    @Column(name = "failed_login_count", nullable = false)
    @Builder.Default
    private Integer failedLoginCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
} 