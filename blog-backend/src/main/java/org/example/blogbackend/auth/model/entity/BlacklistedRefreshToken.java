package org.example.blogbackend.auth.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "blacklisted_refresh_tokens",
        indexes = {
                @Index(name = "idx_brt_jti", columnList = "jti", unique = true),
                @Index(name = "idx_brt_expiresAt", columnList = "expiresAt")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistedRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 36)
    private UUID jti;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}


