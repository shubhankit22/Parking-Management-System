package com.demo.parkinglot.exception;

/**
 * Base exception for parking management operations
 * Provides structured error handling with error codes and context
 */
public class ParkingManagementException extends RuntimeException {
    
    private final String errorCode;
    private final String context;
    
    public ParkingManagementException(String message) {
        super(message);
        this.errorCode = "PARKING_ERROR";
        this.context = "GENERAL";
    }
    
    public ParkingManagementException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PARKING_ERROR";
        this.context = "GENERAL";
    }
    
    public ParkingManagementException(String errorCode, String context, String message) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public ParkingManagementException(String errorCode, String context, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getContext() {
        return context;
    }
    
    @Override
    public String toString() {
        return String.format("[%s:%s] %s", errorCode, context, getMessage());
    }
}
