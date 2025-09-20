package com.demo.parkinglot.service;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;
import com.demo.parkinglot.enums.AllocationStrategyType;
import com.demo.parkinglot.strategy.SlotAllocationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manager-level service for slot allocation operations
 * Provides clean interface and encapsulates complex allocation logic
 */
@Service
public class SlotAllocationManager {

    private final SlotAllocationService slotAllocationService;

    @Autowired
    public SlotAllocationManager(SlotAllocationService slotAllocationService) {
        this.slotAllocationService = slotAllocationService;
    }

    /**
     * Allocate optimal slot using configured strategy
     * 
     * @param availableSlots List of available slots
     * @param entryGate Entry gate for context
     * @return Allocated parking slot
     * @throws IllegalArgumentException if no slots available or invalid parameters
     * @throws IllegalStateException if allocation fails
     */
    public ParkingSlot allocateOptimalSlot(List<ParkingSlot> availableSlots, EntryGate entryGate) {
        validateAllocationParameters(availableSlots, entryGate);
        return slotAllocationService.allocateSlot(availableSlots, entryGate);
    }

    /**
     * Allocate slot using specific strategy
     * 
     * @param availableSlots List of available slots
     * @param entryGate Entry gate for context
     * @param strategyType Specific allocation strategy
     * @return Allocated parking slot
     * @throws IllegalArgumentException if parameters invalid
     * @throws IllegalStateException if allocation fails
     */
    public ParkingSlot allocateSlotWithStrategy(List<ParkingSlot> availableSlots, EntryGate entryGate, 
                                               AllocationStrategyType strategyType) {
        validateAllocationParameters(availableSlots, entryGate);
        return slotAllocationService.allocateSlot(availableSlots, entryGate, strategyType);
    }

    /**
     * Get current allocation strategy
     * 
     * @return Current strategy type
     */
    public AllocationStrategyType getCurrentStrategy() {
        return slotAllocationService.getCurrentStrategy();
    }

    /**
     * Check if strategy is valid
     * 
     * @param strategyName Strategy name to validate
     * @return true if valid, false otherwise
     */
    public boolean isStrategyValid(String strategyName) {
        return slotAllocationService.isValidStrategy(strategyName);
    }

    /**
     * Get all available strategies
     * 
     * @return Array of available strategy types
     */
    public AllocationStrategyType[] getAvailableStrategies() {
        return slotAllocationService.getAvailableStrategies();
    }

    /**
     * Validate allocation parameters
     * 
     * @param availableSlots Slots to validate
     * @param entryGate Entry gate to validate
     * @throws IllegalArgumentException if parameters are invalid
     */
    private void validateAllocationParameters(List<ParkingSlot> availableSlots, EntryGate entryGate) {
        if (availableSlots == null || availableSlots.isEmpty()) {
            throw new IllegalArgumentException("Available slots list cannot be null or empty");
        }
        
        if (entryGate == null) {
            throw new IllegalArgumentException("Entry gate cannot be null");
        }
        
        // Validate that all slots are actually available
        long unavailableSlots = availableSlots.stream()
                .filter(slot -> !slot.isAvailable())
                .count();
        
        if (unavailableSlots > 0) {
            throw new IllegalArgumentException("Found " + unavailableSlots + " unavailable slots in the provided list");
        }
    }
}
