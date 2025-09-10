package ru.melulingerie.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verifications", indexes = {
        @Index(name = "idx_email_verif_user", columnList = "user_id"),
        @Index(name = "idx_email_verif_email", columnList = "email"),
        @Index(name = "idx_email_verif_expires", columnList = "expires_at")
})
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_verif_seq")
    @SequenceGenerator(name = "email_verif_seq", sequenceName = "email_verif_id_seq", allocationSize = 10)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ru.melulingerie.users.entity.User user;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
