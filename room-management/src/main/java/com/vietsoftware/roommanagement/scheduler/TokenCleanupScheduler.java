package com.vietsoftware.roommanagement.scheduler;

import com.vietsoftware.roommanagement.repository.IInvalidatedTokenRepository;
import com.vietsoftware.roommanagement.repository.IRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Scheduled cleanup component responsible for purging expired refresh tokens and blacklisted access tokens.
 *
 * <p>Runs automatically based on the cron expression configured in {@code application.yaml}
 * (default: every day at 00:00 midnight: {@code 0 0 0 * * ?}).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IInvalidatedTokenRepository invalidatedTokenRepository;

    /**
     * Periodically purges expired tokens from the database.
     * Uses bulk delete queries for optimal performance.
     */
    @Scheduled(cron = "${app.cron.token-cleanup-expression:0 0 0 * * ?}")
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        log.info("Starting scheduled token cleanup job at timestamp: {}", now);

        int deletedRefreshTokens = refreshTokenRepository.deleteAllExpired(now);
        int deletedInvalidatedTokens = invalidatedTokenRepository.deleteAllExpired(now);

        log.info("Token cleanup completed: purged {} expired refresh token(s) and {} blacklisted access token(s).",
                deletedRefreshTokens, deletedInvalidatedTokens);
    }
}
