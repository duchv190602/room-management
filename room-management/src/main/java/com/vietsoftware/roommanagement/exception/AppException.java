package com.vietsoftware.roommanagement.exception;

import lombok.Getter;

/**
 * Custom runtime exception used across the application to encapsulate specific business error codes.
 */
@Getter
public class AppException extends RuntimeException {

    /**
     * Encapsulated business error code details.
     */
    private final ErrorCode errorCode;

    /**
     * Constructs a new AppException with the specified error code.
     *
     * @param errorCode the {@link ErrorCode} detailing the error cause
     */
    public AppException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }
}

