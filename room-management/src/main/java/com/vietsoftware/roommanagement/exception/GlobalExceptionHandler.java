package com.vietsoftware.roommanagement.exception;

import com.vietsoftware.roommanagement.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global REST controller advice intercepting exceptions thrown across controllers and returning a standardized {@link ErrorResponse}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom business exception {@link AppException}.
     *
     * @param ex custom application exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse} with mapped HTTP status
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        log.warn(
                "Application exception occurred. code={}, message={}",
                ex.getErrorCode().name(),
                ex.getMessage()
        );
        return buildErrorResponse(ex.getErrorCode(), null, null);
    }

    /**
     * Handles payload validation failures triggered by {@code @Valid} on request bodies.
     *
     * @param ex method argument validation exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse} with field validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        log.warn(
                "Request validation failed. errors={}",
                fieldErrors
        );

        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                "Request payload validation failed",
                fieldErrors
        );
    }

    /**
     * Handles constraint violation failures at entity persistence level.
     *
     * @param ex constraint violation exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, List<String>> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        log.warn(
                "Constraint validation failed. errors={}",
                errors
        );
        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                "Entity constraint validation failed",
                errors
        );
    }

    /**
     * Handles transaction system rollback exceptions wrapping constraint violations.
     *
     * @param ex transaction system exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse}
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystem(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException constraintEx) {
            log.warn("Transaction rolled back due to constraint violation.");
            return handleConstraintViolation(constraintEx);
        }

        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                "Transaction failed due to validation constraint violation",
                null
        );
    }

    /**
     * Handles database SQL integrity constraint violations (e.g. unique index collision).
     *
     * @param ex data integrity violation exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse}
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                "Database constraint violation occurred",
                null
        );
    }

    /**
     * Handles malformed or unparseable HTTP request body.
     *
     * @param ex HTTP message not readable exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn(
                "HTTP request body could not be read. message={}",
                ex.getMostSpecificCause().getMessage()
        );
        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                "Request body is missing or malformed",
                null
        );
    }

    /**
     * Handles type mismatch for request parameters or path variables.
     *
     * @param ex method argument type mismatch exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse}
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                "Invalid data type for parameter: " + ex.getName(),
                null
        );
    }

    /**
     * Handles non-existent URL routes and static resource requests (HTTP 404).
     *
     * @param ex no resource found exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse} with HTTP 404 status
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return buildErrorResponse(
                ErrorCode.RESOURCE_NOT_FOUND,
                "Requested endpoint [" + ex.getResourcePath() + "] was not found",
                null
        );
    }

    /**
     * Fallback handler for all uncaught unexpected system exceptions.
     *
     * @param ex generic exception
     * @return {@link ResponseEntity} wrapping {@link ErrorResponse} with HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return buildErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                null,
                null
        );
    }

    /**
     * Helper builder method constructing standardized {@link ErrorResponse} wrapped in {@link ResponseEntity}.
     *
     * @param errorCode     business error code enum
     * @param customMessage optional override detail message
     * @param errors        optional field error map
     * @return {@link ResponseEntity} containing {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            ErrorCode errorCode,
            String customMessage,
            Map<String, List<String>> errors) {

        ErrorResponse body = ErrorResponse.builder()
                .code(errorCode.name())
                .message(customMessage != null ? customMessage : errorCode.getDetail())
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }
}
