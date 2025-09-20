package com.demo.parkinglot.util;

import com.demo.parkinglot.constants.ParkingConstants;
import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;
import com.demo.parkinglot.enums.VehicleType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ParkingUtility {
    
    private ParkingUtility() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Finds the nearest parking slot to the entry gate
     */
    public static ParkingSlot findNearestSlot(List<ParkingSlot> slots, EntryGate entryGate) {
        if (slots == null || slots.isEmpty()) {
            throw new IllegalArgumentException("Slots list cannot be null or empty");
        }
        
        if (entryGate == null) {
            throw new IllegalArgumentException("Entry gate cannot be null");
        }
        
        return slots.stream()
                .min((slot1, slot2) -> {
                    double distance1 = calculateDistance(entryGate, slot1);
                    double distance2 = calculateDistance(entryGate, slot2);
                    return Double.compare(distance1, distance2);
                })
                .orElse(slots.get(0));
    }
    
    /**
     * Calculates Euclidean distance between entry gate and parking slot
     */
    public static double calculateDistance(EntryGate gate, ParkingSlot slot) {
        if (gate == null || slot == null) {
            throw new IllegalArgumentException("Gate and slot cannot be null");
        }
        
        double deltaX = gate.getXCoordinate() - slot.getXCoordinate();
        double deltaY = gate.getYCoordinate() - slot.getYCoordinate();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
    /**
     * Gets hourly rate for vehicle type with fallback to default
     */
    public static double getHourlyRate(VehicleType vehicleType, double customRate) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        
        return customRate > 0 ? customRate : vehicleType.getDefaultHourlyRate();
    }
    
    /**
     * Calculates parking charge based on duration and hourly rate
     */
    public static double calculateParkingCharge(long durationInMinutes, double hourlyRate) {
        if (durationInMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative");
        }
        
        // Round up to the next hour for billing
        long hours = (durationInMinutes + ParkingConstants.ROUNDING_OFFSET) / ParkingConstants.MINUTES_PER_HOUR;
        return hours * hourlyRate;
    }
    
    /**
     * Calculates duration in minutes between two timestamps
     */
    public static long calculateDurationInMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        
        return ChronoUnit.MINUTES.between(startTime, endTime);
    }
    
    /**
     * Generates a unique receipt number
     */
    public static String generateReceiptNumber() {
        return ParkingConstants.RECEIPT_PREFIX + "-" + System.currentTimeMillis();
    }
    
    /**
     * Validates payment amount against calculated amount
     */
    public static boolean isPaymentAmountValid(double paidAmount, double calculatedAmount) {
        return Math.abs(paidAmount - calculatedAmount) <= ParkingConstants.PAYMENT_TOLERANCE;
    }
    
    /**
     * Validates vehicle type string and returns VehicleType enum
     */
    public static VehicleType validateAndParseVehicleType(String vehicleTypeString) {
        return VehicleType.fromString(vehicleTypeString);
    }
}
