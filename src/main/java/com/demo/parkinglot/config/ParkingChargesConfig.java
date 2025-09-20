package com.demo.parkinglot.config;

import com.demo.parkinglot.enums.VehicleType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "parking.charges")
public class ParkingChargesConfig {
    
    private Map<String, Double> hourlyRates = new HashMap<>();
    
    public ParkingChargesConfig() {
        // Set default rates
        hourlyRates.put(VehicleType.CAR.name(), VehicleType.CAR.getDefaultHourlyRate());
        hourlyRates.put(VehicleType.BIKE.name(), VehicleType.BIKE.getDefaultHourlyRate());
        hourlyRates.put(VehicleType.TRUCK.name(), VehicleType.TRUCK.getDefaultHourlyRate());
    }
    
    public Map<String, Double> getHourlyRates() {
        return hourlyRates;
    }
    
    public void setHourlyRates(Map<String, Double> hourlyRates) {
        this.hourlyRates = hourlyRates;
    }
    
    public double getHourlyRate(VehicleType vehicleType) {
        return hourlyRates.getOrDefault(vehicleType.name(), vehicleType.getDefaultHourlyRate());
    }
}
