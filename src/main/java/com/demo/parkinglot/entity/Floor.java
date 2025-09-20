package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import java.util.List;

@Entity
public class Floor {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private int floorNumber;
    private int totalSlots;
    private int availableSlots;
    private String floorName;
    private boolean isActive;
    
    @ManyToOne
    private ParkingLot parkingLot;
    
    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParkingSlot> parkingSlots;
    
    // Constructors
    public Floor() {}
    
    public Floor(int floorNumber, int totalSlots, String floorName, ParkingLot parkingLot) {
        this.floorNumber = floorNumber;
        this.totalSlots = totalSlots;
        this.availableSlots = totalSlots;
        this.floorName = floorName;
        this.parkingLot = parkingLot;
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }
    
    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }
    
    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }
    
    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public ParkingLot getParkingLot() { return parkingLot; }
    public void setParkingLot(ParkingLot parkingLot) { this.parkingLot = parkingLot; }
    
    public List<ParkingSlot> getParkingSlots() { return parkingSlots; }
    public void setParkingSlots(List<ParkingSlot> parkingSlots) { this.parkingSlots = parkingSlots; }
    
    // Helper methods
    public boolean isFull() {
        return availableSlots <= 0;
    }
    
    public boolean hasAvailableSlots() {
        return availableSlots > 0;
    }
    
    public void decrementAvailableSlots() {
        if (availableSlots > 0) {
            availableSlots--;
        }
    }
    
    public void incrementAvailableSlots() {
        if (availableSlots < totalSlots) {
            availableSlots++;
        }
    }
    
    public double getOccupancyRate() {
        if (totalSlots == 0) return 0.0;
        return (double) (totalSlots - availableSlots) / totalSlots * 100;
    }
}
