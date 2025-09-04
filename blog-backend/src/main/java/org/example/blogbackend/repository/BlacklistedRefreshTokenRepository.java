package org.example.blogbackend.repository;

import org.example.blogbackend.model.entities.BlacklistedRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface BlacklistedRefreshTokenRepository extends JpaRepository<BlacklistedRefreshToken, UUID> {
    boolean existsByJti(UUID jti);
    void deleteByExpiresAtBefore(Instant cutoff);
}
