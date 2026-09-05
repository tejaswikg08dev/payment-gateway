package com.payflow.identity.exception;

import com.payflow.common.dto.ApiResponse;
import com.payflow.common.dto.ErrorResponse;
import com.payflow.common.dto.ValidationError;
import com.payflow.common.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler for identity-service.
 * Converts exceptions into structured API error responses.
 *
 * HOW IT WORKS:
 * 1. Controller method throws an exception (e.g., DuplicateResourceException)
 * 2. Spring intercepts it BEFORE returning to the client
 * 3. Finds the matching @ExceptionHandler method here
 * 4. Returns the structured error response instead
 *
 * WHY @RestControllerAdvice?
 * → Applies to ALL controllers in this service automatically
 * → No try-catch blocks needed in controllers
 * → Centralized error formatting
 */
@RestControllerAdvice
public class IdentityExceptionHandler {

    /**
     * DUPLICATE EMAIL (409 CONFLICT)
     *
     * Triggered when: User tries to register with an email that already exists
     * Source: AuthService.register() → DuplicateResourceException
     *
     * Example response:
     * HTTP 409
     * { "success": false, "error": { "code": "DUPLICATE_RESOURCE", "message": "User with email 'x@y.com' already exists" } }
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ErrorResponse.of(ex.getErrorCode(), ex.getMessage())));
    }

    /**
     * UNAUTHORIZED (401)
     *
     * Triggered when: Wrong password, invalid token, expired token, disabled account
     * Source: AuthService.login(), AuthService.refreshToken()
     *
     * SECURITY: Message is intentionally vague for login failures
     * to prevent user enumeration.
     *
     * Example response:
     * HTTP 401
     * { "success": false, "error": { "code": "UNAUTHORIZED", "message": "Invalid email or password" } }
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorResponse.of(ex.getErrorCode(), ex.getMessage())));
    }

    /**
     * NOT FOUND (404)
     *
     * Triggered when: User ID from header doesn't exist (profile endpoint)
     * Source: AuthService.getProfile()
     *
     * Example response:
     * HTTP 404
     * { "success": false, "error": { "code": "NOT_FOUND", "message": "User not found" } }
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorResponse.of(ex.getErrorCode(), ex.getMessage())));
    }

    /**
     * VALIDATION ERRORS (400 BAD REQUEST)
     *
     * Triggered when: @Valid fails on request DTO
     * Source: Spring MVC (automatic, before controller code runs)
     *
     * Example: RegisterRequest with email = "notanemail" and password = "short"
     *
     * Example response:
     * HTTP 400
     * {
     *   "success": false,
     *   "error": {
     *     "code": "VALIDATION_ERROR",
     *     "message": "Request validation failed",
     *     "details": [
     *       { "field": "email", "message": "Invalid email format", "rejectedValue": "notanemail" },
     *       { "field": "password", "message": "Password must be 8-100 characters", "rejectedValue": "short" }
     *     ]
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<ValidationError> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .toList();
        ErrorResponse error = ErrorResponse.withDetails(
                "VALIDATION_ERROR", "Request validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    /**
     * CATCH-ALL (500 INTERNAL SERVER ERROR)
     *
     * Triggered when: Any unexpected exception (NullPointerException, DB errors, etc.)
     *
     * SECURITY: NEVER expose internal details to clients!
     * Log the real error server-side, return generic message to client.
     *
     * Example response:
     * HTTP 500
     * { "success": false, "error": { "code": "INTERNAL_ERROR", "message": "An unexpected error occurred" } }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        // In production, also log the real exception:
        // log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorResponse.of("INTERNAL_ERROR",
                        "An unexpected error occurred")));
    }

    // ─── Helper ─────────────────────────────────────────────────────

    private ValidationError mapFieldError(FieldError fieldError) {
        return ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
}