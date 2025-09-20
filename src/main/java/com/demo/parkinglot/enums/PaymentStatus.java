package com.demo.parkinglot.enums;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    FAILED("Failed"),
    REFUNDED("Refunded"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static PaymentStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment status cannot be null or empty");
        }
        
        try {
            return PaymentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment status: " + status + 
                ". Valid statuses are: " + getValidStatuses());
        }
    }
    
    public static String getValidStatuses() {
        StringBuilder sb = new StringBuilder();
        for (PaymentStatus status : PaymentStatus.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(status.name());
        }
        return sb.toString();
    }
}
