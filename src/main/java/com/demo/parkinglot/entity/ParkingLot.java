package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Entity
public class ParkingLot {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String location;
    private int totalFloors;
    private boolean isActive;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParkingSlot> parkingSlots;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntryGate> entryGates;

    // Constructors
    public ParkingLot() {}

    public ParkingLot(String name, String location, int totalFloors) {
        this.name = name;
        this.location = location;
        this.totalFloors = totalFloors;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getTotalFloors() { return totalFloors; }
    public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<ParkingSlot> getParkingSlots() { return parkingSlots; }
    public void setParkingSlots(List<ParkingSlot> parkingSlots) { this.parkingSlots = parkingSlots; }

    public List<EntryGate> getEntryGates() { return entryGates; }
    public void setEntryGates(List<EntryGate> entryGates) { this.entryGates = entryGates; }

    // Helper methods for floor management
    public Map<Integer, Integer> getFloorCapacities() {
        Map<Integer, Integer> floorCapacities = new HashMap<>();
        if (parkingSlots != null) {
            for (ParkingSlot slot : parkingSlots) {
                floorCapacities.merge(slot.getFloor(), 1, Integer::sum);
            }
        }
        return floorCapacities;
    }

    public Map<Integer, Integer> getAvailableSlotsByFloor() {
        Map<Integer, Integer> availableByFloor = new HashMap<>();
        if (parkingSlots != null) {
            for (ParkingSlot slot : parkingSlots) {
                if (slot.isAvailable()) {
                    availableByFloor.merge(slot.getFloor(), 1, Integer::sum);
                }
            }
        }
        return availableByFloor;
    }
}
