package com.demo.parkinglot.entity;

import com.demo.parkinglot.enums.VehicleType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.Version;

@Entity
public class ParkingSlot {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private VehicleType slotType;
    private int floor;
    private boolean available;
    private double xCoordinate;
    private double yCoordinate;
    private String slotNumber;

    @ManyToOne
    private ParkingLot parkingLot;
    
    @ManyToOne
    private Floor floorEntity;
    
    @Version
    @Column(name = "version")
    private Long version;

    // Constructors, Getters, Setters
    public ParkingSlot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VehicleType getSlotType() { return slotType; }
    public void setSlotType(VehicleType slotType) { this.slotType = slotType; }

    public void setSlotType(String slotTypeString) {
        this.slotType = VehicleType.fromString(slotTypeString);
    }

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

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public Floor getFloorEntity() { return floorEntity; }
    public void setFloorEntity(Floor floorEntity) { this.floorEntity = floorEntity; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
