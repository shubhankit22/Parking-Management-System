package com.demo.parkinglot.service;

import com.demo.parkinglot.dto.*;
import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.repository.*;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.config.ParkingChargesConfig;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {
    
    @Autowired
    private ParkingChargesConfig parkingChargesConfig;
    
    @Autowired
    private ParkingLotRepository parkingLotRepository;
    
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    
    @Autowired
    private FloorRepository floorRepository;
    
    @Autowired
    private EntryGateRepository entryGateRepository;
    
    /**
     * Update pricing rules for vehicle types
     */
    @Transactional
    public AdminResponse updatePricingRules(PricingRuleRequest request) {
        try {
            // Validate pricing hierarchy
            if (!validatePricingHierarchy(request)) {
                return new AdminResponse(false, "Invalid pricing: Bike rate must be less than Car rate, and Car rate must be less than Truck rate");
            }
            
            // Update pricing configuration
            Map<String, Double> hourlyRates = parkingChargesConfig.getHourlyRates();
            hourlyRates.put(request.getVehicleType().name(), request.getHourlyRate());
            parkingChargesConfig.setHourlyRates(hourlyRates);
            
            Map<String, Object> data = new HashMap<>();
            data.put("vehicleType", request.getVehicleType().getDisplayName());
            data.put("hourlyRate", request.getHourlyRate());
            data.put("description", request.getDescription());
            data.put("isActive", request.isActive());
            
            return new AdminResponse(true, "Pricing rule updated successfully", data);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to update pricing rule: " + e.getMessage());
        }
    }
    
    /**
     * Get current pricing rules
     */
    public AdminResponse getPricingRules() {
        try {
            Map<String, Object> pricingData = new HashMap<>();
            Map<String, Double> hourlyRates = parkingChargesConfig.getHourlyRates();
            
            for (VehicleType type : VehicleType.values()) {
                Map<String, Object> typeData = new HashMap<>();
                typeData.put("hourlyRate", hourlyRates.getOrDefault(type.name(), type.getDefaultHourlyRate()));
                typeData.put("displayName", type.getDisplayName());
                typeData.put("isActive", true);
                pricingData.put(type.name(), typeData);
            }
            
            return new AdminResponse(true, "Pricing rules retrieved successfully", pricingData);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to retrieve pricing rules: " + e.getMessage());
        }
    }
    
    /**
     * Add new parking slot
     */
    @Transactional
    public AdminResponse addParkingSlot(SlotManagementRequest request) {
        try {
            ParkingLot parkingLot = parkingLotRepository.findById(request.getParkingLotId())
                    .orElseThrow(() -> new IllegalArgumentException("Parking lot not found"));
            
            // Create new parking slot
            ParkingSlot newSlot = new ParkingSlot();
            newSlot.setSlotType(request.getSlotType());
            newSlot.setFloor(request.getFloor());
            newSlot.setAvailable(true);
            newSlot.setXCoordinate(request.getXCoordinate());
            newSlot.setYCoordinate(request.getYCoordinate());
            newSlot.setSlotNumber(request.getSlotNumber());
            newSlot.setParkingLot(parkingLot);
            
            // Find or create floor entity
            Floor floor = findOrCreateFloor(parkingLot, request.getFloor());
            newSlot.setFloorEntity(floor);
            
            ParkingSlot savedSlot = parkingSlotRepository.save(newSlot);
            
            // Update floor capacity
            floor.setTotalSlots(floor.getTotalSlots() + 1);
            floor.setAvailableSlots(floor.getAvailableSlots() + 1);
            floorRepository.save(floor);
            
            Map<String, Object> data = new HashMap<>();
            data.put("slotId", savedSlot.getId());
            data.put("slotNumber", savedSlot.getSlotNumber());
            data.put("floor", savedSlot.getFloor());
            data.put("slotType", savedSlot.getSlotType().getDisplayName());
            data.put("coordinates", Map.of("x", savedSlot.getXCoordinate(), "y", savedSlot.getYCoordinate()));
            
            return new AdminResponse(true, "Parking slot added successfully", data);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to add parking slot: " + e.getMessage());
        }
    }
    
    /**
     * Remove parking slot
     */
    @Transactional
    public AdminResponse removeParkingSlot(Long slotId) {
        try {
            ParkingSlot slot = parkingSlotRepository.findById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("Parking slot not found"));
            
            // Check if slot is currently occupied
            if (!slot.isAvailable()) {
                return new AdminResponse(false, "Cannot remove occupied parking slot. Please wait until vehicle exits.");
            }
            
            // Update floor capacity
            Floor floor = slot.getFloorEntity();
            if (floor != null) {
                floor.setTotalSlots(floor.getTotalSlots() - 1);
                floor.setAvailableSlots(floor.getAvailableSlots() - 1);
                floorRepository.save(floor);
            }
            
            // Remove slot
            parkingSlotRepository.delete(slot);
            
            Map<String, Object> data = new HashMap<>();
            data.put("removedSlotId", slotId);
            data.put("slotNumber", slot.getSlotNumber());
            data.put("floor", slot.getFloor());
            
            return new AdminResponse(true, "Parking slot removed successfully", data);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to remove parking slot: " + e.getMessage());
        }
    }
    
    /**
     * Update parking slot
     */
    @Transactional
    public AdminResponse updateParkingSlot(Long slotId, SlotManagementRequest request) {
        try {
            ParkingSlot slot = parkingSlotRepository.findById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("Parking slot not found"));
            
            // Update slot properties
            if (request.getSlotType() != null) {
                slot.setSlotType(request.getSlotType());
            }
            if (request.getSlotNumber() != null) {
                slot.setSlotNumber(request.getSlotNumber());
            }
            if (request.getXCoordinate() != 0) {
                slot.setXCoordinate(request.getXCoordinate());
            }
            if (request.getYCoordinate() != 0) {
                slot.setYCoordinate(request.getYCoordinate());
            }
            
            ParkingSlot updatedSlot = parkingSlotRepository.save(slot);
            
            Map<String, Object> data = new HashMap<>();
            data.put("slotId", updatedSlot.getId());
            data.put("slotNumber", updatedSlot.getSlotNumber());
            data.put("floor", updatedSlot.getFloor());
            data.put("slotType", updatedSlot.getSlotType().getDisplayName());
            data.put("coordinates", Map.of("x", updatedSlot.getXCoordinate(), "y", updatedSlot.getYCoordinate()));
            
            return new AdminResponse(true, "Parking slot updated successfully", data);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to update parking slot: " + e.getMessage());
        }
    }
    
    /**
     * Get parking lot management overview
     */
    public AdminResponse getParkingLotOverview(Long parkingLotId) {
        try {
            ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                    .orElseThrow(() -> new IllegalArgumentException("Parking lot not found"));
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("parkingLotId", parkingLot.getId());
            overview.put("parkingLotName", parkingLot.getName());
            overview.put("location", parkingLot.getLocation());
            overview.put("totalFloors", parkingLot.getTotalFloors());
            overview.put("isActive", parkingLot.isActive());
            
            // Get slot statistics
            Map<String, Long> slotStats = new HashMap<>();
            for (VehicleType type : VehicleType.values()) {
                long totalSlots = parkingSlotRepository.countByParkingLotAndSlotType(parkingLot, type);
                long availableSlots = parkingSlotRepository.countAvailableSlotsByTypeAndParkingLot(parkingLot, type);
                long occupiedSlots = totalSlots - availableSlots;
                
                Map<String, Long> typeStats = new HashMap<>();
                typeStats.put("total", totalSlots);
                typeStats.put("available", availableSlots);
                typeStats.put("occupied", occupiedSlots);
                slotStats.put(type.getDisplayName(), typeStats);
            }
            overview.put("slotStatistics", slotStats);
            
            // Get floor-wise statistics
            Map<String, Object> floorStats = new HashMap<>();
            for (int floor = 1; floor <= parkingLot.getTotalFloors(); floor++) {
                Map<String, Object> floorInfo = new HashMap<>();
                List<ParkingSlot> floorSlots = parkingSlotRepository.findByParkingLotAndFloorAndSlotType(parkingLot, floor, null);
                long totalSlots = floorSlots.size();
                long availableSlots = floorSlots.stream().mapToLong(s -> s.isAvailable() ? 1 : 0).sum();
                long occupiedSlots = totalSlots - availableSlots;
                
                floorInfo.put("totalSlots", totalSlots);
                floorInfo.put("availableSlots", availableSlots);
                floorInfo.put("occupiedSlots", occupiedSlots);
                floorInfo.put("occupancyRate", totalSlots > 0 ? (double) occupiedSlots / totalSlots * 100 : 0.0);
                
                floorStats.put("Floor " + floor, floorInfo);
            }
            overview.put("floorStatistics", floorStats);
            
            return new AdminResponse(true, "Parking lot overview retrieved successfully", overview);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to retrieve parking lot overview: " + e.getMessage());
        }
    }
    
    /**
     * Validate pricing hierarchy
     */
    private boolean validatePricingHierarchy(PricingRuleRequest request) {
        Map<String, Double> currentRates = parkingChargesConfig.getHourlyRates();
        
        double bikeRate = currentRates.getOrDefault(VehicleType.BIKE.name(), VehicleType.BIKE.getDefaultHourlyRate());
        double carRate = currentRates.getOrDefault(VehicleType.CAR.name(), VehicleType.CAR.getDefaultHourlyRate());
        double truckRate = currentRates.getOrDefault(VehicleType.TRUCK.name(), VehicleType.TRUCK.getDefaultHourlyRate());
        
        // Update the rate being changed
        if (request.getVehicleType() == VehicleType.BIKE) {
            bikeRate = request.getHourlyRate();
        } else if (request.getVehicleType() == VehicleType.CAR) {
            carRate = request.getHourlyRate();
        } else if (request.getVehicleType() == VehicleType.TRUCK) {
            truckRate = request.getHourlyRate();
        }
        
        return bikeRate < carRate && carRate < truckRate;
    }
    
    /**
     * Add multiple parking slots in bulk
     */
    @Transactional
    public AdminResponse addBulkParkingSlots(List<SlotManagementRequest> requests) {
        try {
            int successCount = 0;
            int failureCount = 0;
            StringBuilder errors = new StringBuilder();
            
            for (SlotManagementRequest request : requests) {
                try {
                    AdminResponse result = addParkingSlot(request);
                    if (result.isSuccess()) {
                        successCount++;
                    } else {
                        failureCount++;
                        errors.append("Slot ").append(request.getSlotNumber()).append(": ").append(result.getMessage()).append("; ");
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.append("Slot ").append(request.getSlotNumber()).append(": ").append(e.getMessage()).append("; ");
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalRequested", requests.size());
            data.put("successCount", successCount);
            data.put("failureCount", failureCount);
            data.put("errors", errors.toString());
            
            String message = String.format("Bulk operation completed: %d successful, %d failed", successCount, failureCount);
            return new AdminResponse(failureCount == 0, message, data);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to process bulk slot addition: " + e.getMessage());
        }
    }
    
    /**
     * Get slot management statistics
     */
    public AdminResponse getSlotStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // Overall statistics
            long totalSlots = parkingSlotRepository.count();
            long availableSlots = parkingSlotRepository.countByAvailableTrue();
            long occupiedSlots = totalSlots - availableSlots;
            
            statistics.put("totalSlots", totalSlots);
            statistics.put("availableSlots", availableSlots);
            statistics.put("occupiedSlots", occupiedSlots);
            statistics.put("occupancyRate", totalSlots > 0 ? (double) occupiedSlots / totalSlots * 100 : 0.0);
            
            // Statistics by vehicle type
            Map<String, Object> typeStats = new HashMap<>();
            for (VehicleType type : VehicleType.values()) {
                Map<String, Object> typeData = new HashMap<>();
                long totalByType = parkingSlotRepository.countBySlotType(type);
                long availableByType = parkingSlotRepository.countBySlotTypeAndAvailableTrue(type);
                long occupiedByType = totalByType - availableByType;
                
                typeData.put("total", totalByType);
                typeData.put("available", availableByType);
                typeData.put("occupied", occupiedByType);
                typeData.put("occupancyRate", totalByType > 0 ? (double) occupiedByType / totalByType * 100 : 0.0);
                
                typeStats.put(type.getDisplayName(), typeData);
            }
            statistics.put("byVehicleType", typeStats);
            
            // Statistics by parking lot
            List<ParkingLot> parkingLots = parkingLotRepository.findAll();
            Map<String, Object> lotStats = new HashMap<>();
            for (ParkingLot lot : parkingLots) {
                Map<String, Object> lotData = new HashMap<>();
                long totalInLot = parkingSlotRepository.countByParkingLot(lot);
                long availableInLot = parkingSlotRepository.countByParkingLotAndAvailableTrue(lot);
                long occupiedInLot = totalInLot - availableInLot;
                
                lotData.put("total", totalInLot);
                lotData.put("available", availableInLot);
                lotData.put("occupied", occupiedInLot);
                lotData.put("occupancyRate", totalInLot > 0 ? (double) occupiedInLot / totalInLot * 100 : 0.0);
                
                lotStats.put(lot.getName(), lotData);
            }
            statistics.put("byParkingLot", lotStats);
            
            return new AdminResponse(true, "Slot statistics retrieved successfully", statistics);
            
        } catch (Exception e) {
            return new AdminResponse(false, "Failed to retrieve slot statistics: " + e.getMessage());
        }
    }
    
    /**
     * Find or create floor entity
     */
    private Floor findOrCreateFloor(ParkingLot parkingLot, int floorNumber) {
        return floorRepository.findByParkingLotAndFloorNumber(parkingLot, floorNumber)
                .orElseGet(() -> {
                    Floor newFloor = new Floor(floorNumber, 0, "Floor " + floorNumber, parkingLot);
                    return floorRepository.save(newFloor);
                });
    }
}
