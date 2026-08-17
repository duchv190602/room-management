package com.vietsoftware.roommanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a blacklisted (invalidated) JWT access token.
 *
 * <p>When a user logs out, the current access token's JTI (JWT ID) and expiry are stored here so that
 * the token is rejected for the remainder of its natural validity period, even if the signature is valid.
 * This prevents token reuse after logout without requiring stateful session management.</p>
 *
 * <p>Records in this table are safe to purge after their {@code expiresAt} timestamp has passed,
 * since the token would be naturally expired and rejected anyway.</p>
 */
@Entity
@Table(name = "invalidated_tokens",
        indexes = @Index(name = "idx_invalidated_token_jti", columnList = "jti"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken extends BaseEntity {

    /**
     * JWT ID (jti) claim of the invalidated access token. Used as the lookup key during authorization.
     */
    @Column(name = "jti", nullable = false, unique = true, length = 36)
    UUID jti;

    /**
     * The user ID who owned this token. Stored for audit trail purposes.
     */
    @Column(name = "user_id", nullable = false)
    UUID userId;

    /**
     * Absolute expiry time of the original access token.
     * Records with {@code expiresAt} in the past can be safely deleted by a cleanup job.
     */
    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;
}
