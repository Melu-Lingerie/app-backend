package ru.mellingerie.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_session")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_session_id_seq")
    @SequenceGenerator(name = "user_session_id_seq", sequenceName = "user_session_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_device_id")
    private UserDevice userDevice;

    @Column(name = "ip_address", columnDefinition = "INET")
    private InetAddress ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.lastActivityAt = now;
        this.expiresAt = now.plusHours(24);
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastActivityAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }
}