package com.vietsoftware.roommanagement.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties class binding initial admin user credentials from {@code application.yaml}.
 *
 * <p>Supports environment variable overrides for production deployment:
 * <ul>
 *   <li>{@code ADMIN_USERNAME}</li>
 *   <li>{@code ADMIN_PASSWORD}</li>
 *   <li>{@code ADMIN_EMAIL}</li>
 *   <li>{@code ADMIN_FULL_NAME}</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.init-admin")
public class AdminInitProperties {

    /**
     * Initial admin login username.
     */
    private String username;

    /**
     * Initial admin raw password (will be BCrypt hashed on startup).
     */
    private String password;

    /**
     * Initial admin email address.
     */
    private String email;

    /**
     * Initial admin full display name.
     */
    private String fullName;
}
