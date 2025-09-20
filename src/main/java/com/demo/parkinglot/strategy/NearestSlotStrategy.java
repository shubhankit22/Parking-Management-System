package com.demo.parkinglot.strategy;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingSlot;
import com.demo.parkinglot.util.ParkingUtility;

import java.util.List;

/**
 * Nearest slot allocation strategy - assigns the closest available slot to entry gate
 */
public class NearestSlotStrategy implements SlotAllocationStrategy {
    
    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, EntryGate entryGate) {
        return ParkingUtility.findNearestSlot(availableSlots, entryGate);
    }
    
    @Override
    public String getStrategyName() {
        return "NEAREST_SLOT";
    }
}
