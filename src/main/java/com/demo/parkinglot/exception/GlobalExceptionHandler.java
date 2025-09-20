package com.demo.parkinglot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for consistent error responses
 * Provides structured error handling across the application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle parking management specific exceptions
     */
    @ExceptionHandler(ParkingManagementException.class)
    public ResponseEntity<Map<String, Object>> handleParkingManagementException(ParkingManagementException ex) {
        logger.error("Parking Management Exception: [{}:{}] {}", 
                    ex.getErrorCode(), ex.getContext(), ex.getMessage(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            ex.getContext()
        );

        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle slot allocation exceptions
     */
    @ExceptionHandler(SlotAllocationException.class)
    public ResponseEntity<Map<String, Object>> handleSlotAllocationException(SlotAllocationException ex) {
        logger.error("Slot Allocation Exception: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
            "SLOT_ALLOCATION_FAILED",
            ex.getMessage(),
            "ALLOCATION"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle payment processing exceptions
     */
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentProcessingException(PaymentProcessingException ex) {
        logger.error("Payment Processing Exception: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
            "PAYMENT_PROCESSING_FAILED",
            ex.getMessage(),
            "PAYMENT"
        );

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse);
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation Exception: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = createErrorResponse(
            "VALIDATION_FAILED",
            "Request validation failed",
            "VALIDATION"
        );
        errorResponse.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access Denied: {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "ACCESS_DENIED",
            "Insufficient privileges to perform this operation",
            "AUTHORIZATION"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal Argument Exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "INVALID_ARGUMENT",
            ex.getMessage(),
            "VALIDATION"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        logger.warn("Illegal State Exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "INVALID_STATE",
            ex.getMessage(),
            "STATE"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected Exception: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            "SYSTEM"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message, String context) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("context", context);
        errorResponse.put("timestamp", LocalDateTime.now());
        return errorResponse;
    }

    /**
     * Determine HTTP status based on error code
     */
    private HttpStatus determineHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "SLOT_ALLOCATION" -> HttpStatus.CONFLICT;
            case "PAYMENT_PROCESSING" -> HttpStatus.PAYMENT_REQUIRED;
            case "VALIDATION_FAILED", "INVALID_ARGUMENT" -> HttpStatus.BAD_REQUEST;
            case "ACCESS_DENIED" -> HttpStatus.FORBIDDEN;
            case "INVALID_STATE" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
