package com.demo.parkinglot.strategy;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;

import java.util.List;

/**
 * Strategy interface for slot allocation algorithms
 */
public interface SlotAllocationStrategy {
    
    /**
     * Allocate a slot from the available slots based on the strategy
     * @param availableSlots List of available slots
     * @param entryGate Entry gate for context
     * @return Selected parking slot
     */
    ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate);
    
    /**
     * Get strategy name
     * @return Strategy name
     */
    String getStrategyName();
}
