package com.demo.parkinglot.util;

import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.constants.ParkingConstants;

public class PaymentValidator {
    
    private PaymentValidator() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validate pricing hierarchy: Bike < Car < Truck
     */
    public static boolean validatePricingHierarchy() {
        double bikeRate = VehicleType.BIKE.getDefaultHourlyRate();
        double carRate = VehicleType.CAR.getDefaultHourlyRate();
        double truckRate = VehicleType.TRUCK.getDefaultHourlyRate();
        
        return bikeRate < carRate && carRate < truckRate;
    }
    
    /**
     * Validate payment amount against calculated amount
     */
    public static boolean validatePaymentAmount(double paidAmount, double calculatedAmount) {
        if (paidAmount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
        }
        
        if (calculatedAmount < 0) {
            throw new IllegalArgumentException("Calculated amount cannot be negative");
        }
        
        return Math.abs(paidAmount - calculatedAmount) <= ParkingConstants.PAYMENT_TOLERANCE;
    }
    
    /**
     * Get pricing information for all vehicle types
     */
    public static String getPricingInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parking Rates (per hour):\n");
        
        for (VehicleType type : VehicleType.values()) {
            sb.append(String.format("- %s: $%.2f\n", type.getDisplayName(), type.getDefaultHourlyRate()));
        }
        
        // Validate hierarchy
        if (validatePricingHierarchy()) {
            sb.append("\n✓ Pricing hierarchy is correct: Bike < Car < Truck");
        } else {
            sb.append("\n✗ Pricing hierarchy is incorrect!");
        }
        
        return sb.toString();
    }
    
    /**
     * Calculate expected payment for given duration and vehicle type
     */
    public static double calculateExpectedPayment(VehicleType vehicleType, long durationInMinutes) {
        double hourlyRate = vehicleType.getDefaultHourlyRate();
        return ParkingUtility.calculateParkingCharge(durationInMinutes, hourlyRate);
    }
}
