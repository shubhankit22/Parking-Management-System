package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ParkingSlot {

    @Id
    @GeneratedValue
    private Long id;

    private String slotType; // e.g. CAR, BIKE, TRUCK
    private int floor;
    private boolean available;
    private double xCoordinate;
    private double yCoordinate;

    @ManyToOne
    private ParkingLot parkingLot;

    // Constructors, Getters, Setters
    public ParkingSlot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public ParkingLot getParkingLot() { return parkingLot; }
    public void setParkingLot(ParkingLot parkingLot) { this.parkingLot = parkingLot; }
    
    public double getXCoordinate() { return xCoordinate; }
    public void setXCoordinate(double xCoordinate) { this.xCoordinate = xCoordinate; }
    
    public double getYCoordinate() { return yCoordinate; }
    public void setYCoordinate(double yCoordinate) { this.yCoordinate = yCoordinate; }
}
