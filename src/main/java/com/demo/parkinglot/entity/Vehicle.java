package com.demo.parkinglot.entity;

import com.demo.parkinglot.enums.VehicleType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    private String plateNo;
    
    @Enumerated(EnumType.STRING)
    private VehicleType type;
    
    private String ownerId;

    // Constructors, Getters, Setters
    public Vehicle() {}

    public Vehicle(String plateNo, VehicleType type, String ownerId) {
        this.plateNo = plateNo;
        this.type = type;
        this.ownerId = ownerId;
    }
    
    public Vehicle(String plateNo, String typeString, String ownerId) {
        this.plateNo = plateNo;
        this.type = VehicleType.fromString(typeString);
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlateNo() { return plateNo; }
    public void setPlateNo(String plateNo) { this.plateNo = plateNo; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }
    
    public void setType(String typeString) { 
        this.type = VehicleType.fromString(typeString); 
    }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
}
