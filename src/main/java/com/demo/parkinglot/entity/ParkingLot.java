package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ParkingLot {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String location;

    // Constructors, Getters, Setters
    public ParkingLot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
