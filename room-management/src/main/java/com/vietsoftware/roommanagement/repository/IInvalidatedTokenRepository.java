package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data JPA Repository for performing persistence operations on {@link InvalidatedToken} (access token blacklist).
 */
@Repository
public interface IInvalidatedTokenRepository extends JpaRepository<InvalidatedToken, UUID> {

    /**
     * Checks if a given JWT token ID (jti) has been blacklisted.
     *
     * @param jti UUID jti claim of the access token to check
     * @return {@code true} if the token has been blacklisted (revoked), {@code false} otherwise
     */
    boolean existsByJti(UUID jti);

    /**
     * Bulk-deletes all expired invalidated token records to prevent unbounded table growth.
     * Safe to run as a scheduled cleanup job.
     *
     * @param now current timestamp to compare against {@code expiresAt}
     * @return number of expired records deleted
     */
    @Modifying
    @Query("DELETE FROM InvalidatedToken t WHERE t.expiresAt < :now")
    int deleteAllExpired(Instant now);
}
