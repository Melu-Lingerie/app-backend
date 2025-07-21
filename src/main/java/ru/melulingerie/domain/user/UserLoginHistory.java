package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a user's login attempt, including IP, user agent, and success flag.
 * Each record is uniquely associated with a user and a login event.
 * Example usage:
 * <pre>
 *   UserLoginHistory login = new UserLoginHistory(...);
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_login_history")
public class UserLoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_login_history_user"))
    private User user;

    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT", nullable = false)
    private String userAgent;

    @Column(name = "succeeded", nullable = false)
    private boolean succeeded;

    @CreationTimestamp
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    /**
     * Constructs a new UserLoginHistory.
     *
     * @param id         Unique identifier
     * @param user       User reference
     * @param ip         IP address
     * @param userAgent  User agent string
     * @param succeeded  Success flag
     * @param occurredAt Timestamp of login attempt
     */
    public UserLoginHistory(Long id, User user, String ip, String userAgent, boolean succeeded, LocalDateTime occurredAt) {
        this.id = id;
        this.user = user;
        this.ip = ip;
        this.userAgent = userAgent;
        this.succeeded = succeeded;
        this.occurredAt = occurredAt;
    }
} 