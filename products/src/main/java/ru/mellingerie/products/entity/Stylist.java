package ru.mellingerie.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "stylists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stylist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
    
    @Column(name = "instagram_handle", length = 50)
    private String instagramHandle;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
} 