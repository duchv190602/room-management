package com.vietsoftware.roommanagement.service;

import com.vietsoftware.roommanagement.dto.request.LoginRequest;
import com.vietsoftware.roommanagement.dto.request.RefreshTokenRequest;
import com.vietsoftware.roommanagement.dto.request.RegisterRequest;
import com.vietsoftware.roommanagement.dto.response.AuthResponse;
import com.vietsoftware.roommanagement.dto.response.UserResponse;
import com.vietsoftware.roommanagement.exception.AppException;

/**
 * Service interface defining authentication and user registration business operations.
 */
public interface IAuthService {

    /**
     * Registers a new user account with default USER role group.
     *
     * @param userRegisterRequest user registration payload containing username, email, password, and fullName
     * @return {@link UserResponse} containing the created user details (excluding password)
     * @throws AppException with {@code USERNAME_ALREADY_EXISTS} if username is already taken
     * @throws AppException with {@code EMAIL_ALREADY_EXISTS} if email is already registered
     */
    UserResponse register(RegisterRequest userRegisterRequest);

    /**
     * Authenticates user credentials and issues both a JWT access token and a refresh token.
     *
     * @param userLoginRequest login payload containing username and password
     * @return {@link AuthResponse} containing the access token, refresh token, type, and expiry
     * @throws AppException with {@code INVALID_CREDENTIALS} if username or password is incorrect
     */
    AuthResponse login(LoginRequest userLoginRequest);

    /**
     * Issues a new access token using a valid, non-expired refresh token.
     *
     * @param refreshTokenRequest payload containing the raw refresh token string
     * @return {@link AuthResponse} containing the new access token and the same refresh token
     * @throws AppException with {@code INVALID_TOKEN} if the refresh token is not found or expired
     */
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * Invalidates the current access token (adds it to the blacklist) and deletes the provided refresh token.
     * The user will need to log in again to obtain new tokens.
     *
     * @param refreshToken raw refresh token string to delete
     * @throws AppException with {@code INVALID_TOKEN} if the refresh token is not found
     */
    void logout( String refreshToken);
}
