package com.vietsoftware.roommanagement.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties class binding JWT token settings from {@code application.yaml}.
 *
 * <p>All values are read from the {@code jwt.*} properties namespace. Secret is loaded from
 * an environment variable ({@code JWT_SECRET}) with a fallback default for development.</p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HMAC-SHA signing secret key (Base64-encoded).
     * Must be overridden in production via the {@code JWT_SECRET} environment variable.
     */
    private String secret;

    /**
     * Access token validity duration in seconds (e.g. 900 = 15 minutes).
     */
    private long accessTokenExpiry;

    /**
     * Refresh token validity duration in seconds (e.g. 604800 = 7 days).
     */
    private long refreshTokenExpiry;
}
