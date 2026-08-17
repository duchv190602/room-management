package com.vietsoftware.roommanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object (DTO) payload for user authentication (login) requests.
 */
@Schema(description = "User authentication request payload")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    /**
     * User login username.
     */
    @NotBlank(message = "Username is required")
    @Schema(description = "User login username", example = "john_doe")
    String username;

    /**
     * Account password.
     */
    @NotBlank(message = "Password is required")
    @Schema(description = "Account password", example = "SecurePass1")
    String password;
}
