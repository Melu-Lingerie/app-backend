package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.melulingerie.domain.DeviceType;

import java.time.LocalDateTime;

/**
 * Represents a device associated with a user, including type, push token, and activity timestamps.
 * Each record is uniquely associated with a user and a device.
 * Example usage:
 * <pre>
 *   UserDevice device = new UserDevice(...);
 * </pre>
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "user_devices")
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_device_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "push_token", columnDefinition = "TEXT")
    private String pushToken;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructs a new immutable UserDevice.
     *
     * @param id         Unique identifier
     * @param user       User reference
     * @param deviceType Device type
     * @param pushToken  Push notification token
     * @param lastSeenAt Last seen timestamp
     * @param createdAt  Creation timestamp
     */
    public UserDevice(Long id, User user, DeviceType deviceType, String pushToken, LocalDateTime lastSeenAt, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.deviceType = deviceType;
        this.pushToken = pushToken;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = createdAt;
    }
}