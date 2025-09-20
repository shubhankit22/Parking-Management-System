package com.demo.parkinglot.dto;

import com.demo.parkinglot.enums.VehicleType;

public class SlotManagementRequest {
    
    private Long parkingLotId;
    private int floor;
    private VehicleType slotType;
    private String slotNumber;
    private double xCoordinate;
    private double yCoordinate;
    private String action; // ADD, REMOVE, UPDATE
    
    // Constructors
    public SlotManagementRequest() {}
    
    public SlotManagementRequest(Long parkingLotId, int floor, VehicleType slotType, String slotNumber, 
                               double xCoordinate, double yCoordinate, String action) {
        this.parkingLotId = parkingLotId;
        this.floor = floor;
        this.slotType = slotType;
        this.slotNumber = slotNumber;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.action = action;
    }
    
    // Getters and Setters
    public Long getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(Long parkingLotId) { this.parkingLotId = parkingLotId; }
    
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    
    public VehicleType getSlotType() { return slotType; }
    public void setSlotType(VehicleType slotType) { this.slotType = slotType; }
    
    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
    
    public double getXCoordinate() { return xCoordinate; }
    public void setXCoordinate(double xCoordinate) { this.xCoordinate = xCoordinate; }
    
    public double getYCoordinate() { return yCoordinate; }
    public void setYCoordinate(double yCoordinate) { this.yCoordinate = yCoordinate; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
