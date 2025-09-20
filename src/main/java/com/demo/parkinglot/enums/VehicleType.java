package com.demo.parkinglot.enums;

public enum VehicleType {
    CAR("Car", 2.0),
    BIKE("Bike", 1.0),
    TRUCK("Truck", 5.0);
    
    private final String displayName;
    private final double defaultHourlyRate;
    
    VehicleType(String displayName, double defaultHourlyRate) {
        this.displayName = displayName;
        this.defaultHourlyRate = defaultHourlyRate;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getDefaultHourlyRate() {
        return defaultHourlyRate;
    }
    
    public static VehicleType fromString(String vehicleType) {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle type cannot be null or empty");
        }
        
        try {
            return VehicleType.valueOf(vehicleType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType + 
                ". Valid types are: " + getValidTypes());
        }
    }
    
    public static String getValidTypes() {
        StringBuilder sb = new StringBuilder();
        for (VehicleType type : VehicleType.values()) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(type.name());
        }
        return sb.toString();
    }
}
