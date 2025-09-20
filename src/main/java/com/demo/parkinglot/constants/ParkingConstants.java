package com.demo.parkinglot.constants;

public class ParkingConstants {
    
    // Receipt constants
    public static final String RECEIPT_PREFIX = "RCP";
    
    // Payment validation
    public static final double PAYMENT_TOLERANCE = 0.01;
    
    // Time calculation
    public static final int MINUTES_PER_HOUR = 60;
    public static final int ROUNDING_OFFSET = 60; // For rounding up to next hour
    
    // Error messages
    public static final String VEHICLE_ALREADY_PARKED = "Vehicle already parked";
    public static final String NO_AVAILABLE_SLOT = "No available slot for type: ";
    public static final String INVALID_TICKET_ID = "Invalid ticket ID";
    public static final String TICKET_INACTIVE = "Ticket is already inactive";
    public static final String INVALID_ENTRY_GATE = "Invalid entry gate ID";
    public static final String PAYMENT_AMOUNT_MISMATCH = "Payment amount does not match calculated amount";
    
    // Success messages
    public static final String VEHICLE_EXIT_SUCCESS = "Vehicle successfully exited. Payment processed and slot freed.";
    
    private ParkingConstants() {
        // Utility class - prevent instantiation
    }
}
