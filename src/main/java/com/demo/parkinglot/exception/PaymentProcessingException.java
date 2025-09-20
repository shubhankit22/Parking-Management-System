package com.demo.parkinglot.exception;

/**
 * Exception for payment processing failures
 */
public class PaymentProcessingException extends ParkingManagementException {
    
    public PaymentProcessingException(String message) {
        super("PAYMENT_PROCESSING", "PAYMENT", message);
    }
    
    public PaymentProcessingException(String message, Throwable cause) {
        super("PAYMENT_PROCESSING", "PAYMENT", message, cause);
    }
    
    public PaymentProcessingException(String context, String message) {
        super("PAYMENT_PROCESSING", context, message);
    }
    
    public PaymentProcessingException(String context, String message, Throwable cause) {
        super("PAYMENT_PROCESSING", context, message, cause);
    }
}
