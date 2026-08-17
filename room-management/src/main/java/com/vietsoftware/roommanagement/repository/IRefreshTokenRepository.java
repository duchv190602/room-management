package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for performing persistence operations on {@link RefreshToken} entities.
 */
@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Finds a refresh token by its raw token string.
     *
     * @param token raw refresh token string
     * @return {@link Optional} containing the matching refresh token, or empty if not found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens belonging to a specific user.
     * Used during global logout (logout from all devices).
     *
     * @param userId UUID of the user whose refresh tokens should be deleted
     * @return number of tokens deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    int deleteAllByUserId(@Param("userId") UUID userId);

    /**
     * Bulk-deletes all expired refresh token records to prevent unbounded table growth.
     * Safe to run as a scheduled cleanup job.
     *
     * @param now current timestamp to compare against {@code expiresAt}
     * @return number of expired records deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteAllExpired(@Param("now") Instant now);
}
