package com.vietsoftware.roommanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token issued on last login", example = "eyJhbGci...")
    String refreshToken;
}
