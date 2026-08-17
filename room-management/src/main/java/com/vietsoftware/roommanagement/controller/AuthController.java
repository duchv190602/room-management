package com.vietsoftware.roommanagement.controller;

import com.vietsoftware.roommanagement.constant.ApiConstants;
import com.vietsoftware.roommanagement.constant.SecurityConstants;
import com.vietsoftware.roommanagement.dto.request.LoginRequest;
import com.vietsoftware.roommanagement.dto.request.RefreshTokenRequest;
import com.vietsoftware.roommanagement.dto.request.RegisterRequest;
import com.vietsoftware.roommanagement.dto.response.AuthResponse;
import com.vietsoftware.roommanagement.dto.response.UserResponse;
import com.vietsoftware.roommanagement.service.IAuthService;
import com.vietsoftware.roommanagement.service.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication resources providing endpoints for registration, login, token refresh, and logout.
 */
@Tag(name = "Authentication Management", description = "Public endpoints for user registration and JWT authentication")
@RestController
@RequestMapping(ApiConstants.AUTH_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /**
     * Registers a new user account with default USER role group.
     *
     * @param userRegisterRequest user registration payload
     * @return {@link ResponseEntity} wrapping {@link UserResponse} and HTTP 201 status
     */
    @Operation(summary = "Register a new user account",
            description = "Creates a new user account with default USER role group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation failure on input payload"),
            @ApiResponse(responseCode = "409", description = "Username or email already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest userRegisterRequest) {
        UserResponse response = authService.register(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates user credentials and issues both a JWT access token and a refresh token.
     *
     * @param userLoginRequest user credentials payload
     * @return {@link ResponseEntity} wrapping {@link AuthResponse} and HTTP 200 status
     */
    @Operation(summary = "Login user credentials",
            description = "Authenticates credentials and returns both a JWT access token and a refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "403", description = "Account is deactivated")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest userLoginRequest) {
        AuthResponse response = authService.login(userLoginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Issues a new access token using a valid, non-expired refresh token.
     *
     * @param refreshTokenRequest payload containing the raw refresh token
     * @return {@link ResponseEntity} wrapping {@link AuthResponse} with new access token and HTTP 200 status
     */
    @Operation(summary = "Refresh access token",
            description = "Issues a new access token using a valid non-expired refresh token. The refresh token is rotated on each use.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token issued successfully"),
            @ApiResponse(responseCode = "401", description = "Refresh token not found or expired")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out the current user by blacklisting the access token and deleting the refresh token.
     *
     * @param request the full {@code Authorization: Bearer <accessToken>} header value
     * @param refreshTokenRequest payload containing the raw refresh token to revoke
     * @return HTTP 200 with no body on success
     */
    @Operation(summary = "Logout user",
            description = "Invalidates the current access token (blacklists jti) and revokes the provided refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        String accessToken = JwtTokenProvider.extractBearerToken(request);
        authService.logout(accessToken, refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok().build();
    }
}
