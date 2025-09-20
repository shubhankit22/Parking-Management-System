package com.demo.parkinglot.strategy;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;

import java.util.List;

/**
 * First available slot allocation strategy - assigns the first available slot
 */
public class FirstAvailableStrategy implements SlotAllocationStrategy {
    
    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate) {
        if (availableSlots == null || availableSlots.isEmpty()) {
            throw new IllegalArgumentException("No available slots");
        }
        return availableSlots.get(0);
    }
    
    @Override
    public String getStrategyName() {
        return "FIRST_AVAILABLE";
    }
}
