package com.demo.parkinglot.strategy;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Level-wise slot allocation strategy - prioritizes lower floors
 */
public class LevelWiseStrategy implements SlotAllocationStrategy {
    
    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate) {
        if (availableSlots == null || availableSlots.isEmpty()) {
            throw new IllegalArgumentException("No available slots");
        }
        
        // Sort by floor (ascending) and then by slot ID
        List<ParkingSlot> sortedSlots = availableSlots.stream()
                .sorted((s1, s2) -> {
                    int floorCompare = Integer.compare(s1.getFloor(), s2.getFloor());
                    if (floorCompare != 0) {
                        return floorCompare;
                    }
                    return Long.compare(s1.getId(), s2.getId());
                })
                .toList();
        
        return sortedSlots.getFirst();
    }
    
    @Override
    public String getStrategyName() {
        return "LEVEL_WISE";
    }
}
