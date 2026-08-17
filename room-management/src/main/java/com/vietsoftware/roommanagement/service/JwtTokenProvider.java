package com.vietsoftware.roommanagement.service;

import com.vietsoftware.roommanagement.configuration.JwtProperties;
import com.vietsoftware.roommanagement.constant.SecurityConstants;
import com.vietsoftware.roommanagement.dto.JwtPayload;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Utility component responsible for generating and parsing JWT access tokens.
 *
 * <p>Each access token stores:
 * <ul>
 *   <li>{@code sub} — user UUID</li>
 *   <li>{@code jti} — unique token ID used for blacklisting after logout</li>
 *   <li>{@code username} — login name</li>
 *   <li>{@code roles} — list of assigned role names</li>
 * </ul>
 * Permissions are NOT stored in the token and are resolved dynamically at runtime.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Constructs a HMAC-SHA signing key from the configured Base64-encoded secret.
     *
     * @return {@link SecretKey} for signing and verification
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a signed JWT access token.
     * A new random UUID is assigned as the {@code jti} claim on every issuance.
     *
     * @param userId   user UUID
     * @param username user login name
     * @param roles    set of assigned role name strings
     * @return compact signed JWT string
     */
    public String generateAccessToken(UUID userId, String username, Set<String> roles) {
        UUID jti = UUID.randomUUID();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpiry() * 1000L);

        return Jwts.builder()
                .id(jti.toString())
                .subject(userId.toString())
                .claim(SecurityConstants.CLAIM_USERNAME, username)
                .claim(SecurityConstants.CLAIM_ROLES, roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Parses the JWT token and extracts all claims into a {@link JwtPayload} in a single cryptographic operation.
     *
     * <p>This is the preferred method when multiple claims are needed from the same token.
     * It avoids repeated signature verification by parsing the token exactly once.</p>
     *
     * @param token valid JWT string
     * @return {@link JwtPayload} containing all extracted claims
     * @throws JwtException         if signature verification fails, token is malformed, or token is expired
     * @throws IllegalArgumentException if token is null or empty
     */
    @SuppressWarnings("unchecked")
    public JwtPayload extractAllClaims(String token) {
        Claims claims = parseClaims(token);
        return new JwtPayload(
                UUID.fromString(claims.getId()),
                UUID.fromString(claims.getSubject()),
                claims.get(SecurityConstants.CLAIM_USERNAME, String.class),
                (List<String>) claims.get(SecurityConstants.CLAIM_ROLES),
                claims.getExpiration()
        );
    }

    /**
     * Extracts the raw JWT token string from the {@code Authorization: Bearer <token>} header.
     *
     * @param request HTTP servlet request
     * @return raw JWT string, or {@code null} if header is absent or not in Bearer format
     */
    public static String extractBearerToken(HttpServletRequest request) {
        return extractBearerToken(request.getHeader(SecurityConstants.AUTH_HEADER));
    }

    /**
     * Extracts the raw JWT token string from a raw Authorization header value.
     *
     * @param authorizationHeader raw value of the {@code Authorization} header
     * @return raw JWT string, or {@code null} if header is absent or not in Bearer format
     */
    public static String extractBearerToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader)) {
            String trimmedHeader = authorizationHeader.trim();
            if (trimmedHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
                return trimmedHeader.substring(SecurityConstants.BEARER_PREFIX.length()).trim();
            }
            if (trimmedHeader.startsWith("Bearer")) {
                return trimmedHeader.substring(6).trim();
            }
            return trimmedHeader;
        }
        return null;
    }

    /**
     * Parses and returns the claims payload body from a JWT token string.
     * Verifies signature and expiry as part of parsing.
     *
     * @param token JWT token string
     * @return parsed {@link Claims} instance
     * @throws JwtException if token parsing, signature verification, or expiry check fails
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
