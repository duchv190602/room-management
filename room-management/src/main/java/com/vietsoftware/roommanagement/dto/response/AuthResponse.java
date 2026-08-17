package com.vietsoftware.roommanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object (DTO) payload returned upon successful authentication.
 *
 * <p>Contains both the short-lived access token and the long-lived refresh token.
 * Clients should store the refresh token securely (e.g. HttpOnly cookie) and use it
 * to obtain a new access token when the current one expires.</p>
 */
@Schema(description = "Authentication response payload containing JWT access and refresh tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {

    /**
     * Short-lived signed JWT Access Token string. Use in the {@code Authorization: Bearer <token>} header.
     */
    @Schema(description = "Signed JWT access token", example = "eyJhbGciOiJIUzI1Ni...")
    String accessToken;

    /**
     * Long-lived raw refresh token string. Use to obtain a new access token via {@code POST /api/v1/auth/refresh}.
     */
    @Schema(description = "Long-lived refresh token", example = "a1b2c3d4...")
    String refreshToken;

    /**
     * Token authentication scheme type.
     */
    @Schema(description = "Token type scheme", example = "Bearer")
    @Builder.Default
    String tokenType = "Bearer";

    /**
     * Access token validity duration in seconds.
     */
    @Schema(description = "Access token expiry duration in seconds", example = "900")
    long expiresIn;
}
