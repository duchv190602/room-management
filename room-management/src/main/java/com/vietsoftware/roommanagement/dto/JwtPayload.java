package com.vietsoftware.roommanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) encapsulating all JWT access token claims parsed in a single operation.
 *
 * <p>Used to avoid repeated cryptographic parsing of the same token within a single request lifecycle.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtPayload {

    /**
     * Unique token identifier (used for blacklisting).
     */
    UUID jti;

    /**
     * User UUID from the {@code sub} claim.
     */
    UUID userId;

    /**
     * Login username from the {@code username} claim.
     */
    String username;

    /**
     * List of assigned role names from the {@code roles} claim.
     */
    List<String> roles;

    /**
     * Token expiry timestamp from the {@code exp} claim.
     */
    Date expiresAt;
}
