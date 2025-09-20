package com.demo.parkinglot.service;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;
import com.demo.parkinglot.enums.AllocationStrategyType;
import com.demo.parkinglot.strategy.SlotAllocationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service that uses Strategy Pattern with Enum for slot allocation
 */
@Service
public class SlotAllocationService {

    @Value("${parking.allocation.strategy:NEAREST_SLOT}")
    private String allocationStrategy;

    /**
     * Allocate slot using the configured strategy
     */
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate) {
        AllocationStrategyType strategyType = AllocationStrategyType.fromString(allocationStrategy);
        SlotAllocationStrategy strategy = strategyType.createStrategy();
        return strategy.allocateSlot(availableSlots, entryGate);
    }

    /**
     * Allocate slot using a specific strategy by enum
     */
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate, AllocationStrategyType strategyType) {
        SlotAllocationStrategy strategy = strategyType.createStrategy();
        return strategy.allocateSlot(availableSlots, entryGate);
    }

    /**
     * Allocate slot using a specific strategy by string name
     */
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate, String strategyName) {
        AllocationStrategyType strategyType = AllocationStrategyType.fromString(strategyName);
        return allocateSlot(availableSlots, entryGate, strategyType);
    }

    /**
     * Get all available strategy types
     */
    public AllocationStrategyType[] getAvailableStrategies() {
        return AllocationStrategyType.getAllStrategies();
    }

    /**
     * Get strategy information as map
     */
    public Map<String, Object> getStrategyInfo(AllocationStrategyType strategyType) {
        return Map.of(
            "name", strategyType.name(),
            "displayName", strategyType.getDisplayName(),
            "description", strategyType.getDescription()
        );
    }

    /**
     * Get all strategies information
     */
    public List<Map<String, Object>> getAllStrategiesInfo() {
        return Arrays.stream(AllocationStrategyType.getAllStrategies())
                .map(this::getStrategyInfo)
                .collect(Collectors.toList());
    }

    /**
     * Validate if strategy name is valid
     */
    public boolean isValidStrategy(String strategyName) {
        return AllocationStrategyType.isValidStrategy(strategyName);
    }

    /**
     * Get current configured strategy
     */
    public AllocationStrategyType getCurrentStrategy() {
        return AllocationStrategyType.fromString(allocationStrategy);
    }

    /**
     * Get strategy names as string array (for backward compatibility)
     */
    public String[] getAvailableStrategyNames() {
        return Arrays.stream(AllocationStrategyType.getAllStrategies())
                .map(Enum::name)
                .toArray(String[]::new);
    }
}
