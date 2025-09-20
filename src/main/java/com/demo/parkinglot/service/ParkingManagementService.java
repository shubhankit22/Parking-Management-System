package com.demo.parkinglot.service;

import com.demo.parkinglot.dto.ExitResponse;
import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.repository.*;
import com.demo.parkinglot.util.ParkingUtility;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.config.ParkingChargesConfig;
import com.demo.parkinglot.constants.ParkingConstants;
import com.demo.parkinglot.exception.SlotAllocationException;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParkingManagementService {
    
    @Autowired
    private ParkingSlotRepository slotRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private EntryGateRepository entryGateRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private FloorRepository floorRepository;
    
    @Autowired
    private ParkingLotRepository parkingLotRepository;
    
    @Autowired
    private ParkingChargesConfig parkingChargesConfig;
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * Park a vehicle with concurrency-safe slot allocation
     */
    @Transactional
    public Ticket parkVehicle(String plateNo, String type, String ownerId, Long entryGateId) {
        // Validate and parse vehicle type
        VehicleType vehicleType = ParkingUtility.validateAndParseVehicleType(type);

        // Check if vehicle is already parked
        Vehicle vehicle = vehicleRepository.findByPlateNo(plateNo)
                .orElseGet(() -> vehicleRepository.save(new Vehicle(plateNo, vehicleType, ownerId)));

        if (ticketRepository.findByVehicleAndActiveTrue(vehicle).isPresent()) {
            throw new SlotAllocationException("Vehicle with plate number " + plateNo + " is already parked");
        }

        // Validate entry gate
        EntryGate entryGate = entryGateRepository.findById(entryGateId)
                .orElseThrow(() -> new IllegalArgumentException(ParkingConstants.INVALID_ENTRY_GATE));

        // Get parking lot from entry gate
        ParkingLot parkingLot = entryGate.getParkingLot();
        if (parkingLot == null || !parkingLot.isActive()) {
            throw new IllegalStateException("Parking lot is not available");
        }

        // Check if parking lot is full
        if (isParkingLotFull(parkingLot, vehicleType)) {
            throw new SlotAllocationException("Parking lot is full for vehicle type: " + vehicleType.getDisplayName());
        }

        // Find and allocate slot with concurrency safety
        ParkingSlot allocatedSlot = allocateSlotSafely(parkingLot, vehicleType, entryGate);
        
        // Create ticket
        Ticket ticket = new Ticket(vehicle, allocatedSlot, entryGate, LocalDateTime.now(), true);
        return ticketRepository.save(ticket);
    }
    
    /**
     * Safely allocate a slot with concurrency protection
     */
    private ParkingSlot allocateSlotSafely(ParkingLot parkingLot, VehicleType vehicleType, EntryGate entryGate) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // Get available slots for the specific parking lot and vehicle type
                List<ParkingSlot> availableSlots = slotRepository.findAvailableSlotsByTypeAndParkingLot(vehicleType, parkingLot);
                
                if (availableSlots.isEmpty()) {
                    throw new SlotAllocationException("No available slots for vehicle type: " + vehicleType.getDisplayName());
                }
                
                // Find the nearest slot to the entry gate
                ParkingSlot nearestSlot = ParkingUtility.findNearestSlot(availableSlots, entryGate);
                
                // Attempt to allocate the slot atomically
                int updatedRows = slotRepository.allocateSlot(nearestSlot.getId());
                
                if (updatedRows == 1) {
                    // Successfully allocated, update floor availability
                    updateFloorAvailability(nearestSlot.getFloor(), -1);
                    // Refresh the slot to get updated availability status
                    nearestSlot.setAvailable(false);
                    return nearestSlot;
                } else {
                    // Slot was already allocated by another transaction
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw new SlotAllocationException("Unable to allocate slot after " + maxRetries + " attempts");
                    }
                    // Small delay before retry
                    Thread.sleep(50);
                }
                
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new IllegalStateException("Concurrent modification detected. Please try again.");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Allocation interrupted");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Allocation interrupted");
            }
        }
        
        throw new SlotAllocationException("Unable to allocate slot after maximum retries");
    }
    
    /**
     * Check if parking lot is full for a specific vehicle type
     */
    private boolean isParkingLotFull(ParkingLot parkingLot, VehicleType vehicleType) {
        long availableSlots = slotRepository.countAvailableSlotsByTypeAndParkingLot(parkingLot, vehicleType);
        return availableSlots == 0;
    }
    
    /**
     * Get parking lot status with floor-wise availability
     */
    public Map<String, Object> getParkingLotStatus(Long parkingLotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot not found"));
        
        Map<String, Object> status = new java.util.HashMap<>();
        status.put("parkingLotId", parkingLotId);
        status.put("parkingLotName", parkingLot.getName());
        status.put("totalFloors", parkingLot.getTotalFloors());
        status.put("isActive", parkingLot.isActive());
        
        // Get floor-wise availability
        Map<Integer, Integer> floorCapacities = parkingLot.getFloorCapacities();
        Map<Integer, Integer> availableByFloor = parkingLot.getAvailableSlotsByFloor();
        
        Map<String, Object> floorStatus = new java.util.HashMap<>();
        for (int floor = 1; floor <= parkingLot.getTotalFloors(); floor++) {
            Map<String, Object> floorInfo = new java.util.HashMap<>();
            int totalSlots = floorCapacities.getOrDefault(floor, 0);
            int availableSlots = availableByFloor.getOrDefault(floor, 0);
            int occupiedSlots = totalSlots - availableSlots;
            
            floorInfo.put("totalSlots", totalSlots);
            floorInfo.put("availableSlots", availableSlots);
            floorInfo.put("occupiedSlots", occupiedSlots);
            floorInfo.put("isFull", availableSlots == 0 && totalSlots > 0);
            floorInfo.put("occupancyRate", totalSlots > 0 ? (double) occupiedSlots / totalSlots * 100 : 0.0);
            
            floorStatus.put("Floor " + floor, floorInfo);
        }
        
        status.put("floorStatus", floorStatus);
        
        // Overall availability by vehicle type
        Map<String, Long> availabilityByType = new java.util.HashMap<>();
        for (VehicleType type : VehicleType.values()) {
            long count = slotRepository.countAvailableSlotsByTypeAndParkingLot(parkingLot, type);
            availabilityByType.put(type.getDisplayName(), count);
        }
        status.put("availabilityByVehicleType", availabilityByType);
        
        return status;
    }
    
    /**
     * Update floor availability count
     */
    private void updateFloorAvailability(int floorNumber, int change) {
        // This would typically update the Floor entity's availableSlots count
        // For now, we'll implement a simple approach
        // In a real implementation, you might want to use a more sophisticated approach
    }
    
    /**
     * Unpark vehicle with atomic payment processing
     */
    @Transactional
    public ExitResponse unparkVehicle(Long ticketId, double amount) {
        return paymentService.processPayment(ticketId, amount);
    }
    
    /**
     * Retry failed payment
     */
    @Transactional
    public ExitResponse retryPayment(Long ticketId, double amount) {
        return paymentService.retryPayment(ticketId, amount);
    }
}
