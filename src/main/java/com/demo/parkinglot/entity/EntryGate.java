package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class EntryGate {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String gateName;
    private int gateNumber;
    private double xCoordinate;
    private double yCoordinate;
    
    @ManyToOne
    private ParkingLot parkingLot;
    
    // Constructors
    public EntryGate() {}
    
    public EntryGate(String gateName, int gateNumber, double xCoordinate, double yCoordinate, ParkingLot parkingLot) {
        this.gateName = gateName;
        this.gateNumber = gateNumber;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.parkingLot = parkingLot;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getGateName() { return gateName; }
    public void setGateName(String gateName) { this.gateName = gateName; }
    
    public int getGateNumber() { return gateNumber; }
    public void setGateNumber(int gateNumber) { this.gateNumber = gateNumber; }
    
    public double getXCoordinate() { return xCoordinate; }
    public void setXCoordinate(double xCoordinate) { this.xCoordinate = xCoordinate; }
    
    public double getYCoordinate() { return yCoordinate; }
    public void setYCoordinate(double yCoordinate) { this.yCoordinate = yCoordinate; }
    
    public ParkingLot getParkingLot() { return parkingLot; }
    public void setParkingLot(ParkingLot parkingLot) { this.parkingLot = parkingLot; }
}
