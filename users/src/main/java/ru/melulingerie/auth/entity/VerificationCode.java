package ru.melulingerie.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verifications_codes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verif_seq")
    @SequenceGenerator(name = "verif_seq", sequenceName = "verif_id_seq", allocationSize = 100)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_credentials_id", nullable = false)
    private UserCredentials userCredentials;

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

    public User getUser() {
        return userCredentials.getUser();
    }

    @Transient
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Проверяет, может ли код быть использован
     */
    @Transient
    public boolean isActive() {
        return status == VerificationStatus.ACTIVE && !isExpired();
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
