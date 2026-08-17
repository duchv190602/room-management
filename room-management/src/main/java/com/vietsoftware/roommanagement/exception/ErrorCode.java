package com.vietsoftware.roommanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enumeration defining standardized business error codes, HTTP status mappings, and default detail messages.
 */
@Getter
public enum ErrorCode {

    // ─── GENERAL SYSTEM ERRORS ───────────────────────────────────────────
    /**
     * Unexpected internal server error.
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred"),

    /**
     * Invalid input payload or bad request syntax.
     */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request payload or parameters"),

    /**
     * Requested URL path or API endpoint was not found.
     */
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested endpoint or resource was not found"),

    // ─── AUTHENTICATION & SECURITY ERRORS ───────────────────────────────
    /**
     * Request requires authentication token.
     */
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource"),

    /**
     * User lacks required role permissions.
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this resource"),

    /**
     * Invalid login username or password.
     */
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password"),

    /**
     * Invalid or malformed JWT token.
     */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid or malformed"),

    /**
     * Expired JWT access token.
     */
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access token has expired"),

    // ─── USER MANAGEMENT ERRORS ──────────────────────────────────────────
    /**
     * Requested user account was not found.
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User account not found"),

    /**
     * User account is deactivated.
     */
    USER_INACTIVE(HttpStatus.FORBIDDEN, "User account is deactivated"),

    /**
     * Username is already taken during registration.
     */
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username is already taken"),

    /**
     * Email address is already registered.
     */
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email address is already registered"),

    // ─── ROOM MANAGEMENT ERRORS ──────────────────────────────────────────
    /**
     * Requested room resource was not found.
     */
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Room not found"),

    /**
     * Room code already exists in the system.
     */
    ROOM_CODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Room code already exists");

    /**
     * Corresponding HTTP status code.
     */
    private final HttpStatus httpStatus;

    /**
     * Detailed error message description.
     */
    private final String detail;

    /**
     * Constructor for ErrorCode enum.
     *
     * @param httpStatus HTTP status code
     * @param detail     detailed error message
     */
    ErrorCode(HttpStatus httpStatus, String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}
