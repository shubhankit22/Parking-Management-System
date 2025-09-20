package com.demo.parkinglot.exception;

/**
 * Exception for slot allocation failures
 */
public class SlotAllocationException extends ParkingManagementException {
    
    public SlotAllocationException(String message) {
        super("SLOT_ALLOCATION", "ALLOCATION", message);
    }
    
    public SlotAllocationException(String message, Throwable cause) {
        super("SLOT_ALLOCATION", "ALLOCATION", message, cause);
    }
    
    public SlotAllocationException(String context, String message) {
        super("SLOT_ALLOCATION", context, message);
    }
    
    public SlotAllocationException(String context, String message, Throwable cause) {
        super("SLOT_ALLOCATION", context, message, cause);
    }
}
