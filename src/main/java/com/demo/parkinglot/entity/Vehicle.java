package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    private String plateNo;
    private String type; // CAR, BIKE, TRUCK
    private String ownerId;

    // Constructors, Getters, Setters
    public Vehicle() {}

    public Vehicle(String plateNo, String type, String ownerId) {
        this.plateNo = plateNo;
        this.type = type;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlateNo() { return plateNo; }
    public void setPlateNo(String plateNo) { this.plateNo = plateNo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
}
