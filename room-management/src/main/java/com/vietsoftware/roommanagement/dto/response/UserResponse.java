package com.vietsoftware.roommanagement.dto.response;

import com.vietsoftware.roommanagement.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) payload representing user details (excluding sensitive password info).
 */
@Schema(description = "User profile response payload")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    /**
     * Unique user identifier UUID.
     */
    @Schema(description = "User UUID identifier")
    UUID id;

    /**
     * Unique login username.
     */
    @Schema(description = "Login username", example = "john_doe")
    String username;

    /**
     * User email address.
     */
    @Schema(description = "User email address", example = "john@example.com")
    String email;

    /**
     * User full display name.
     */
    @Schema(description = "User full name", example = "John Doe")
    String fullName;

    /**
     * Account status.
     */
    @Schema(description = "Account operational status")
    UserStatus status;

    /**
     * Account creation timestamp.
     */
    @Schema(description = "Account creation timestamp", example = "2026-08-05T10:00:00Z")
    Instant createdAt;
}
