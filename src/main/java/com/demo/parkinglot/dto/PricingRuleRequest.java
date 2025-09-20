package com.demo.parkinglot.dto;

import com.demo.parkinglot.enums.VehicleType;

public class PricingRuleRequest {
    
    private VehicleType vehicleType;
    private double hourlyRate;
    private String description;
    private boolean isActive;
    
    // Constructors
    public PricingRuleRequest() {}
    
    public PricingRuleRequest(VehicleType vehicleType, double hourlyRate, String description, boolean isActive) {
        this.vehicleType = vehicleType;
        this.hourlyRate = hourlyRate;
        this.description = description;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
