package com.vietsoftware.roommanagement.dto.request;

import com.vietsoftware.roommanagement.constant.ApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object (DTO) payload for new user registration requests.
 */
@Schema(description = "User registration request payload")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    /**
     * Unique login username.
     */
    @NotBlank(message = "Username is required")
    @Size(min = ApiConstants.USERNAME_MIN_LENGTH, max = ApiConstants.USERNAME_MAX_LENGTH,
            message = "Username must be between {min} and {max} characters")
    @Schema(description = "Unique login username", example = "john_doe")
    String username;

    /**
     * Unique email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address format")
    @Size(max = ApiConstants.EMAIL_MAX_LENGTH, message = "Email must not exceed {max} characters")
    @Schema(description = "Email address", example = "john@example.com")
    String email;

    /**
     * Password (plaintext, will be hashed before storage).
     */
    @NotBlank(message = "Password is required")
    @Size(min = ApiConstants.PASSWORD_MIN_LENGTH, message = "Password must be at least {min} characters long")
    @Schema(description = "Account password (min 8 chars, at least 1 uppercase letter and 1 digit)", example = "SecurePass1")
    String password;

    /**
     * Full display name of the user.
     */
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed {max} characters")
    @Schema(description = "User display full name", example = "John Doe")
    String fullName;
}
