package ru.mellingerie.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_device_id_seq")
    @SequenceGenerator(name = "user_device_id_seq", sequenceName = "user_device_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "device_uuid", length = 255, unique = true)
    private String deviceUuid;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "os_version", length = 50)
    private String osVersion;

    @Column(name = "browser_name", length = 50)
    private String browserName;

    @Column(name = "browser_version", length = 50)
    private String browserVersion;

    @Column(name = "screen_width")
    private Integer screenWidth;

    @Column(name = "screen_height")
    private Integer screenHeight;

    @Column(name = "screen_density", precision = 4, scale = 2)
    private BigDecimal screenDensity;

    @Column(name = "push_token", columnDefinition = "TEXT")
    private String pushToken;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
} 