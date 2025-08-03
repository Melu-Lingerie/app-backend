package ru.mellingerie.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_subscriptions", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_subscription", columnNames = {"user_id", "subscription_type"})
})
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_subscriptions_id_seq")
    @SequenceGenerator(name = "user_subscriptions_id_seq", sequenceName = "user_subscriptions_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private DeliveryFrequency frequency;

    @Column(name = "next_delivery_date")
    private LocalDate nextDeliveryDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 