package com.vietsoftware.roommanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Entity representing a persistent refresh token issued to a user upon successful authentication.
 *
 * <p>Each user may hold multiple refresh tokens (e.g. multiple devices). A token can be revoked individually
 * (logout from a specific device) or in bulk (logout from all devices).
 * Extends {@link BaseEntity} for UUID primary key and auditing timestamps.</p>
 *
 * <p>Token lifecycle:
 * <ul>
 *   <li>Created on login</li>
 *   <li>Deleted on logout (single device)</li>
 *   <li>All tokens for a user deleted on global logout</li>
 *   <li>Expired tokens are treated as invalid</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "refresh_tokens",
        indexes = @Index(name = "idx_refresh_token_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken extends BaseEntity {

    /**
     * The user that owns this refresh token.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    /**
     * Raw refresh token string.
     */
    @Column(name = "token", nullable = false, unique = true, length = 512)
    String token;

    /**
     * Absolute expiry timestamp of this refresh token.
     */
    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;


    /**
     * Returns true if this refresh token is past its expiry time.
     *
     * @return {@code true} if token is expired, {@code false} otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
