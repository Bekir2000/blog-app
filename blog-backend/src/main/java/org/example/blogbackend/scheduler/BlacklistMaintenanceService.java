package org.example.blogbackend.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.auth.repository.BlacklistedRefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlacklistMaintenanceService {

    private final BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    /** Runs every hour by default (override via property) */
    @Transactional
    @Scheduled(cron = "${blacklist.cleanup.cron:0 5 3 * * *}")
    public void purgeExpired() {
        blacklistedRefreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
