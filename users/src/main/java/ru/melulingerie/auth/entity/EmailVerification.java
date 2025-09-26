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
    // TODO подумать насчет привязать верификацию к UserCredentionals и заменить EmailVerification что бы он был универсален для телефона и емэил
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ru.melulingerie.users.entity.User user;
    //TODO можно удалить и определять по связи с UserCredentionals
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

    // Статус верификации
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false)
    private VerificationStatus status = VerificationStatus.ACTIVE;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Проверяет, может ли код быть использован
     */
    public boolean isActive() {
        return status == VerificationStatus.ACTIVE && !isExpired();
    }
    
    /**
     * Проверяет, может ли старый код еще работать (в течение окна толерантности)
     */
    public boolean isValidWithTolerance(int toleranceMinutes) {
        if (status == VerificationStatus.ACTIVE) {
            return !isExpired();
        }
        if (status == VerificationStatus.SUPERSEDED) {
            // Старые коды работают еще toleranceMinutes минут
            LocalDateTime toleranceExpiry = createdAt.plusMinutes(toleranceMinutes);
            return LocalDateTime.now().isBefore(toleranceExpiry) && !isExpired();
        }
        return false;
    }
    
    /**
     * Статусы верификации (упрощенная версия)
     */
    public enum VerificationStatus {
        ACTIVE,     // Текущий активный код
        SUPERSEDED, // Заменен новым кодом (работает с tolerance)  
        USED        // Успешно использован
        // EXPIRED убран - используем isExpired()
    }
}
